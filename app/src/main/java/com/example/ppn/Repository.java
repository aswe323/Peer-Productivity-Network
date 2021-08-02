package com.example.ppn;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * <h4>a service class.</h3>
 * <p>main access to data and firestore operations. puts together critical compunents for ease of use.
 *</p>
 *
 * <p> {@link com.example.ppn.Repository#userName userName} should be set by the coder, otherwise "default" will be used as a collection across all devices.</p>
 * <p>  {@link com.example.ppn.Repository#defaultActivity defaultActivity } should be assign to a default activity to be return to when a notification is clicked/tapped</p>
 * <p> {@link com.example.ppn.Repository#defaultContext defaultContext} should be assign to pass a context which can be used for creating a notification. </p>
 
 * <p>  methods are structured in a c.r.u.d manner, and return a Task object. Task is an object representing an async operation.</p>
 *
 * <h4>refrence to Task and it's uses:</h4>
 * @see <a href="https://developer.android.com/reference/com/google/android/play/core/tasks/Task">Tasks Android Reference</a>
 * @see <a href="https://cloud.google.com/firestore/docs/query-data/get-data">Getting Data with Firestore</a>
 *
 *
 */
public class Repository {

    private static final String TAG = "Repository";
    private static boolean created = false;
    /**
     * indicates default context to be used when creating a new notification.
     */
    private static Context defaultContext;
    /**
     * indicates what activity should be returned to by default when notifications are clicked/tapped.
     */
    private static Activity defaultActivity;
    private static FirebaseUser user;
    private static FirebaseAuth firebaseAuth;


    private static Map<String, Integer> priorityWords = new HashMap<>();
    private static Map<String, TimePack> bucketWords = new HashMap<>();


    /**
     * effectively the collection name for the current user.
     */
    private static String userName;;
    private Class<ActivityTask> activityTaskClass;


    private static String setUserName(String userName) {
        Repository.userName = userName;
        return Repository.userName;
    }

    private Repository(){


        created = true;
    }

    private static Task<AuthResult> waitForSignIn() {

        Task task = null;

            task = firebaseAuth.getPendingAuthResult();
        return task;

    }


    private static DocumentReference getPriorityWordsRef(){
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName()).document("PriorityWords");
    }


    private static DocumentReference getBucketWordsRef() {
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName()).document("BucketWords");
    }


    private static DocumentReference getUserGroupRef(){
        return FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName());
    }

    @NonNull
    private static DocumentReference getAnotherUserGroup(String userName) {
        return FirebaseFirestore.getInstance().collection("groups").document(userName);
    }

    /**
     * <p>initializes the prioritywords and bucketwords document.</p>
     * <p>note: to access a users collection, {@link Repository#setUserName(String) Repository.setUserName()} should be used with the desired name to be given the collection.</p>
     */
    public static void init(FirebaseAuth firebaseAuth){
        Repository.firebaseAuth = firebaseAuth;
        if(created) return;
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth1 -> {
            if(firebaseAuth1.getCurrentUser() != null) {
                Repository.userName = firebaseAuth1.getCurrentUser().getDisplayName();
                Log.d(TAG, "init: confirmed logged in");
                setUser(firebaseAuth1.getCurrentUser());
                HashMap<String, Object> commetnsInit = new HashMap<String, Object>(){{
                    put("comments",FieldValue.arrayUnion());
                }};
                HashMap<String, Object> groupMembersInit = new HashMap<String, Object>(){{
                    put("groupMembers",FieldValue.arrayUnion());
                }};
                FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName()).get().addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.contains("comments")){
                        FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName()).set(commetnsInit,SetOptions.merge());
                    }
                    if(!documentSnapshot.contains("groupMembers")){
                        FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName()).set(groupMembersInit,SetOptions.merge());

                    }
                });
                Task taskPriorityWords = getAllPriorityWords();
                Task taskBucketWords = getBucketWords();

                taskPriorityWords.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                    if (task.isSuccessful()) {
                        try {

                            for (Map.Entry<String, Object> entry:
                                    task.getResult().getData().entrySet()){
                                priorityWords.put(entry.getKey(), ((Long) entry.getValue()).intValue());
                            }
                        } catch (Throwable throwable) {
                            Log.d("firestore,Repository", "onComplete: failed at adding PriorityWords to HashMap");
                            throwable.printStackTrace();
                        }
                    }
                });
                taskBucketWords.addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                    if (task.isSuccessful()) {
                        try {
                            for(Map.Entry<String, Object> entry:
                                    task.getResult().getData().entrySet()){
                                bucketWords.put(entry.getKey(), (TimePack) entry.getValue());
                            }
                        } catch (Throwable throwable) {
                            Log.d("firestore,Repository", "onComplete: failed at adding BucketWords to HashMap");
                            throwable.printStackTrace();
                        }
                    }
                });

            }

        });
        created = true;
        return;
    }

    /**
     * @see Repository#setNotification(Context, ActivityTask, Activity)
     * @param defaultActivity the activity to be used by default.
     */
    public static void setDefaultActivity(Activity defaultActivity) {
        Repository.defaultActivity = defaultActivity;
    }

    /**
     * @see Repository#setNotification(Context, ActivityTask, Activity)
     * @param defaultContext the context to be used by default.
     */
    public static void setDefaultContext(Context defaultContext) {
        Repository.defaultContext = defaultContext;
    }

    //region ActivityTask
    public static Task createActivityTask(int activityTaskID, MasloCategory masloCategory, String content, ArrayList<SubActivity> subActivitys, TimePack timePack) {
        ActivityTask activityTask = new ActivityTask(activityTaskID,masloCategory,content,subActivitys,timePack, priorityWords);

        return getActivityTaskCollection().orderBy("activityTaskID", Query.Direction.DESCENDING).limit(1).get().continueWithTask(task ->
                task.addOnCompleteListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documentSnapshot = queryDocumentSnapshots.getResult().getDocuments();
            if(!documentSnapshot.isEmpty()) {
                int nextID = documentSnapshot.get(0).toObject(ActivityTask.class).getActivityTaskID() + 1;
                activityTask.setActivityTaskID(nextID);
                getActivityTaskCollection().document("ActivityTask" + nextID).set(activityTask)
                        .addOnSuccessListener(unused -> Log.d("firestore", "createActivityTask: success"))
                        .addOnFailureListener(e -> Log.d("firestore", "createActivityTask: failed"));

            }else {
                getActivityTaskCollection().document("ActivityTask" + activityTaskID).set(activityTask)
                        .addOnSuccessListener(unused -> Log.d("firestore", "createActivityTask: success"))
                        .addOnFailureListener(e -> Log.d("firestore", "createActivityTask: failed"));
            }
        }));



    }

    public static Task<QuerySnapshot> getAllUserActivityTasks(){

       Task task = getActivityTaskCollection()
               .get()
               .addOnSuccessListener(queryDocumentSnapshots -> Log.d("firestore", "getAllUserActivityTasks: success"))
               .addOnFailureListener(e -> Log.d("firestore", "getAllUserActivityTasks: failure"));

        return task;
    }

    @NonNull
    private static CollectionReference getActivityTaskCollection() {
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName() + "ActivityTasks");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Task<QuerySnapshot> getThisDayActivityTasks(){

        //because there are no other objects beside ActivityTask that contain TimePack, this will return an array of ActivityTasks
        Task task = getActivityTaskCollection()
                .whereEqualTo("monthNumber", YearMonth.now().getMonthValue())
                .whereArrayContains("relaventDatesNumbered",LocalDateTime.now().getDayOfMonth())
                .get();

         return task;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Task<Void> updateActivityTask(int activityTaskID, String fieldToUpdate, String newValue) {

        DocumentReference updatedActivityTask = getActivityTaskCollection().document("ActivityTask" + activityTaskID);



        Task task = updatedActivityTask
                .update(fieldToUpdate, newValue)
                .addOnSuccessListener(unused -> {
                    Log.d("firestore", "updateActivityTask: success for ID " + activityTaskID + " | updated " + fieldToUpdate + "to " + newValue);
                    int newPriority = 0;
                    if(fieldToUpdate.equals("content")){
                        String[] newWords = newValue.split(" ");
                        for (String newWord :
                                newWords) {
                            newPriority += priorityWords.getOrDefault(newWord,0);
                        }

                    }
                    updatedActivityTask.update("priority",newPriority);
                });

        return task;
    }

    public static Task<Void> deleteActivivtyTask(int activityTaskID){


        return getActivityTaskCollection().document("ActivityTask" + activityTaskID)
                .delete()
                .addOnFailureListener(ea -> Log.d("firestore", "deleteActivivtyTask: failed to delete | " + ea.getCause()))
                .addOnSuccessListener(unused -> Log.d("firestore", "deleteActivivtyTask: success"));

    }

    public static void completeActivityTask(int activityTaskID){

    HashMap<String,FieldValue> updates = new HashMap<>();
    updates.put(getUser().getDisplayName(),FieldValue.increment(1));

        getActivityTaskCollection().document("ActivityTask" + activityTaskID).get().addOnSuccessListener(documentSnapshot -> {
            ActivityTask activityTask = documentSnapshot.toObject(ActivityTask.class);
            if(activityTask != null && !activityTask.getComplete()){
                getActivityTaskCollection().document("ActivityTask" + activityTaskID).update("complete",true).continueWith(task -> {
                    if(task.isSuccessful()) {
                        FirebaseFirestore.getInstance().collection("groups").get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        task1.getResult().getDocuments().forEach(documentSnapshot1 -> {
                                            documentSnapshot1.getReference().update("groupMembers." + getUser().getDisplayName(), FieldValue.increment(1));
                                        });

                                    } else {
                                        Log.d(TAG, "completeActivityTask: failed");
                                    }
                                });
                    }
                    return null;
                });
            }
        });

    };


    //endregion

    //region PriorityWords


    public static Task<Void> createPriorityWord(String word,int priorty) {
        Map<String,Integer> newWord = new HashMap<>();
        newWord.put(word,priorty);
        Task task = null;

            getPriorityWordsRef()
                    .set(newWord, SetOptions.merge())
                    .addOnSuccessListener(unused -> Log.d("firestore", "createPriorityWord: success for word " + word + " user " + getUser().getDisplayName()))
                    .addOnFailureListener(e -> Log.d("firestore", "createPriorityWord: failure"));


        return task;
    }

    public static Task<DocumentSnapshot> getAllPriorityWords() {
        Task task;

            task =getPriorityWordsRef().get();


        return task;
    }

    public static Task<Void> updatePriorityWord(String word, int priority){
        Task task;

           task = getPriorityWordsRef()
                    .update(word,priority)
                    .addOnSuccessListener(unused -> Log.d("firestore", "updatePriorityWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "updatePriorityWord: failed"));


        return task;
    }

    public static Task<Void> deletePriorityWord(String word){

        Map<String,Object> updates = new HashMap<>();
        updates.put(word, FieldValue.delete());
        Task task;

            task = getPriorityWordsRef()
                    .update(updates)
                    .addOnSuccessListener(unused -> Log.d("firestore", "deletePriorityWord: success removing word :" + word ))
                    .addOnFailureListener(e -> Log.d("firestore", "deletePriorityWord: failed to remove word :" + word));


        return task;

    }

    //endregion

    //region BucketWords


    public static Task<Void> createBucketWord(String word, TimePack timePack){
        Map<String, TimePack> newSet = new HashMap<>();
        newSet.put(word,timePack);
        Task task;

            task = getBucketWordsRef()
                    .set(newSet,SetOptions.merge())
                    .addOnSuccessListener(unused -> Log.d("firestore", "createBucketWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "createBucketWord: failed"));


        return task;
    }

    public static Task<DocumentSnapshot> getBucketWords(){ //TODO: this was privet, changed it to public - from Lior

        Task task;


           task = getBucketWordsRef()
                    .get()
                    .addOnSuccessListener(documentSnapshot -> Log.d("firestore", "getBucketWords: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "getBucketWords: failed"));


        return task;

    }

    public static Task<Void> updateBucketWord(String word,TimePack timePack){
        Task task;


            task = getBucketWordsRef()
                    .update(word,timePack)
                    .addOnSuccessListener(unused -> Log.d("firestore", "updateBucketWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "updateBucketWord: failed"));


        return task;
    }

    public static Task<Void> deleteBucketWord(String word){
        Map<String ,Object> updates = new HashMap<>();
        updates.put(word,FieldValue.delete());
        Task task;

            task = getBucketWordsRef()
                    .update(updates)
                    .addOnSuccessListener(unused -> Log.d("firestore", "deleteBucketWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "deleteBucketWord: failed"));


        return task;
    }

    //endregion

    //region notification

    /**
     *<p>The method calls for the ActivityTasks relevent for the date LocalDateTime.now() returns. and applies smart notification logic</p>
     *@see <a href="https://app.diagrams.net/#G1F6Cc5yGinKNx1HIdPaEcC9FuKkSm9ye3">Smart notification logic flowchart</a>
     * @throws Throwable
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void refreshNotifications() {
        Map<LocalDate,Boolean> today = new HashMap<>();
        today.put(LocalDate.now(),true);
        ArrayList<ActivityTask> thisDayActivityTasks = new ArrayList<>();
        getThisDayActivityTasks().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document :
                            task.getResult()) {
                        thisDayActivityTasks.add(document.toObject(ActivityTask.class));
                    }


                    for (ActivityTask activityTask1 :
                            thisDayActivityTasks) {

                        ArrayList<LocalDateTime> act1time = activityTask1.getTimePack().readTimeTange();
                        LocalDateTime act1StartingTime = act1time.get(0);
                        LocalDateTime act1EndingTime = act1time.get(1);

                        for (ActivityTask activityTask2 :
                                thisDayActivityTasks) {

                            // if AT1 start time ==  AT2 start time
                            // if AT1 priority > AT2 priority: (AT2 priority / AT1 priority) * (AT1endtimeInFromEpocInMili - AT1startTimeFromEpoc) -> push AT2
                            // same goes for AT1 priority < AT2 priority

                            //if the activities start at the same time, and act1 maslocategory is more important then act2
                            if(activityTask1.getTimePack().readTimeTange().get(0).isEqual(activityTask2.getTimePack().readTimeTange().get(0)) &&
                                    activityTask1.getMasloCategory().ordinal() > activityTask2.getMasloCategory().ordinal()){

                                ArrayList<String> activityTask1bucketWords = new ArrayList<>();
                                ArrayList<String> activityTask2bucketWords = new ArrayList<>();

                                ArrayList<LocalDateTime> act2TimeRange = activityTask2.getTimePack().readTimeTange();

                                LocalDateTime act2StartingTime = act2TimeRange.get(0);
                                LocalDateTime act2EndingTime = act2TimeRange.get(1);
                                for (String word :
                                        activityTask1.getContent().split(" ")) {
                                    if(bucketWords.containsKey(word)) activityTask1bucketWords.add(word);
                                }
                                for (String word :
                                        activityTask2.getContent().split(" ")) {
                                    if(bucketWords.containsKey(word)) activityTask2bucketWords.add(word);

                                }

                                //removing buckets words that exists in both activities.
                                activityTask2bucketWords.removeAll(activityTask1bucketWords);



                                LocalDateTime bestChoice = act2StartingTime;
                                //if there are buckets words that don't exist, try to use them to reassign the start time of act2
                                if (activityTask2bucketWords.size() != 0){



                                    for (String word :
                                            activityTask2bucketWords) {

                                        LocalDateTime time = bucketWords.get(word).readTimeTange().get(0);


                                        if(time.isAfter(act1StartingTime) && time.isBefore(bestChoice)){
                                            bestChoice = time;
                                        }
                                    }
                                    if (bestChoice != act2StartingTime) {
                                        Long changeInTime =  act2EndingTime.atZone(ZoneId.of("Asia/Jerusalem")).toEpochSecond() - act2StartingTime.atZone(ZoneId.of("Asia/Jerusalem")).toEpochSecond();


                                        activityTask2.getTimePack().readTimeTange().set(0,act2StartingTime.plus(changeInTime,ChronoUnit.MILLIS));
                                        activityTask2.getTimePack().readTimeTange().set(1,act2EndingTime.plus(changeInTime,ChronoUnit.MILLIS));
                                        continue;

                                    }
                                }

                                //if bucketwords are the same, try to use priority as a means to resolve.
                                Map<String , ActivityTask.compareResult> x = activityTask1.compare(activityTask2);
                                        if (x.get("priority").equals(ActivityTask.compareResult.higherPriority)) {
                                            Long delay = (act1EndingTime.atZone(ZoneId.of("Assia/Jerusalem")).toInstant().toEpochMilli() - act1StartingTime.atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli()) * ( activityTask2.getPriority() / activityTask1.getPriority());
                                            activityTask2.getTimePack().readTimeTange().set(0,act2StartingTime.plus(delay,ChronoUnit.MILLIS));
                                            activityTask2.getTimePack().readTimeTange().set(1,act2EndingTime.plus(delay,ChronoUnit.MILLIS));

                                            continue;


                                        }


                                        //if all else fails, try to use NATTY to resolve
                                        if(!activityTask2.getTimePack().readNattyResults().isEqual(act1StartingTime)){
                                            LocalDateTime nattyresults = activityTask2.getTimePack().readNattyResults();
                                            Long delay = act2EndingTime.atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli() - act2StartingTime.atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli();
                                            activityTask2.getTimePack().readTimeTange().set(0,nattyresults);
                                            activityTask2.getTimePack().readTimeTange().set(1,nattyresults.plus(delay,ChronoUnit.MILLIS));
                                            continue;
                                        }

                            }
                        }
                    }

                    for (ActivityTask activityTask :
                            thisDayActivityTasks) {
                        setNotification(defaultContext,activityTask,defaultActivity);
                    }
                }
            }
        });

    }

    /**
     * <p>set a notification for a specific activity task. uses the notificationID in the TimePack of the activity task as a requestCode.</p>
     *
     * @param context context of which the notification was created. if non given, {@link Repository#defaultContext defaultContext} will be used.
     * @param activityTask the activity task which the notification is created for.
     * @param activity the activity to open when the notification is tapped on.  if non given, {@link Repository#defaultActivity defaultActivity} will be used.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setNotification(Context context, ActivityTask activityTask, Activity activity){



        long delayInMili = activityTask.getTimePack().readTimeTange().get(0).atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli() - LocalDateTime.now().atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli();

        NotificationSystem.scheduleNotification(context,
                delayInMili,
                activityTask.getTimePack().getNotificationID(),
                activity.getClass(),
                activityTask.getMasloCategory().toString(),
                activityTask.getContent());

        Toast.makeText(context, ""+delayInMili, Toast.LENGTH_LONG).show();
    }


    //endregion

    //region firebase User
    public static FirebaseUser getUser() {
        return Repository.user;
    }

    public static void setUser(FirebaseUser user) {
        Repository.user = user;
        setUserName(user.getDisplayName());
    }
    //endregion

    //region groups
// TODO: 30/07/2021 facilitate groups, a collection of user names with each Documant having a map of username:score.
//  create: automatic.
//  read: read the all documant relevant to getuser().userdisplayname inside collection "groups".(DONE!)
//  update: each Documant updated if it has the current user name inside it. so where(username,FieldValue.incrament(1)).
//  delete: in the relavent documant delete(String removedUser) from the map.(DONE!)
//  group action "Add User To My group": simply add a key:value to the user group documant. if the user exists it will be updated with the "complete" c.r.u.d method of the added user.(DONE!)
// TODO: 30/07/2021 STRUCUTRE: "groups" -> username -> Score map(username:int),Comments map(username:String)

    // TODO: 01/08/2021 facilitate ActivityTask completion and score updates

    public static Task<DocumentSnapshot> readGroup(){

        Task<DocumentSnapshot> task;
        task = getUserGroupRef().get();
        return task;
    }

    public static Task<Void> deleteUserFromMyGroup(String targetedUser){

        Task<Void> task;

        task = getUserGroupRef().update("groupMembers",targetedUser,FieldValue.delete());

        return task;

    }

    public static Task<Void> addUserToMyGroup(String addedUser){
        Map<String,Object> newMember = new HashMap<String,Object>(){{
            put(addedUser,0);
        }};
        Map<String,Object> init = new HashMap<String,Object>(){{
            put("groupMembers",new HashMap<String,Integer>());
        }};


            getUserGroupRef().set(init,SetOptions.merge());

        Task<Void> set = getUserGroupRef().update("groupMembers",FieldValue.arrayUnion(newMember));
        return set;

    }

    public static Task<Void> addCommentToAnotherUser(String targetedUser, String comment){
        Task<Void> task = null;
        HashMap<String,String> addedField = new HashMap<>();
        addedField.put(getUser().getDisplayName(),comment);

        getAnotherUserGroup(targetedUser).update("comments",FieldValue.arrayUnion(addedField));

        return task;
    }


    public static Task deleteCommentFromMyProfile(String comment,String targetedUserName){

        Task<Void> task = null;
        Map<String,Object> updates = new HashMap<String,Object>(){
            {
                put(targetedUserName, comment);
            }};
        getUserGroupRef().update("comments",updates);

        return task;

    }




    //endregion

    //priority one:
    // TODO: 25/07/2021 learn to test. possible: tests with android studio.(DONE!) follow up: learn to test with firestore(Time consuming)
    // TODO: 25/07/2021 is it ok to use javadocs as a project book?

    // TODO: 01/08/2021 check out the activityTaskID incrmentation(DONE!)
    // TODO: 01/08/2021 look into the async issue

    // TODO: 18/07/2021 implament auto assignment to timerange in timepack (DONE!)
    // TODO: 18/07/2021 auto fill releventDates with Repetition enum(DONE!)
    // TODO: 18/07/2021 make activiytaskss with empty timerange to go through NATTY or use current time otherwise(DONE!)
    // TODO: 23/07/2021  create a number array in timepack representing the days of the current month that are relavent to the activityTask(DONE!)


    // TODO: 18/07/2021 find a solution to static context memory leak
    // TODO: 18/07/2021 consider using collectionsRef instead of docref for priority and bucket words

}
