package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A helper class to centralize time operations.
 */
public class TimePack {


    /**
     * the time of day i.e 21:00 to 22:00
     */
    private LocalDateTime[][] timeRange;
    /**
     * range of dates, i.e : [Aug 10]  [aug 29], [feb 6] [feb 16], ... and so on.
     */
    private LocalDateTime[][] dateRange;
    /**
     * January will be 1 February will be 2 and so on... tip: can get current month number with YearMonth.now().getMonth().getValue()
     */
    private int monthNumber;
    /**
     * the dates at which the TimePack instance is relevant to.
     */
    private Map<LocalDate,Boolean> monthRange;
    /**
     * if the TimePack overlaps with the next month, this will have the same purpose as monthRange but for the month after it.
     */
    private Map<LocalDateTime,Boolean> overlapedMonth;
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

    public void setNattyResults(LocalDateTime nattyResults) {
        this.nattyResults = nattyResults;
    }

    public TimePack() {
    }

    public TimePack(LocalDateTime[][] timeRange, int monthNumber, Map<LocalDate, Boolean> monthRange, Map<LocalDateTime, Boolean> overlapedMonth) {



        this.timeRange = timeRange;
        this.dateRange = new LocalDateTime[YearMonth.of(Year.now().getValue(),monthNumber).getMonth().maxLength()][];
        this.monthNumber = monthNumber;
        this.monthRange = monthRange;
        this.overlapedMonth = overlapedMonth;

        notificationCounter = notificationCounter +1;

    }

    public void setRepetition(Repetition repetition) {
        this.repetition = repetition;
    }
//what does notification need?


    public LocalDateTime[][] getTimeRange() {
        return timeRange;
    }

    public LocalDateTime[][] getDateRange() {
        return dateRange;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public Map<LocalDate, Boolean> getMonthRange() {
        return monthRange;
    }

    public Map<LocalDateTime, Boolean> getOverlapedMonth() {
        return overlapedMonth;
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
}
