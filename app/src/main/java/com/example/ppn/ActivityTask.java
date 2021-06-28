package com.example.ppn;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
}
