package com.example.ppn;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.YearMonth;
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
     * Indicates the last date this {@link ActivityTask} have been completed
     */
    private String stringifiedLastDateCompleted;

    /**
     * used by firestore
     * @return a string formatted from a {@link LocalDateTime} using {@link TimePack#getFormatter()}
      */
   public String getStringifiedLastDateCompleted() {
        return stringifiedLastDateCompleted;
    }

    /**
     * used by firestore
     * @param stringifiedLastDateCompleted a string formatted from a {@link LocalDateTime} using {@link TimePack#getFormatter()}
     */
    public void setStringifiedLastDateCompleted(String stringifiedLastDateCompleted) {
        this.stringifiedLastDateCompleted = stringifiedLastDateCompleted;
    }

    /**
     *
     * @param dayOfCompletion a {@link LocalDateTime} object indicating the date this {@link ActivityTask} was last completed.
     */
    public void updateStringifiedLastDateCompleted(LocalDateTime dayOfCompletion){
        setStringifiedLastDateCompleted(dayOfCompletion.format(TimePack.getFormatter()));
    }

    /**
     *
     * @return the last date when this {@link ActivityTask } was completed. or null if it never was completed
     */
    public  LocalDateTime readStringifiedLastDateCompleted(){
        if (getStringifiedLastDateCompleted() != "") {
            return LocalDateTime.parse(getStringifiedLastDateCompleted(),TimePack.getFormatter());
        }
        return null;
    }


    /**
     * firestore requires an empty constructor in order to use {@link com.google.firebase.firestore.DocumentSnapshot#toObject(Class) toObject(Class)}.
     */
    public ActivityTask() {
    }

    /**
     * <p>should only be called from {@link Repository} in to make sure to sync the {@link #activityTaskID} with firestore.</p>
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
        setStringifiedLastDateCompleted("");

        parseContent(content,priorityWords);

        extracted(timePack);
        getTimePack().reCalculateReleventDates();
    }

    /**
     * makes sure the time pack have a starting and ending time, otherwise sets the starting and ending time to now.
     * @param timePack
     */
    private void extracted(TimePack timePack) {
        if(this.getTimePack().readTimeTange().isEmpty()){
            getTimePack().readTimeTange().set(0, timePack.readNattyResults());
            getTimePack().readTimeTange().set(1, timePack.readNattyResults());
        }
    }

    /**
     *
     * <p>tryes to use natty on content to get a date, if failes then set's {@link TimePack#stringifiedNattyResults} to {@link LocalDateTime#now()} using {@link TimePack#getFormatter()}</p>
     * <p>set's the score of {@link ActivityTask#priority} using passed priorityWords</p>
     *
     * @param content the content of the {@link ActivityTask}.
     * @param priorityWords words ranked by priority.
     */
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

    /**
     * used by firestore, do mark an {@link ActivityTask} as completed, use {@link Repository#completeActivityTask(int)} passing {@link ActivityTask#activityTaskID}.
     * @param complete {@link #complete}
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     *
     * @return {@link #complete}
     */
    public boolean getComplete(){
        return complete;
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
