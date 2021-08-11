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
 * <p><b>note:</b> it is easyer to work with the already supported fields in firestore, hench the heavy use of Strings</p>
 *
 */
public class TimePack {

    private static final String TAG = "TimePack";
    /**
     * a String form of localedatetime, the starting time of the timepack.
     */
    private String startingTime;
    /**
     *       a String form of localedatetime, the ending time of the timepack.
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
    private String stringifiedNattyResults;
    /**
     * number correlating to this months days, if the number exist in the array the {@link ActivityTask} is relevant to that date.
     */
    private ArrayList<Integer> relaventDatesNumbered = new ArrayList<>();
    /**
     * firestore requires empty constructor to create class
     */
    public TimePack() {
    }
    /**
     * <p>sets the starting time and ending time of the {@link #startingTime} and {@link #endingTime} according to the first and secound elements of timeRange paramater, any other elements are not used</p>
     * <p>uses the repetition parmeter to calculate {@link #relaventDatesNumbered} form the starting time recived.</p>
     *<p>{@link #stringifiedNattyResults} is set by default to now, incase natty will not be able to parse {@link ActivityTask#content} for a date</p>
     * @param timeRange expected to hold 2 values, the first is the starting time of the {@link TimePack} the second is the ending time
     * @param monthNumber the month number where this {@link TimePack} is relevant to. 1-12
     * @param repetition {@link Repetition} as to how the {@link TimePack} is to be repeated.
     * @param strigifiedRelaventDates stringified {@link LocalDateTime}s in the format {@link #getFormatter()} gives.
     */
    public TimePack(ArrayList<LocalDateTime> timeRange, int monthNumber,Repetition repetition,ArrayList<String> strigifiedRelaventDates) {

        try {
            this.startingTime = timeRange.get(0).format(getFormatter());
            this.endingTime = timeRange.get(1).format(getFormatter());

        }catch (Exception e){
            Log.d(TAG, "TimePack: wrong LocalDateTime format, use TimePack.getFormatter() to make sure it's the right one.");
        }
        

        setMonthNumber(monthNumber);
        setRepetition(repetition);
        setStrigifiedRelaventDates(strigifiedRelaventDates);
        this.stringifiedNattyResults = LocalDateTime.now().format(getFormatter());

        notificationCounter = notificationCounter +1;
        for (String string:
                strigifiedRelaventDates) {
            relaventDatesNumbered.add(LocalDateTime.parse(string,getFormatter()).getDayOfMonth());
        }

    }

    /**
     *
     * <p>calculates relevant dates according to the repetition parameter and adds the one sin the relevantDates parameter</p>
     * <p>sets {@link #stringifiedNattyResults} to {@link LocalDateTime#now()} using {@link #getFormatter()}</p>
     * <p></p>
     * @param startingTime starting time of the {@link TimePack}.
     * @param endingTime ending time of the {@link TimePack}.
     * @param monthNumber the month number relevant for this {@link TimePack}.
     * @param repetition {@link Repetition} as to represent how this {@link TimePack} is to be repeated.
     * @param relaventDates an array reprasenting the relavent dates to which this {@link TimePack } is relevant to.
     */
    public TimePack(LocalDateTime startingTime,LocalDateTime endingTime, int monthNumber,Repetition repetition,ArrayList<LocalDateTime> relaventDates) {

        setStartingTime(startingTime.format(getFormatter()));
        setEndingTime(endingTime.format(getFormatter()));



        this.monthNumber = monthNumber;
        setRepetition(repetition);
        updateRelaventDates(relaventDates);
        this.stringifiedNattyResults = LocalDateTime.now().format(getFormatter());

        notificationCounter = notificationCounter +1;
        for (String string:
                strigifiedRelaventDates) {
            relaventDatesNumbered.add(LocalDateTime.parse(string,getFormatter()).getDayOfMonth());
        }

    }

