package com.example.ppn;

public class SubActivity {
    private String content;
    private int subActivityID ;
    private int activityTaskID;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public SubActivity() {
    }

    public SubActivity(String content, int activityTaskID) {
        this.content = content;
        this.activityTaskID = activityTaskID;
    }

    public int getSubActivityID() {
        return subActivityID;
    }

    public int getActivityTaskID() {
        return activityTaskID;
    }
}
