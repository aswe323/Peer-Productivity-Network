package com.example.ppn;

import android.util.Log;

import androidx.annotation.NonNull;


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
    private int activityTaskID;
    private MasloCategory masloCategory;
    private String content;
    private ArrayList<SubActivity> subActivitys;
    private int priority;
    private TimePack timePack;
    private boolean complete;


    public ActivityTask() {
    }

    public ActivityTask(int activityTaskID, MasloCategory masloCategory, String content, ArrayList<SubActivity> subActivitys, TimePack timePack,@NonNull Map<String, Integer> priorityWords) {
        setActivityTaskID(activityTaskID);
        setMasloCategory(masloCategory);
        setSubActivitys(subActivitys);
        setContent(content);
        setTimePack(timePack);
        setComplete(false);
        
        List<DateGroup> groups;
        Parser parser = new Parser();
        groups = parser.parse(content);
        if (groups.size()>0){//check if i got a date

            List dates = groups.get(0).getDates();//get the date that natty created for us
            LocalDateTime localDateTime = ((Date) dates.get(0)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); //convert it to LocalDateTIme
            getTimePack().updateNattyResults(localDateTime);
        }else{
            getTimePack().updateNattyResults(LocalDateTime.now());
            Log.d("activityTask", "ActivityTask: natty was not able to parse activityTask with ID:" + activityTaskID);
        }

        String[] words = content.split(" ");
        int  score = 0;
        for (String word :
                words) {
            score +=  priorityWords.getOrDefault(word,0);
        }
        setPriority(score);

        if(this.getTimePack().readTimeTange().isEmpty()){
            getTimePack().readTimeTange().set(0,timePack.readNattyResults());
            getTimePack().readTimeTange().set(1,timePack.readNattyResults());
        }
        getTimePack().reCalculateReleventDates();

    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean editReminder(String newContent, MasloCategory newMasloCategory, Repetition newRepetition){
        setMasloCategory(newMasloCategory);
        setContent(newContent);
        getTimePack().setRepetition(newRepetition);
        return true;
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
