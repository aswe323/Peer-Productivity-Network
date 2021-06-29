package com.example.ppn;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ActivityTask {
    private int activityTaskID;
    private MasloCategory masloCategory;
    private String content;
    private ArrayList<SubActivity> subActivity;
    private int priority;
    private TimePack timePack;

    public ActivityTask() {
    }

    public ActivityTask(int activityTaskID, MasloCategory masloCategory, String content, ArrayList<SubActivity> subActivity, TimePack timePack,@NonNull Map<String, Integer> priorityWords) {
        this.activityTaskID = activityTaskID;
        this.masloCategory = masloCategory;
        this.subActivity = subActivity;
        this.content = content;
        this.timePack = timePack;
        
        List<DateGroup> groups;
        Parser parser = new Parser();
        groups = parser.parse(content);
        if (groups.size()>0){//check if i got a date

            List dates = groups.get(0).getDates();//get the date that natty created for us
            LocalDateTime localDateTime = ((Date) dates.get(0)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); //convert it to LocalDateTIme
            this.timePack.setNattyResults(localDateTime);

        }else{
            this.timePack.setNattyResults(LocalDateTime.now());
            Log.d("activityTask,natty", "ActivityTask: natty was not able to parse activityTask with ID:" + activityTaskID);
        }

        String[] words = content.split(" ");
        int  score = 0;
        for (String word :
                words) {
            score +=  priorityWords.getOrDefault(word,0);
        }
        this.priority = score;

    }

    public boolean editReminder(String newContent,MasloCategory newMasloCategory,Repetition newRepetition){
        this.timePack.setRepetition(newRepetition);
        this.content = newContent;
        this.masloCategory = newMasloCategory;
        return true;
    }

    public int size() {
        return subActivity.size();
    }

    public boolean add(SubActivity subActivity) {
        return this.subActivity.add(subActivity);
    }

    public SubActivity removeSubActivityAtIndex(int index) {
        return subActivity.remove(index);
    }

    public boolean removeSubActivityIf(@NonNull @NotNull Predicate<? super SubActivity> filter) {
        return subActivity.removeIf(filter);
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

    public ArrayList<SubActivity> getSubActivity() {
        return subActivity;
    }

    public int getPriority() {
        return priority;
    }

    public TimePack getTimePack() {
        return timePack;
    }



    enum compareResult{sameContent,lowerPriority,higherPriority,samePriority,sameBucketWords,sameNattyResult,none}
    public Map<String,compareResult> compare(ActivityTask activityTask) {
        Map<String ,compareResult> results = new HashMap<>();


        if(this.content.equals(activityTask.getContent())) results.put("content",compareResult.sameContent);
        if(this.priority > activityTask.getPriority()) results.put("priority",compareResult.higherPriority);
        if(this.priority < activityTask.getPriority()) results.put("priority",compareResult.lowerPriority);
        if(this.priority == activityTask.getPriority()) results.put("priority",compareResult.samePriority);



        return results;
    }
}
