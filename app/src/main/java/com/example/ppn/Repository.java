package com.example.ppn;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Repository {

    private static Repository repository;
    private static boolean created = false;

    private static Context defaultContext;

    private static Activity defaultActivity;

    private static WordPriority wordPriority;// TODO: 27/06/2021 DO WE EVEN NEEDS THAT?
    private static Map<String, Integer> priorityWords = new HashMap<>();
    private static Map<String, TimePack> bucketWords = new HashMap<>();

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    static private DocumentReference priorityWordsRef = db.collection(Repository.userName).document("PriorityWords");
    static private DocumentReference bucketWordsRef = db.collection(Repository.userName).document("BucketWords");

    private static String userName = "default";

    public static String setUserName(String userName) {
        Repository.userName = userName;
        return Repository.userName;
    }

    private Repository(){

        Task taskPriorityWords = getAllPriorityWords();
        Task taskBucketWords = getBucketWords();

        taskPriorityWords.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    priorityWords.putAll((Map<? extends String, ? extends Integer>) task.getResult(Map.class));
                } catch (Throwable throwable) {
                    Log.d("firestore,Repository", "onComplete: failed at adding PriorityWords to HashMap");
                    throwable.printStackTrace();
                }
            }
        });
        taskBucketWords.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    bucketWords.putAll((Map<? extends String, ? extends TimePack>) task.getResult(Map.class));
                } catch (Throwable throwable) {
                    Log.d("firestore,Repository", "onComplete: failed at adding BucketWords to HashMap");
                    throwable.printStackTrace();
                }
            }
        });

        created = true;
    }

    /**
     *
     * intitializes the prioritywords and bucketwords documant.
     * note: to access a users collection, setUserName should be used with the desired name to be given the collection.
     *
     *
     * @return a Repository instance
     */
    public static Repository getInstance(){
        if(created) return repository;
        repository = new Repository();
        created = true;
        return repository;
    }


    public static void setDefaultActivity(Activity defaultActivity) {
        Repository.defaultActivity = defaultActivity;
    }
    public static void setDefaultContext(Context defaultContext) {
        Repository.defaultContext = defaultContext;
    }

    //region ActivityTask


    public static Task createActivityTask(int activityTaskID, MasloCategory masloCategory, String content, Map<Integer,SubActivity> subActivity, TimePack timePack){

        ActivityTask activityTask = new ActivityTask(activityTaskID,masloCategory,content,subActivity,timePack, priorityWords);

        Task task = db.collection(Repository.userName + "ActivityTasks").document("ActivityTask" + activityTaskID)
                .set(activityTask)
                .addOnSuccessListener(unused -> Log.d("firestore", "createActivityTask: success"))
                .addOnFailureListener(e -> Log.d("firestore", "createActivityTask: failed"));

        return task;
    }



    public static Task getAllUserActivityTasks() throws Throwable {

       Task task = db.collection(Repository.userName + "ActivityTasks")
               .get()
               .addOnSuccessListener(queryDocumentSnapshots -> Log.d("firestore", "getAllUserActivityTasks: success"))
               .addOnFailureListener(e -> Log.d("firestore", "getAllUserActivityTasks: failure"));


       /*       task.addOnCompleteListener(task1 -> {
           if(task1.isSuccessful()){
               ////adapter.setdata(activityTasks);
           }
       });

 */

        return task;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Task getThisDayActivityTasks(){

        Map<LocalDate,Boolean> today = new HashMap<>();
        today.put(LocalDate.now(),true);

        //because there are no other objects beside ActivityTask that contain TimePack, this will return an array of ActivityTasks
        Task task = db.collection(Repository.userName + "ActivityTasks")
                .whereEqualTo("monthNumber", YearMonth.now().getMonthValue())
                .whereArrayContains("monthRange",today)
                .get();

         return task;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Task updateActivityTask(int activityTaskID, String fieldToUpdate, String newValue) {

        DocumentReference updatedActivityTask = db.collection(Repository.userName + "ActivityTasks").document("ActivityTask" + activityTaskID);



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
                    updatedActivityTask.update("content",newPriority);
                });

        return task;
    }

    public static Task deleteActivivtyTask(int activityTaskID){

        Task task = db.collection(Repository.userName + "ActivityTasks").document("ActivityTask" + activityTaskID)
                .delete()
                .addOnFailureListener(e -> Log.d("firestore", "deleteActivivtyTask: failed to delete | " + e.getCause()))
                .addOnSuccessListener(unused -> Log.d("firestore", "deleteActivivtyTask: success"));

        return task;

    }


    //endregion

    //region PriorityWords


    public static Task createPriorityWord(String word,int priorty){
        Map<String,Integer> newWord = new HashMap<>();
        newWord.put(word,priorty);
        Task task = priorityWordsRef
                .set(newWord)
                .addOnSuccessListener(unused -> Log.d("firestore", "createPriorityWord: success"))
                .addOnFailureListener(e -> Log.d("firestore", "createPriorityWord: failure"));
        return task;
    }

    private static Task getAllPriorityWords(){

        Task task = priorityWordsRef.get();
        return task;
    }

    public static Task updatePriorityWord(String word, int priority){

        Task task = priorityWordsRef
                .update(word,priority)
                .addOnSuccessListener(unused -> Log.d("firestore", "updatePriorityWord: success"))
                .addOnFailureListener(e -> Log.d("firestore", "updatePriorityWord: failed"));

        return task;
    }

    public static Task deletePriorityWord(String word){

        Map<String,Object> updates = new HashMap<>();
        updates.put(word, FieldValue.delete());

        Task task = priorityWordsRef
                .update(updates)
                .addOnSuccessListener(unused -> Log.d("firestore", "deletePriorityWord: success removing word :" + word ))
                .addOnFailureListener(e -> Log.d("firestore", "deletePriorityWord: failed to remove word :" + word));

        return task;

    }

    //endregion

    //region BucketWords


    public static Task createBucketWord(String word, TimePack timePack){
        Map<String, TimePack> newSet = new HashMap<>();
        newSet.put(word,timePack);

        Task task = bucketWordsRef
                .set(newSet)
                .addOnSuccessListener(unused -> Log.d("firestore", "createBucketWord: success"))
                .addOnFailureListener(e -> Log.d("firestore", "createBucketWord: failed"));

        return task;
    }

    private static Task getBucketWords(){

        Task task = bucketWordsRef
                .get()
                .addOnSuccessListener(documentSnapshot -> Log.d("firestore", "getBucketWords: success"))
                .addOnFailureListener(e -> Log.d("firestore", "getBucketWords: failed"));

        return task;

    }

    public static Task updateBucketWord(String word,TimePack timePack){

        Task task = bucketWordsRef
                .update(word,timePack)
                .addOnSuccessListener(unused -> Log.d("firestore", "updateBucketWord: success"))
                .addOnFailureListener(e -> Log.d("firestore", "updateBucketWord: failed"));

        return task;
    }

    public static Task deleteBucketWord(String word){
        Map<String ,Object> updates = new HashMap<>();
        updates.put(word,FieldValue.delete());

        Task task = bucketWordsRef
                .update(updates)
                .addOnSuccessListener(unused -> Log.d("firestore", "deleteBucketWord: success"))
                .addOnFailureListener(e -> Log.d("firestore", "deleteBucketWord: failed"));

        return task;
    }

    //endregion

    //region notification

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void refreshNotifications() throws Throwable {
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
                    for (ActivityTask activityTask :
                            thisDayActivityTasks) {
                        setNotification(defaultContext,activityTask,defaultActivity);
                    }
                }
            }
        });

    }

    /**
     *
     * set a notification for a specific activity task. uses the notificationID in the TimePack of the activity task as a requestCode.
     *
     * @param context, context of which the notification was created
     * @param activityTask the activity task which the notification is created for
     * @param activity the activity to open when the notification is tapped on
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setNotification(Context context, ActivityTask activityTask, Activity activity){

        final LocalDateTime[] nearestTime = new LocalDateTime[1];
        activityTask.getTimePack().getTimeRange().forEach((localDateTime, localDateTime2) -> nearestTime[0] = localDateTime.isBefore(nearestTime[0])? localDateTime: nearestTime[0]);

        long delayInMili = LocalDateTime.now().atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli() - nearestTime[0].atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli();

        NotificationSystem.scheduleNotification(context,
                delayInMili,
                activityTask.getTimePack().getNotificationID(),
                activity.getClass(),
                activityTask.getMasloCategory().toString(),
                activityTask.getContent());
    }

    //endregion

    // TODO: 28/06/2021 check for overlap in timepack timerange of active(todays) notifications and handle according to bucketwords > priority > natty
}
