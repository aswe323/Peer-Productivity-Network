package com.example.ppn;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityTask {
    /**
     * used to identify particular {@link ActivityTask}s on firestore, using {@link Repository}'s methods.
     */
    private int activityTaskID;
    /**
     * indicates the human need this Task is related to.
     * @see <a href="https://carrothealth.com/wp-content/uploads/2020/06/20191223-Maslow.png">maslow's pyramid of needs infographic</a>
     */
    private MasloCategory masloCategory;
    /**
     * the description of what the user what's to do.
     */
    private String content;
    /**
     * an {@link ArrayList} of {@link SubActivity}s. allowing to break what the user want's into multiple actions.
     */
    private ArrayList<SubActivity> subActivitys;
    /**
     * the calculated priority of the {@link ActivityTask}, calculated by parsing {@link #content} for priority words and adding their value.
     */
    private int priority;
    /**
     * {@link TimePack}, holding the {@link ActivityTask} time related information.
     */
    private TimePack timePack;
    /**
     * indicates if the task was completed or not.
     */
    private boolean complete;


    /**
     * firestore requires an empty constructor in order to use {@link com.google.firebase.firestore.DocumentSnapshot#toObject(Class) toObject(Class)}.
     */
    public ActivityTask() {
    }

    /**
     * <p>should only be called from {@link Repository} in order to sync the {@link #activityTaskID} with firestore.</p>
     * <p>if natty is not able to parse {@link #content} for a {@link LocalDateTime}, {@link LocalDateTime#now()} will be used instead.</p>
     * <p>uses the passed priorityWords parameter to set the {@link #priority}</p>
     * @param activityTaskID {@link #activityTaskID}
     * @param masloCategory {@link #masloCategory}
     * @param content {@link #content}
     * @param subActivitys {@link #subActivitys}
     * @param timePack {@link #timePack}
     * @param priorityWords makes sure the constructor is only called from {@link Repository}.
     */
    public ActivityTask(int activityTaskID, MasloCategory masloCategory, String content, ArrayList<SubActivity> subActivitys, TimePack timePack,@NonNull Map<String, Integer> priorityWords) {
        setActivityTaskID(activityTaskID);
        setMasloCategory(masloCategory);
        setSubActivitys(subActivitys);
        setContent(content);
        setTimePack(timePack);
        setComplete(false);

        parseContent(content,priorityWords);

        if(this.getTimePack().readTimeTange().isEmpty()){
            getTimePack().readTimeTange().set(0,timePack.readNattyResults());
            getTimePack().readTimeTange().set(1,timePack.readNattyResults());
        }
        getTimePack().reCalculateReleventDates();

    }

    private void parseContent(String content,@NonNull Map<String, Integer> priorityWords) {
        List<DateGroup> groups;
        Parser parser = new Parser();
        groups = parser.parse(content);
        if (groups.size()>0){//check if i got a date

            List dates = groups.get(0).getDates();//get the date that natty created for us
            LocalDateTime localDateTime = ((Date) dates.get(0)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); //convert it to LocalDateTIme
            getTimePack().updateNattyResults(localDateTime);
        }else{
            getTimePack().updateNattyResults(LocalDateTime.now());
            Log.d("activityTask", "ActivityTask: natty was not able to parse activityTask with ID:" + this.activityTaskID);
        }
        String[] words = content.split(" ");
        int  score = 0;
        for (String word :
                words) {
            score +=  priorityWords.getOrDefault(word,0);
        }
        setPriority(score);
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean getComplete(){
        return complete;
    }

    /**
     *  <p>if natty is not able to parse newContent, {@link LocalDateTime#now()} will be used.</p>
     *  <p>note that the task returned is the operation updating the {@link ActivityTask} {@link #priority}</p>
     * @param newContent will be the new {@link #content}
     * @param newMasloCategory will be the new {@link #masloCategory}
     * @param newRepetition will be this {@link ActivityTask} {@link TimePack#repetition}
     * @return {@link Task<DocumentSnapshot>} of the operation
     */
    public Task<DocumentSnapshot> editReminder(String newContent, MasloCategory newMasloCategory, Repetition newRepetition){
        setMasloCategory(newMasloCategory);
        setContent(newContent);//
        getTimePack().setRepetition(newRepetition);

        return Repository.getAllPriorityWords().addOnSuccessListener(documentSnapshot -> {
            int newScore = 0;
            Map<String, Object> data = documentSnapshot.getData();

            for (String word :
                        newContent.split(" ")) {
                                if(data.containsKey(word))
                            newScore += (int) data.get(word);
                }
        });
    }

    public ArrayList<SubActivity> getSubActivitys() {
        return subActivitys;
    }

    public void setActivityTaskID(int activityTaskID) {
        this.activityTaskID = activityTaskID;
    }

    public void setMasloCategory(MasloCategory masloCategory) {
        this.masloCategory = masloCategory;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSubActivitys(ArrayList<SubActivity> subActivitys) {
        this.subActivitys = subActivitys;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTimePack(TimePack timePack) {
        this.timePack = timePack;
    }

    public int getActivityTaskID() {
        return activityTaskID;
    }

    public MasloCategory getMasloCategory() {
        return masloCategory;
    }

    public String getContent() {
        return content;
    }

    public int getPriority() {
        return priority;
    }

    public TimePack getTimePack() {
        return timePack;
    }



    enum compareResult{sameContent,lowerPriority,higherPriority,samePriority,sameNattyResult,none}
    public Map<String,compareResult> compare(ActivityTask activityTask) {
        Map<String ,compareResult> results = new HashMap<>();


        if(this.content.equals(activityTask.getContent())) results.put("content",compareResult.sameContent);

        if(this.priority > activityTask.getPriority()) results.put("priority",compareResult.higherPriority);
        if(this.priority < activityTask.getPriority()) results.put("priority",compareResult.lowerPriority);
        if(this.priority == activityTask.getPriority()) results.put("priority",compareResult.samePriority);


        if(this.getTimePack().readNattyResults().isEqual(activityTask.getTimePack().readNattyResults())) results.put("natty",compareResult.sameNattyResult);

        results.putIfAbsent("content",compareResult.none);
        results.putIfAbsent("natty",compareResult.none);



        return results;
    }
}
