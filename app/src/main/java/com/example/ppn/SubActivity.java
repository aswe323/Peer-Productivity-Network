package com.example.ppn;


/**
 * designed as a subclass for {@link ActivityTask} to help the user separate tasks into smaller actions
 *
 */
public class SubActivity {
    /**
     * the discription of the action to be done.
     */
    private String content;
    /**
     * the holding {@link ActivityTask#activityTaskID}
     */
    private int activityTaskID;

    /**
     *
     * @param content description of the SubActivity
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return the description of the SubActivity
     */
    public String getContent() {
        return content;
    }

    /**
     * firestore requires an empty constructor to rebuild classes.
     */
    public SubActivity() {
    }

    /**
     *
     * @param content the description of the SubActivity
     * @param activityTaskID the {@link ActivityTask#activityTaskID} of the {@link SubActivity}
     */
    public SubActivity(String content, int activityTaskID) {
        this.content = content;
        this.activityTaskID = activityTaskID;
    }

    /**
     *
     * @return the activityTaskID this SubActivity is related to.
     */
    public int getActivityTaskID() {
        return activityTaskID;
    }
}
