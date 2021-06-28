package com.example.ppn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Repository {


    //In lions rest the king sleeps, awaiting the return of the son to his keep.
    //for dark and sorrow angels came, taking the fabled son away.
    //now his in heaven, in hell, in shadow.
    //and the land now calls.
    //for the son of the lion.

    //In lions rest the king sleeps, awaiting the return of the son to his keep.
    //but evil and death tricks play, the son's soul they took away.
    //replaced by death, greed, and power.
    //but the land still calls.
    //for the son of the lion.

    //In lions rest the king awaits, awaits asleep for his son's soul to be released.
    //for no evil is too great, and no sorrow too deep.
    //will keep the lions son.
    //from returning to his keep.
    //and the land awakes, to return him home.
    //but the son of the lion, in the light walks no more.

    //fabled kings and lost sons. lost to darkness and to death beyond.
    //and in the bright light and sorrow's deep.
    //the lions son.
    //lost his keep.

    //In lions rest, the kings last sleep.
    //In his sons keep, the cold winter now creeps.
    //Evil and sorrow and death and greed.
    //Have took away the sons keep.

    //The lions son, will soon sleep.


    private static Repository repository;
    private static boolean created = false;

    private static WordPriority wordPriority;// TODO: 27/06/2021 DO WE EVENNEEDS THAT?
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

    //region ActivityTask


    public static Task createActivityTask(int activityTaskID, MasloCategory masloCategory, String content, ArrayList<SubActivity> subActivity, TimePack timePack){

        ActivityTask activityTask = new ActivityTask(activityTaskID,masloCategory,content,subActivity,timePack, priorityWords);

        Task task = db.collection(Repository.userName).document("ActivityTask" + activityTaskID)
                .set(activityTask)
                .addOnSuccessListener(unused -> Log.d("firestore", "createActivityTask: success"))
                .addOnFailureListener(e -> Log.d("firestore", "createActivityTask: failed"));

        return task;
    }


    // TODO: 24/06/2021 get ALL activity tasks of the current user

    public static Task getAllUserActivityTasks() throws Throwable {

       Task task = db.collection(Repository.userName)
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

    // TODO: 24/06/2021 update a specific activity task(?)

    public static Task updateActivityTask(int activtyiTaskID,String fieldToUpdate, String newValue) {

        Task task = db.collection(Repository.userName).document("ActivityTask" + activtyiTaskID)
                .update(fieldToUpdate, newValue)
                .addOnSuccessListener(unused -> Log.d("firestore", "updateActivityTask: success for ID " + activtyiTaskID + " | updated " + fieldToUpdate + "to " + newValue));

        return task;
    }



    // TODO: 24/06/2021 remove an activity task

    public static Task deleteActivivtyTask(int activityTaskID){

        Task task = db.collection(Repository.userName).document("ActivityTask" + activityTaskID)
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


    /**
     *
     * set a notification for a specific activity task. uses the notificationID in the TimePack of the activity task as a requestCode.
     *
     * @param context, context of which the notification was created
     * @param activityTask the activity task which the notification is created for
     * @param activity the activity to open when the notification is tapped on
     */
    public static void setNotification(Context context, ActivityTask activityTask, Activity activity){
        long delayInMiliseconds = LocalDateTime.now().atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli() - activityTask.getTimePack().getTimeRange()[0][0].atZone(ZoneId.of("Asia/Jerusalem")).toInstant().toEpochMilli();

        NotificationSystem.scheduleNotification(context,
                delayInMiliseconds,
                activityTask.getTimePack().getNotificationID(),
                activity.getClass(),
                activityTask.getMasloCategory().toString(),
                activityTask.getContent());
        // TODO: 28/06/2021 check for overlap in timepack timerange of active notifications and handle according to bucketwords > priority > natty
        // TODO: 28/06/2021 implament natty parsing into TimePack
        // TODO: 28/06/2021 get the nearest (24h) relevent activitytasks with quering timepacks for monthNumber -> daterange -> timerange
    }


    //endregion

}
