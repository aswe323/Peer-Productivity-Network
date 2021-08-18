package com.example.ppn;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
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
 * <p>A control class giving access to system components and operation in firestore</p>
 *
 *
 * <p>{@link #init()} should be called <b>before</b> logging into firestore, to make sure the {@link FirebaseAuth.AuthStateListener} is listening to a login otherwise {@link Repository} will not be initialized properly</p>
 * <p>some members should be set by the front end:</p>
 * <lo>
 *     <li>{@link Repository#defaultContext} and {@link Repository#defaultActivity} in order to make sure {@link Repository#refreshNotifications()} can run, as it does not accept a context</li>
 * </lo>
 * <p> a use of a method might look like this</p>
 *<pre> ArrayList<ActivityTask> activityTask = new ArrayList<>();
 *        Repository.getAllUserActivityTasks().addOnSuccessListener(queryDocumentSnapshots -> {
 *            List<DocumentSnapshot> activityTasks = queryDocumentSnapshots.getDocuments();
 *            activityTasks.forEach(documentSnapshot -> {
 *                 activityTask.add(documentSnapshot.toObject(ActivityTask.class));
 *            });
 *        });
 * </pre>
 *
 * <p>sense {@link Task} is an asynchronous operation, it is reccomanded to use {@link Task#continueWithTask(Continuation)} to synchronise multiple operations one after the other:</p>
 * <pre>
 *     ArrayList<ActivityTask> activityTask = new ArrayList<>();
 *     Repository.getAllUserActivityTasks().addOnSuccessListener(queryDocumentSnapshots -> {
 *            List<DocumentSnapshot> activityTasks = queryDocumentSnapshots.getDocuments();
 *            activityTasks.forEach(documentSnapshot -> {
 *            activityTask.add(documentSnapshot.toObject(ActivityTask.class));
 *                });
 *            }).continueWithTask(task ->{
 *                    task.addOnSuccessListener(task1 -> {
 *                          //update UI here
 *                        }
 *                });
 * </pre>
 * <p> most methods return a {@link Task} representing a asynchronous operation, for more information read the google/firestore documentation </p>
 * @see <a href="https://developer.android.com/reference/com/google/android/play/core/tasks/Task">Tasks Android Reference</a>
 * @see <a href="https://cloud.google.com/firestore/docs/query-data/get-data">Getting Data with Firestore</a>
 */
public class Repository {
    /**
     * Used for debugging
     */
    private static final String TAG = "Repository";
    /**
     * <p>indicated if {@link #init()} was run</p>
     */
    private static boolean created = false;
    /**
     * <p>default context to be used when creating a new notification.</p>
     * <p>use {@link #setDefaultContext(Context)} to initiate</p>
     */
    private static Context defaultContext;
    /**
     * indicates what activity should be returned to by default when notifications are clicked/tapped.
     */
    private static Activity defaultActivity;
    /**
     * the currently signin user object
     */
    private static FirebaseUser user;
    /**
     * priority words of the user. should not be used outside {@link Repository}
     */
    private static Map<String, Integer> priorityWords = new HashMap<>();
    /**
     * bucket words of the user. should not be used outside {@link Repository}
     */
    private static Map<String, TimePack> bucketWords = new HashMap<>();

    /**
     *
     * @return {@link DocumentReference} refrencing the current {@link #user} priority words.
     */
    public static DocumentReference getPriorityWordsRef(){
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName()).document("PriorityWords");
    }

    /**
     *
     * @return {@link DocumentReference} refrencing the current {@link #user} bucket words.
     */
    public static DocumentReference getBucketWordsRef() {
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName()).document("BucketWords");
    }

    /**
     *
     * @return {@link DocumentReference} refrencing the current {@link #user} group.
     */
    private static DocumentReference getUserGroupRef(){
        return FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName());
    }

    /**
     *
     * @param userName the user name of the request user, should be the same as if the requested user's {@link FirebaseUser#getDisplayName()} result
     * @return {@link DocumentReference} referencing the group corresponding to the userName passed.
     */
    @NonNull
    private static DocumentReference getAnotherUserGroup(String userName) {
        return FirebaseFirestore.getInstance().collection("groups").document(userName);
    }

    /**
     * @deprecated
     * <p> should use {@link #init()}, as fireBaseAuth parameter is no longer needed.</p>
     * <p>calls {@link #init()}</p>
     * @param firebaseAuth not used.
     */
    public static void init(FirebaseAuth firebaseAuth){
        init();
    }
    /**
     * <p>if {@link #created} is true, returns</p>
     * <p>uses a {@link FirebaseAuth.AuthStateListener}, when the state changed and a user is logged in does the following:</p>
     * <lo>
     *     <li>calls {@link #setUser(FirebaseUser)}</li>
     *     <li>makes sure both the current user group and user comments fields are initialized in firestore, if not, initializes them</li>
     *     <li>initializes {@link #priorityWords} and {@link #bucketWords} from firestore</li>
     *     <li>sets {@link #created} to true, <b>regardless of success</b></li>
     * </lo>
     */
    public static void init(){
        if(created) return;
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth1 -> {
            if(firebaseAuth1.getCurrentUser() != null) {
                Log.d(TAG, "init: confirmed logged in");
                setUser(firebaseAuth1.getCurrentUser());
                HashMap<String, Object> commetnsInit = new HashMap<String, Object>(){{
                    put("comments",FieldValue.arrayUnion());
                }};
                HashMap<String, Object> groupMembersInit = new HashMap<String, Object>(){{
                    put("groupMembers",FieldValue.arrayUnion());
                }};
                FirebaseFirestore.getInstance().collection("groups").document(getUser().getDisplayName()).get()
                        .addOnSuccessListener(documentSnapshot -> {
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

    /**
     * <p>resolves a new {@link ActivityTask} into the {@link #user} ActivityTask collection</p>
     * <p>the ID given may change if already taken.</p>
     * @param activityTaskID the candidate ID for the ActivityTask
     * @param masloCategory {@link MasloCategory}
     * @param content the description of the ActivityTask
     * @param subActivitys an array of {@link SubActivity}, can be null.
     * @param timePack {@link TimePack}
     * @return {@link Task} the upload process to firestore
     */
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

    /**
     *
     * @return {@link Task<QuerySnapshot>} resolving into a {@link QuerySnapshot} with all of the activityTasks of the user
     */
    public static Task<QuerySnapshot> getAllUserActivityTasks(){

       Task task = getActivityTaskCollection()
               .get()
               .addOnSuccessListener(queryDocumentSnapshots -> Log.d("firestore", "getAllUserActivityTasks: success"))
               .addOnFailureListener(e -> Log.d("firestore", "getAllUserActivityTasks: failure"));

        return task;
    }

    /**
     *
     * @return {@link CollectionReference} of the user activityTasks collection.
     */
    @NonNull
    public static CollectionReference getActivityTaskCollection() {
        return FirebaseFirestore.getInstance().collection(getUser().getDisplayName() + "ActivityTasks");
    }

    /**
     *
     * @return {@link Task<QuerySnapshot>} that resolves into {@link QuerySnapshot} containing activityTasks relevant to the current date.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Task<QuerySnapshot> getThisDayActivityTasks(){
        FieldPath.of("timePack","relaventDatesNumbered");
        //because there are no other objects beside ActivityTask that contain TimePack, this will return an array of ActivityTasks
        Task task = getAllUserActivityTasks().continueWithTask(task1 -> {
                task1.getResult().toObjects(ActivityTask.class);
            return task1;
        });
         return task;
    }

    /**
     *
     * @param activityTaskID the ID of the {@link ActivityTask} to update in firestore
     * @param fieldToUpdate one of {@link ActivityTask} members.
     * @param newValue the new value for the field to hold.
     * @return {@link Task<Void>} of the operation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Task<Void> updateActivityTask(int activityTaskID, String fieldToUpdate, Object newValue) {
        DocumentReference updatedActivityTask = getActivityTaskCollection().document("ActivityTask" + activityTaskID);
        Task task = updatedActivityTask
                .update(fieldToUpdate, newValue)
                .addOnSuccessListener(unused -> {
                    Log.d("firestore", "updateActivityTask: success for ID " + activityTaskID + " | updated " + fieldToUpdate + "to " + newValue);
                    int newPriority = 0;
                    if(fieldToUpdate.equals("content")){
                        String[] newWords = ((String) newValue).split(" ");
                        for (String newWord :
                                newWords) {
                            newPriority += priorityWords.getOrDefault(newWord,0);
                        }
                    }
                    updatedActivityTask.update("priority",newPriority);
                });
        return task;
    }

    /**
     *
     * @param activityTaskID the ID of the {@link ActivityTask} to be deleted from firestore
     * @return {@link Task} of the operation.
     */
    public static Task<Void> deleteActivivtyTask(int activityTaskID){


        return getActivityTaskCollection().document("ActivityTask" + activityTaskID)
                .delete()
                .addOnFailureListener(ea -> Log.d("firestore", "deleteActivivtyTask: failed to delete | " + ea.getCause()))
                .addOnSuccessListener(unused -> Log.d("firestore", "deleteActivivtyTask: success"));

    }

    /**
     * marks the {@link ActivityTask} with the activityTaskID as complete, and updates score across groups.
     * @param activityTaskID the ID of the {@link ActivityTask} to mark as completed.
     */
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

    /**
     *
     * @param word the word to attack a priority to
     * @param priorty the number value of the word.
     * @return {@link Task<Void>} of the operation
     */
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

    /**
     *
     * @return {@link Task<DocumentSnapshot>} that resolves into {@link DocumentSnapshot} containing all the priority words of the current {@link #user}
     */
    public static Task<DocumentSnapshot> getAllPriorityWords() {
        Task task;
            task =getPriorityWordsRef().get();
        return task;
    }

    /**
     * updates a priority word with new priority value.
     * @param word the words to be updated
     * @param priority the new value of the word
     * @return {@link Task<Void> } of the operation.
     */
    public static Task<Void> updatePriorityWord(String word, int priority){
        Task task;

           task = getPriorityWordsRef()
                    .update(word,priority)
                    .addOnSuccessListener(unused -> Log.d("firestore", "updatePriorityWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "updatePriorityWord: failed"));


        return task;
    }

    /**
     * removes a priority word from the {@link #user} firestore documant.
     * @param word the word to be deleted
     * @return {@link Task<Void>}
     */
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

    /**
     *
     * @param word the word to create
     * @param timePack the {@link TimePack} corresponding to the word
     * @return {@link Task<Void>} of the operation
     */
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

    /**
     *
     * @return {@link Task<DocumentSnapshot>} resolves into {@link DocumentSnapshot} holding the {@link #user} bucket words
     */
    public static Task<DocumentSnapshot> getBucketWords(){
        Task task;
           task = getBucketWordsRef()
                    .get()
                    .addOnSuccessListener(documentSnapshot -> Log.d("firestore", "getBucketWords: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "getBucketWords: failed"));
        return task;
    }

    /**
     *
     * @param word the word to be updated
     * @param timePack the new {@link TimePack} that word will represent
     * @return {@link Task<Void>} of the operation
     */
    public static Task<Void> updateBucketWord(String word,TimePack timePack){
        Task task;


            task = getBucketWordsRef()
                    .update(word,timePack)
                    .addOnSuccessListener(unused -> Log.d("firestore", "updateBucketWord: success"))
                    .addOnFailureListener(e -> Log.d("firestore", "updateBucketWord: failed"));


        return task;
    }

    /**
     *
     * @param word the word to be removed from firestore
     * @return {@link Task<Void>} of the operation
     */
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
        getAllUserActivityTasks().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document :
                        task.getResult()) {
                    thisDayActivityTasks.add(document.toObject(ActivityTask.class));
                }
                thisDayActivityTasks.removeIf (activityTask -> !activityTask.getTimePack().getRelaventDatesNumbered().contains(MonthDay.now().getDayOfMonth()));
                thisDayActivityTasks.forEach(activityTask -> {
                    activityTask.setComplete(false);
                    updateActivityTask(activityTask.getActivityTaskID(),"complete",false);
                });

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
                        boolean sameStartTime = activityTask1.getTimePack().readTimeTange().get(0).isEqual(activityTask2.getTimePack().readTimeTange().get(0));
                        boolean act1MasloHigher = activityTask1.getMasloCategory().ordinal() > activityTask2.getMasloCategory().ordinal();
                        if(sameStartTime && act1MasloHigher && !activityTask1.getComplete()) {

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
        });

    }

    /**
     * <p>set a notification for a specific {@link ActivityTask}. uses the {@link TimePack#notificationID} in the {@link ActivityTask#timePack} as a requestCode.</p>
     *
     * @param context context of which the notification was created. if non given, {@link Repository#defaultContext} will be used.
     * @param activityTask the {@link ActivityTask} which the notification is created for.
     * @param activity the activity to open when the notification is tapped on.  if non given, {@link Repository#defaultActivity} will be used.
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

    /**
     *
     * @return the current {@link FirebaseUser} the {@link Repository} is using for it's operations.
     */
    public static FirebaseUser getUser() {
        return Repository.user;
    }

    /**
     *
     * @param user the {@link FirebaseUser} the {@link Repository} will use for it's operations
     */
    public static void setUser(FirebaseUser user) {
        Repository.user = user;
    }
    //endregion

    //region groups


    public static Task<HashMap<String,Integer>> getOtherUserGroup(String otherUserDisplayName){
        Task<HashMap<String ,Integer>> returned = FirebaseFirestore.getInstance().collection("groups").document(otherUserDisplayName).get().continueWith(task -> {
            HashMap<String ,Integer> hashMap = new HashMap<>();
            task.addOnSuccessListener(documentSnapshot -> {
                for (Map.Entry entry:
                        ((HashMap<String ,Integer>)task.getResult().getData().get("groupMemebrs")).entrySet()) {
                    hashMap.put((String)entry.getKey(),(Integer) entry.getValue());
                }
            });
            return hashMap;

        });
        return returned;
    }

    /**
     *
     * @return {@link Task<DocumentSnapshot>} that resolves into a {@link DocumentSnapshot} with the {@link #user} group document.
     */
    public static Task<DocumentSnapshot> readGroup(){

        Task<DocumentSnapshot> task;
        task = getUserGroupRef().get();
        return task;
    }

    /**
     * deletes a user from the user's tracked users group.
     * @param targetedUser the targeted user, as will be given if they called {@link FirebaseUser#getDisplayName()}, and present in the current {@link #user} group.
      * @return {@link Task<Void>} of the operation
     */
    public static Task<Void> deleteUserFromMyGroup(String targetedUser){

        Task<Void> task;

        task = getUserGroupRef().update("groupMembers",targetedUser,FieldValue.delete());

        return task;

    }

    /**
     * adds a user to track the score of in the {@link #user} group.
     * @param addedUser the name of the user to be added as will be seen if they called {@link FirebaseUser#getDisplayName()}.
     * @return {@link Task<Void>} of the operation
     */
    public static Task<Void> addUserToMyGroup(String addedUser){
        Map<String,Object> newMember = new HashMap<String,Object>(){{
            put(addedUser,0);
        }};

        Task<Void> set = getUserGroupRef().update("groupMembers",FieldValue.arrayUnion(newMember));
        return set;

    }

    /**
     * adds a comment to the targetedUser comment field in his relavent group documant.
     * @param targetedUser the name of the user as it will be return from {@link FirebaseUser#getDisplayName()}
     * @param comment the comment to add to the user
     * @return  {@link Task<Void>} of the operation
     */
    public static Task<Void> addCommentToAnotherUser(String targetedUser, String comment){
        Task<Void> task = null;
        HashMap<String,String> addedField = new HashMap<>();
        addedField.put(getUser().getDisplayName(),comment);

        getAnotherUserGroup(targetedUser).update("comments",FieldValue.arrayUnion(addedField));

        return task;
    }

    /**
     * deletes a comment from the {@link #user} comment section in his group documant
     * @param comment the comment content
     * @param targetedUserName the display name of the user that commented on the current {@link #user}
     * @return {@link Task<Void>} of the operation
     */
    public static Task<Void> deleteCommentFromMyProfile(String comment,String targetedUserName){

        Task<Void> task = null;
        Map<String,Object> updates = new HashMap<String,Object>(){
            {
                put(targetedUserName, comment);
            }};
        getUserGroupRef().update("comments",FieldValue.arrayRemove(updates));

        return task;

    }





    //endregion
}