    /**
     * a class ment to be used with data returned from firestore, should only be used if something gone wrong and there is a problem with {@link com.google.firebase.firestore.DocumentSnapshot#toObject(Class)}
     * @param mappedData the mapped data that build the TimePack, keys are the member names and values are their content.
     */
    public TimePack(HashMap<String ,Object> mappedData) {
        this.startingTime = (String) mappedData.get("startingTime");
        this.endingTime = (String) mappedData.get("startingTime");
        this.monthNumber = ((Long) mappedData.get("monthNumber")).intValue();
        this.strigifiedRelaventDates = (ArrayList<String>) mappedData.get("strigifiedRelaventDates");
        this.stringifiedNattyResults = (String) mappedData.get("strigifiedNattyResults");
        this.repetition = Repetition.valueOf((String) mappedData.get("repetition"));
        this.notificationID = ((Long) mappedData.get("notificationID")).intValue();
    }

    /**
     * converts {@link LocalDateTime} to a string and assings to {@link #stringifiedNattyResults} using {@link #getFormatter()}
     * @param nattyResults
     */
    public void updateNattyResults(LocalDateTime nattyResults) {
        this.stringifiedNattyResults = nattyResults.format(getFormatter());
    }

    /**
     * setter for {@link #stringifiedNattyResults}
     * @param stringifiedNattyResults
     */
    public void setStringifiedNattyResults(String stringifiedNattyResults) {
        this.stringifiedNattyResults = stringifiedNattyResults;
    }

    /**
     * getter for {@link #stringifiedNattyResults}
     * @return {@link #stringifiedNattyResults}
     */
    public String getStringifiedNattyResults() {
        return stringifiedNattyResults;
    }

    /**
     * adds relevant dates to {@link #relaventDatesNumbered} according to {@link #repetition}
     */
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

    /**
     *
     * @return an {@link ArrayList} of {@link LocalDateTime} containt the starting time in position 0 and ending time in position 1
     */
    public ArrayList<LocalDateTime> readTimeTange() {
        ArrayList<LocalDateTime> timeRange = new ArrayList<>();
        timeRange.add(LocalDateTime.parse(startingTime,getFormatter()));
        timeRange.add(LocalDateTime.parse(endingTime,getFormatter()));

        return timeRange;
    }

    /**
     * @return {@link LocalDateTime} got from the content of the {@link ActivityTask} related to this TimePack, if it is not relevent to an {@link ActivityTask} or natty could not parse a date, it will be the result of the {@link LocalDateTime#now()} at the time of creation
     */
    public LocalDateTime readNattyResults() {
        return LocalDateTime.parse(stringifiedNattyResults,getFormatter());
    }
    /**
     * used to parse and covert strings into {@link LocalDateTime}, to remain consistent with setting and getting.
     */
    public static DateTimeFormatter getFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    /**
     * @return {@link #startingTime}
     */
    public String getStartingTime() {
        return startingTime;
    }

    /**
     * @return {@link #endingTime}
     */
    public String getEndingTime() {
        return endingTime;
    }

    /**
     * !!DO NOT USE!!. is required for firebase to work with the class.
     * @param notificationID {@link #notificationID}
     */
    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    /**
     *
     * @param startingTime the strigified {@link LocalDateTime} formated with {@link #getFormatter()}
     */
    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    /**
     *
     * @param endingTime the stringified {@link LocalDateTime} foramted with {@link #getFormatter()}
     */
    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public static String getTAG() {
        return TAG;
    }

    /**
     * @return {@link #relaventDatesNumbered}
     */
    public ArrayList<Integer> getRelaventDatesNumbered() {
        return relaventDatesNumbered;
    }

    /**
     *  mainly used by firestore
     * @param relaventDatesNumbered an array of numbers corresponding to this {@link TimePack} relevant dates for this month
     */
    public void setRelaventDatesNumbered(ArrayList<Integer> relaventDatesNumbered) {
        this.relaventDatesNumbered = relaventDatesNumbered;
    }

    /**
     * adds the relevant dates array of {@link LocalDateTime} to the
     * @param relaventDates
     */
    public void updateRelaventDates(ArrayList<LocalDateTime> relaventDates) {
        if (relaventDates != null) {


            ArrayList<String> settedStrings = new ArrayList<>();

            for (LocalDateTime localDateTime :
                    relaventDates) {
                settedStrings.add(localDateTime.format(getFormatter()));
            }
            strigifiedRelaventDates.addAll(settedStrings);
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
