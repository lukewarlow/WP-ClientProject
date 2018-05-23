package com.nsa.teamtwo.welshpharmacy.data.reminder;

import java.util.Calendar;

public class Time {
    private int hour, minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(String time) {
        String[] splitUp = time.split(":");
        try {
            this.hour = Integer.valueOf(splitUp[0]);
            this.minute = Integer.valueOf(splitUp[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public Time(Calendar now) {
        this(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public String toString() {
        String res;
        if (minute < 10) {
            res = String.format("%d:0%d", hour, minute);
        } else {
            res = String.format("%d:%d", hour, minute);
        }
        return res;
    }
}

