package com.nsa.teamtwo.welshpharmacy.data.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReminderDB extends SQLiteOpenHelper {

    private static final String TAG = "ReminderDB";

    private static final String TABLE_NAME = "reminder_table";
    private static final String COL2 = "reminder";
    private static final String COL3 = "date";
    private static final String COL4 = "time";
    private static final String COL5 = "repeats";
    private static final String COL6 = "interval";
    private static final String COL7 = "type";
    private static final String COL8 = "notificationActive";

    public ReminderDB(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = String.format(
                "CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_NAME,
                COL2,
                COL3,
                COL4,
                COL5,
                COL6,
                COL7,
                COL8);
        db.execSQL(createTable);
    }

    public boolean addReminder(Reminder reminder) {
        Reminder checkUniqueness = getReminder(reminder.getReminderText(), reminder.getDate().toString(), reminder.getTime().toString());
        if (checkUniqueness == null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, reminder.getReminderText());
            contentValues.put(COL3, reminder.getDate().toString());
            contentValues.put(COL4, reminder.getTime().toString());
            contentValues.put(COL5, String.valueOf(reminder.shouldRepeat()));
            contentValues.put(COL6, reminder.getRepeatInterval());
            contentValues.put(COL7, reminder.getRepeatType().toString());
            contentValues.put(COL8, String.valueOf(reminder.isNotificationActive()));

            Log.d(TAG, "Adding data");

            long result = db.insert(TABLE_NAME, null, contentValues);

//        Checking if the data has been inserted safely
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            Log.d(TAG, "Reminder is not unique");
            return false;
        }
    }

    public ArrayList<Reminder> getReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Reminder> reminders = new ArrayList<>();
        while (data.moveToNext()) {
            Reminder reminder = new Reminder();
            reminder.setReminderText(data.getString(1));
            reminder.setDate(new Date(data.getString(2)));
            reminder.setTime(new Time(data.getString(3)));
            reminder.setShouldRepeat(Boolean.valueOf(data.getString(4)));
            reminder.setRepeatInterval(Integer.valueOf(data.getString(5)));
            reminder.setRepeatType(RepeatType.valueOf(data.getString(6)));
            reminder.setNotificationActive(Boolean.valueOf(data.getString(7)));
            reminders.add(reminder);
        }
        data.close();
        return reminders;
    }

    public Reminder getReminder(String reminderText, String date, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s=? AND %s=? AND %s=?", TABLE_NAME, COL2, COL3, COL4);
        String[] args = new String[] {
                reminderText,
                date,
                time
        };
        Cursor data = db.rawQuery(query, args);
        Reminder reminder = null;
        while (data.moveToNext()) {
            reminder = new Reminder();
            reminder.setReminderText(data.getString(1));
            reminder.setDate(new Date(data.getString(2)));
            reminder.setTime(new Time(data.getString(3)));
            reminder.setShouldRepeat(Boolean.valueOf(data.getString(4)));
            reminder.setRepeatInterval(Integer.valueOf(data.getString(5)));
            reminder.setRepeatType(RepeatType.valueOf(data.getString(6)));
            reminder.setNotificationActive(Boolean.valueOf(data.getString(7)));
        }
        data.close();
        return reminder;
    }

    public void editReminder(Reminder originalReminder, Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(
                "UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=? AND %s=? AND %s=?",
                TABLE_NAME,
                COL2,
                COL3,
                COL4,
                COL5,
                COL6,
                COL7,
                COL8,
                COL2,
                COL3,
                COL4);

        String[] args = new String[] {
                reminder.getReminderText(),
                reminder.getDate().toString(),
                reminder.getTime().toString(),
                String.valueOf(reminder.shouldRepeat()),
                String.valueOf(reminder.getRepeatInterval()),
                reminder.getRepeatType().toString(),
                String.valueOf(reminder.isNotificationActive()),
                originalReminder.getReminderText(),
                originalReminder.getDate().toString(),
                originalReminder.getTime().toString()
        };
        db.execSQL(query, args);
    }

    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format(
                "DELETE FROM %s WHERE %s=? AND %s=? AND %s=?",
                TABLE_NAME,
                COL2,
                COL3,
                COL4);

        String[] args = new String[] {
                reminder.getReminderText(),
                reminder.getDate().toString(),
                reminder.getTime().toString(),
        };
        db.execSQL(query, args);
    }
}
