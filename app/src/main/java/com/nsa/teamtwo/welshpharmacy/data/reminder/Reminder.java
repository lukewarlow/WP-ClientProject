package com.nsa.teamtwo.welshpharmacy.data.reminder;

public class Reminder {
    private String reminderText;
    private Date date;
    private Time time;
    private boolean shouldRepeat;
    private int repeatInterval;
    private RepeatType repeatType;
    private boolean notificationActive;

    public Reminder(String reminderText, Date date, Time time, boolean shouldRepeat, int repeatInterval, RepeatType repeatType, boolean notificationActive) {
        this.reminderText = reminderText;
        this.date = date;
        this.time = time;
        this.shouldRepeat = shouldRepeat;
        this.repeatInterval = repeatInterval;
        this.repeatType = repeatType;
        this.notificationActive = notificationActive;
    }

    public Reminder() {}

    public String getReminderText() {
        return reminderText;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean shouldRepeat() {
        return shouldRepeat;
    }

    public void setShouldRepeat(boolean shouldRepeat) {
        this.shouldRepeat = shouldRepeat;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public long getRepeatIntervalMillis() {
        long res = repeatInterval;
        switch (repeatType) {
            case MINUTE:
                res *= 60;
                break;
            case HOUR:
                res *= 3600;
                break;
            case DAY:
                res *= 86400;
                break;
            case WEEK:
                res *= 604800;
                break;
            case MONTH:
                res *= 2629746;
                break;
        }
        return res * 1000;
    }

    public boolean isNotificationActive() {
        return notificationActive;
    }

    public void setNotificationActive(boolean notificationActive) {
        this.notificationActive = notificationActive;
    }

    public void toggleNotficationActive() {
        notificationActive = !notificationActive;
    }
}

