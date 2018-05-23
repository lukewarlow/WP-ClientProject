package com.nsa.teamtwo.welshpharmacy.data.reminder;

import java.util.Calendar;

public class Date {
    private int day, month, year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Date(String date) {
        String[] splitUp = date.split("/");
        try {
            this.day = Integer.valueOf(splitUp[0]);
            this.month = Integer.valueOf(splitUp[1]);
            this.year = Integer.valueOf(splitUp[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public Date(Calendar today) {
        this(today.get(Calendar.DAY_OF_MONTH),
                today.get(Calendar.MONTH),
                today.get(Calendar.YEAR));
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%d/%d/%d", day, month, year);
    }
}
