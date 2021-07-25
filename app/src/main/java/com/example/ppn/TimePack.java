package com.example.ppn;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A helper class to centralize time operations.
 */
public class TimePack {

    private static final String TAG = "TimePack";
    /**
     * a String form of localedatetime, the starting time of the timepack.
     */
    private String startingTime;
    /**
     *      * a String form of localedatetime, the ending time of the timepack.
     */
    private String endingTime;
    /**
     * January will be 1 February will be 2 and so on... tip: can get current month number with YearMonth.now().getMonth().getValue()
     */
    private int monthNumber;
    /**
     * the dates at which the TimePack instance is relevant to.
     */
    private ArrayList<String> strigifiedRelaventDates;
    /**
     * represents the repetition at which the TimePack is relevant to.
     */
    private Repetition repetition;
    /**
     * used for a unique identification of an INSTANCED timepack. may result in issues if used across multiple unique identifier requiring systems.
     */
    private static int notificationCounter;
    /**
     * internal counter, initialization with the unique identifier requiring system every time the TimePack CLASS is initialized.
     */
    private int notificationID = notificationCounter+1;
    /**
     * natty results from parsing the content of the related activityTask
     */
    private String strigifiedNattyResults;

    private ArrayList<Integer> relaventDatesNumbered = new ArrayList<>();// FIXME: 25/07/2021 is not inserted into firebase correctly. Possible issue with flow of setter/getter.





    public TimePack() {
    }

    public TimePack(ArrayList<LocalDateTime> timeRange, int monthNumber,Repetition repetition,ArrayList<String> strigifiedRelaventDates) {

        try {
            this.startingTime = timeRange.get(0).format(getFormatter());
            this.endingTime = timeRange.get(1).format(getFormatter());

        }catch (Exception e){
            Log.d(TAG, "TimePack: wrong LocalDateTime format, use TimePack.getFormatter() to make sure it's the right one.");
        }
        

        this.monthNumber = monthNumber;
        this.repetition = repetition;
        setStrigifiedRelaventDates(strigifiedRelaventDates);
        this.strigifiedNattyResults = LocalDateTime.now().format(getFormatter());

        notificationCounter = notificationCounter +1;
        for (String string:
                strigifiedRelaventDates) {
            relaventDatesNumbered.add(LocalDateTime.parse(string,getFormatter()).getDayOfMonth());
        }

    }

    public TimePack(HashMap<String ,Object> mappedData) {



        this.startingTime = (String) mappedData.get("startingTime");
        this.endingTime = (String) mappedData.get("startingTime");
        this.monthNumber = ((Long) mappedData.get("monthNumber")).intValue();
        this.strigifiedRelaventDates = (ArrayList<String>) mappedData.get("strigifiedRelaventDates");
        this.strigifiedNattyResults = (String) mappedData.get("strigifiedNattyResults");
        this.repetition = Repetition.valueOf((String) mappedData.get("repetition"));
        this.notificationID = ((Long) mappedData.get("notificationID")).intValue();

    }

    public void updateNattyResults(LocalDateTime nattyResults) {
        this.strigifiedNattyResults = nattyResults.format(getFormatter());
    }

    public void setStrigifiedNattyResults(String strigifiedNattyResults) {
        this.strigifiedNattyResults = strigifiedNattyResults;
    }

    public String getStrigifiedNattyResults() {
        return strigifiedNattyResults;
    }

    public void reCalculateReleventDates(){
        ArrayList<LocalDateTime> newRelaventDates = new ArrayList<>();



        switch (this.repetition){
            case No_repeting: return;
            //region Every_24_hours
            case Every_24_hours:
                newRelaventDates.add(readTimeTange().get(0));
                newRelaventDates.add(readTimeTange().get(0).plusDays(1));
                newRelaventDates.add(readTimeTange().get(0).plusDays(2));
                newRelaventDates.add(readTimeTange().get(0).plusDays(3));
                return;
            //endregion

            //region every_week
            case every_week:
                newRelaventDates.add(readTimeTange().get(0));
                newRelaventDates.add(readTimeTange().get(0).plusWeeks(1));
                newRelaventDates.add(readTimeTange().get(0).plusWeeks(2));
                newRelaventDates.add(readTimeTange().get(0).plusWeeks(3));
                return;
            //endregion
            //region every_year
            case every_year:
                newRelaventDates.add(readTimeTange().get(0));
                newRelaventDates.add(readTimeTange().get(0).plusYears(1));
                return;
            //endregion
            //region every_monday
            case every_monday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                return;
            //endregion
            //region every_satuday
            case every_satuday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                return;
            //endregion
            //region every_friday
            case every_friday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                return;
            //endregion
            //region every_sunday
            case every_sunday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                return;
            //endregion
            //region every_tuesday
            case every_tuesday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                return;
            //endregion
            //region every_thursday
            case every_thursday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                return;
            //endregion
            //region every_wednesday
            case every_wednesday:
                newRelaventDates.add(readTimeTange().get(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                return;
            //endregion
        }

        updateRelaventDates(newRelaventDates);

    }

    public ArrayList<LocalDateTime> readTimeTange() {
        ArrayList<LocalDateTime> timeRange = new ArrayList<>();
        timeRange.add(LocalDateTime.parse(startingTime,getFormatter()));
        timeRange.add(LocalDateTime.parse(endingTime,getFormatter()));

        return timeRange;
    }

    public LocalDateTime readNattyResults() {
        return LocalDateTime.parse(strigifiedNattyResults,getFormatter());
    }
    /**
     * used to parse and covert strings into LocalDateTime
     */
    public static DateTimeFormatter getFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    /**
     * !!DO NOT USE!!. is required for firebase to work with the class.
     * @param notificationID
     */
    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public static String getTAG() {
        return TAG;
    }

    public ArrayList<Integer> getRelaventDatesNumbered() {
        return relaventDatesNumbered;
    }

    public void setRelaventDatesNumbered(ArrayList<Integer> relaventDatesNumbered) {
        this.relaventDatesNumbered = relaventDatesNumbered;
    }

    public void updateRelaventDates(ArrayList<LocalDateTime> relaventDates) {

        ArrayList<String> settedStrings = new ArrayList<>();

        for (LocalDateTime localDateTime :
                relaventDates) {
            settedStrings.add(localDateTime.format(getFormatter()));
        }
    }
    public ArrayList<LocalDateTime> readRelaventDates() {

        ArrayList<LocalDateTime> returned = new ArrayList<>();

        for (String strigifiedLocalDateTime :
                strigifiedRelaventDates) {
            returned.add(LocalDateTime.parse(strigifiedLocalDateTime,getFormatter()));
        }

        return returned;    }


    public ArrayList<String> getStrigifiedRelaventDates() {
        return strigifiedRelaventDates;
    }

    public void setStrigifiedRelaventDates(ArrayList<String> strigifiedRelaventDates) {
        this.strigifiedRelaventDates = strigifiedRelaventDates;
    }




    public void setRepetition(Repetition repetition) {

        this.repetition = repetition;
        reCalculateReleventDates();
    }
//what does notification need?



    public int getMonthNumber() {
        return monthNumber;
    }

    public Repetition getRepetition() {
        return repetition;
    }

    public static int getNotificationCounter() {
        return notificationCounter;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public static void setNotificationCounter(int notificationCounter) {
        TimePack.notificationCounter = notificationCounter;
    }


}
