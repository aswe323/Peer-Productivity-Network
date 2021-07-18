package com.example.ppn;

import android.icu.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A helper class to centralize time operations.
 */
public class TimePack {

    /**
     * the time of day i.e 21:00 to 22:00, only index 0 and 1 are used. if no timeRange is specified (isEmpty is true during activityTask instantiation)natty will be assigned to both index 0 and 1.
     */
    private ArrayList<LocalDateTime> timeRange;
    /**
     * January will be 1 February will be 2 and so on... tip: can get current month number with YearMonth.now().getMonth().getValue()
     */
    private int monthNumber;
    /**
     * the dates at which the TimePack instance is relevant to.
     */
    private ArrayList<LocalDate> relaventDates;
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
    private final int notificationID = notificationCounter+1;
    /**
     * natty results from parsing the content of the related activityTask
     */
    private LocalDateTime nattyResults;



    public TimePack() {
    }

    public TimePack(ArrayList<LocalDateTime> timeRange, int monthNumber,Repetition repetition,ArrayList<LocalDate> relaventDates) {

        this.timeRange = timeRange;
        this.monthNumber = monthNumber;
        this.repetition = repetition;
        this.relaventDates = relaventDates;

        notificationCounter = notificationCounter +1;

    }

    public ArrayList<LocalDate> getRelaventDates() {
        return relaventDates;
    }

    public void setRelaventDates(ArrayList<LocalDate> relaventDates) {
        this.relaventDates = relaventDates;
    }

    public void reCalculateReleventDates(){
        ArrayList<LocalDate> newRelaventDates = new ArrayList<>();

        switch (this.repetition){
            case No_repeting: return;
            //region Every_24_hours
            case Every_24_hours:
                newRelaventDates.add(timeRange.get(0).toLocalDate());
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusDays(1));
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusDays(2));
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusDays(3));
                return;
            //endregion
            //region every_week
            case every_week:
                newRelaventDates.add(timeRange.get(0).toLocalDate());
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusWeeks(1));
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusWeeks(2));
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusWeeks(3));
                return;
            //endregion
            //region every_year
            case every_year:
                newRelaventDates.add(timeRange.get(0).toLocalDate());
                newRelaventDates.add(timeRange.get(0).toLocalDate().plusYears(1));
                return;
            //endregion
            //region every_monday
            case every_monday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
                return;
            //endregion
            //region every_satuday
            case every_satuday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.SATURDAY)));
                return;
            //endregion
            //region every_friday
            case every_friday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
                return;
            //endregion
            //region every_sunday
            case every_sunday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
                return;
            //endregion
            //region every_tuesday
            case every_tuesday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.TUESDAY)));
                return;
            //endregion
            //region every_thursday
            case every_thursday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)));
                return;
            //endregion
            //region every_wednesday
            case every_wednesday:
                newRelaventDates.add(timeRange.get(0).toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(0).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(1).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                newRelaventDates.add(newRelaventDates.get(2).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)));
                return;
            //endregion
        }

        setRelaventDates(newRelaventDates);

    }

    public LocalDateTime getNattyResults() {
        return nattyResults;
    }

    public void setNattyResults(LocalDateTime nattyResults) {
        this.nattyResults = nattyResults;
    }

    public void setRepetition(Repetition repetition) {

        this.repetition = repetition;
        reCalculateReleventDates();
    }
//what does notification need?

    public ArrayList<LocalDateTime> getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(ArrayList<LocalDateTime> timeRange) {
        this.timeRange = timeRange;
    }

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
