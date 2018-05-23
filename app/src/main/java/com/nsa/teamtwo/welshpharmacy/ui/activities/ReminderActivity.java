package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Date;
import com.nsa.teamtwo.welshpharmacy.data.reminder.NotificationReceiver;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Reminder;
import com.nsa.teamtwo.welshpharmacy.data.reminder.ReminderDB;
import com.nsa.teamtwo.welshpharmacy.data.reminder.RepeatType;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Time;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Adapted from: https://www.youtube.com/watch?v=P46LTiPlvUA
 * Accessed on 13/04/2018 at 21:44
 */
public class ReminderActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Reminder reminder;
    private Reminder originalReminder;
    private EditText reminderText;
    private TextView dateText, timeText, repeatText, repeatNoText, repeatTypeText;
    private Switch repeatSwitch;
    private FloatingActionButton notificationFab;
    private ReminderDB reminderDB;
    private boolean isNew;

    private static final String TAG = "Add Reminder Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        isNew = true;

        Calendar now = Calendar.getInstance();
        reminder = new Reminder(
                "",
                new Date(now),
                new Time(now),
                false,
                1,
                RepeatType.HOUR,
                true);

        reminderDB = new ReminderDB(this);
        String reminderTxt = getIntent().getStringExtra("reminderText");
        if (reminderTxt != null && !reminderTxt.isEmpty()) {
            String date = getIntent().getStringExtra("date");
            String time = getIntent().getStringExtra("time");
            if (date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
                isNew = false;
                reminder = reminderDB.getReminder(reminderTxt, date, time);
                originalReminder = reminderDB.getReminder(reminderTxt, date, time);
            }
        }

        ImageView dateImage = findViewById(R.id.image_date);
        dateImage.setImageDrawable(Util.getThemedDrawable(this, R.drawable.ic_date_24dp));
        dateText = findViewById(R.id.set_date);
        dateText.setText(reminder.getDate().toString());

        ImageView timeImage = findViewById(R.id.image_time);
        timeImage.setImageDrawable(Util.getThemedDrawable(this, R.drawable.ic_time_24dp));
        timeText = findViewById(R.id.set_time);
        timeText.setText(reminder.getTime().toString());

        ImageView repeatImage = findViewById(R.id.image_repeat);
        repeatImage.setImageDrawable(Util.getThemedDrawable(this, R.drawable.ic_repeat_24dp));
        repeatText = findViewById(R.id.set_repeat);
        if (reminder.getRepeatInterval() > 1) {
            repeatText.setText(String.format(getString(R.string.repeat_with_interval), String.valueOf(reminder.getRepeatInterval()), reminder.getRepeatType().getName(this)));
        } else {
            repeatText.setText(String.format(getString(R.string.repeat_without_interval), reminder.getRepeatType().getName(this)));
        }

        ImageView repeatNoImage = findViewById(R.id.image_repeat_no);
        repeatNoImage.setImageDrawable(Util.getThemedDrawable(this, R.drawable.ic_repeat_no_24dp));
        repeatNoText = findViewById(R.id.set_repeat_no);
        repeatNoText.setText(String.valueOf(String.valueOf(reminder.getRepeatInterval())));

        ImageView repeatTypeImage = findViewById(R.id.image_repeat_type);
        repeatTypeImage.setImageDrawable(Util.getThemedDrawable(this, R.drawable.ic_repeat_type_24dp));
        repeatTypeText = findViewById(R.id.set_repeat_type);
        repeatTypeText.setText(reminder.getRepeatType().getName(this));

        RelativeLayout dateGroup = findViewById(R.id.date);
        dateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        RelativeLayout timeGroup = findViewById(R.id.time);
        timeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

        RelativeLayout repeatNoGroup = findViewById(R.id.RepeatNo);
        repeatNoGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatNo(v);
            }
        });

        RelativeLayout repeatTypeGroup = findViewById(R.id.RepeatType);
        repeatTypeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatType(v);
            }
        });

        repeatSwitch = findViewById(R.id.switch_repeat);
        repeatSwitch.setChecked(reminder.shouldRepeat());
        onRepeatSwitchChanged(reminder.shouldRepeat());
        repeatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRepeatSwitchClick(v);
            }
        });

        notificationFab = findViewById(R.id.fab_notification);
        if (reminder.isNotificationActive()) {
            notificationFab.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        }
        notificationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationFabClicked(v);
            }
        });

        reminderText = findViewById(R.id.text_reminder);
        reminderText.setText(reminder.getReminderText());
        reminderText.setSelection(reminder.getReminderText().length());
        reminderText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setReminderText(s.toString().trim());
                reminderText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            boolean reminderHasChanged = !reminder.equals(originalReminder);
            if (!reminderHasChanged) {
                //If there are no changes, close activity.
                finish();
            } else {
                // Otherwise, Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(false);
            }
            return true;
        } else if (item.getItemId() == R.id.save_reminder) {
            if (reminderText.getText().toString().length() == 0) {
                reminderText.setError(getString(R.string.must_enter_something));
            } else {
                boolean close = true;
                if (isNew) {
                    close = saveReminder();
                } else {
                    editReminder();
                }
                if (close) finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean reminderHasChanged = !reminder.equals(originalReminder);
        if (!reminderHasChanged) {
            //If there are no changes, close activity.
            finish();
            super.onBackPressed();
        } else {
            // Otherwise, Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(true);
        }
    }

    private boolean saveReminder() {
        Boolean successful = reminderDB.addReminder(reminder);
        if (successful) {
            Util.shortToast(this, R.string.reminder_successfully_saved);
            if (reminder.isNotificationActive()) {
                //From https://stackoverflow.com/questions/44216509/how-to-display-notification-at-a-particular-time?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); //request alarm service
                Intent notificationAlert = new Intent(this, NotificationReceiver.class); //intent to connection at broadcastReceiver class
                notificationAlert.putExtra("reminderText", reminder.getReminderText());
                String tag = reminder.getReminderText() + reminder.getDate().toString() + reminder.getTime().toString();
                Log.d(TAG, tag);
                notificationAlert.putExtra("reminderID", tag);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationAlert, PendingIntent.FLAG_UPDATE_CURRENT); //request broadcast in AlertNotification class
                try {
                    Calendar calendar = Calendar.getInstance();
                    Date date = reminder.getDate();
                    Time time = reminder.getTime();
                    calendar.set(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute());
                    if (!reminder.shouldRepeat()) {
                        alarmManager.set(
                                AlarmManager.RTC_WAKEUP, //alarm time in system
                                calendar.getTimeInMillis(), //start milliseconds
                                pendingIntent
                        );
                    } else {
                        alarmManager.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP, //alarm time in system
                                calendar.getTimeInMillis(), //start milliseconds
                                reminder.getRepeatIntervalMillis(),
                                pendingIntent
                        );
                    }
                    Log.d(TAG, "Notification set");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "notification disabled");
            }
        } else {
            Util.shortToast(this, R.string.something_went_wrong);
            Log.e(TAG, "Error when saving reminder.");
        }
        return successful;
    }

    private void editReminder() {
        reminderDB.editReminder(originalReminder, reminder);
        Util.shortToast(this, R.string.reminder_edited_successfully);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String tag = originalReminder.getReminderText() + originalReminder.getDate().toString() + originalReminder.getTime().toString();
        //From https://stackoverflow.com/questions/44216509/how-to-display-notification-at-a-particular-time?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); //request alarm service
        Intent notificationAlert = new Intent(this, NotificationReceiver.class); //intent to connection at broadcastReceiver class
        notificationAlert.putExtra("reminderText", reminder.getReminderText());
        notificationAlert.putExtra("reminderID", tag);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationAlert, PendingIntent.FLAG_UPDATE_CURRENT); //request broadcast in AlertNotification class
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = reminder.getDate();
            Time time = reminder.getTime();
            calendar.set(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute());
            if (reminder.isNotificationActive()) {
                if (!reminder.shouldRepeat()) {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP, //alarm time in system
                            calendar.getTimeInMillis(), //start milliseconds
                            pendingIntent
                    );
                } else {
                    alarmManager.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP, //alarm time in system
                            calendar.getTimeInMillis(), //start milliseconds
                            reminder.getRepeatIntervalMillis(),
                            pendingIntent
                    );
                }
                Log.d(TAG, "Notification edited");
            } else {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "Notification delete");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showUnsavedChangesDialog(final boolean backPressed) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                if (backPressed) ReminderActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setDate(View v) {
        Date date = reminder.getDate();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                date.getYear(),
                date.getMonth(),
                date.getDay()
        );
        dpd.setMinDate(Calendar.getInstance());
        if (Util.isDarkTheme(this)) {
            dpd.setAccentColor(getTheme().getResources().getColor(R.color.colorSecondary));
            dpd.setThemeDark(true);
        } else {
            dpd.setAccentColor(getTheme().getResources().getColor(R.color.colorPrimary));
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(dayOfMonth, monthOfYear, year);
        reminder.setDate(date);

        dateText.setText(date.toString());
    }

    public void setTime(View v) {
        Time time = reminder.getTime();

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                time.getHour(),
                time.getMinute(),
                false);
        if (Util.isDarkTheme(this)) {
            tpd.setAccentColor(getTheme().getResources().getColor(R.color.colorSecondary));
            tpd.setThemeDark(true);
        } else {
            tpd.setAccentColor(getTheme().getResources().getColor(R.color.colorPrimary));
        }
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hour, int minute, int seconds) {
        Time time = new Time(hour, minute);
        reminder.setTime(time);
        timeText.setText(time.toString());
    }

    // On clicking the repeat switch
    public void onRepeatSwitchClick(View view) {
        boolean state = ((Switch) view).isChecked();
        reminder.setShouldRepeat(state);
        onRepeatSwitchChanged(state);
    }

    private void onRepeatSwitchChanged(Boolean state) {
        if (state) {
            if (reminder.getRepeatInterval() > 1) {
                repeatText.setText(String.format(getString(R.string.repeat_with_interval), String.valueOf(reminder.getRepeatInterval()), reminder.getRepeatType().getName(this)));
            } else {
                repeatText.setText(String.format(getString(R.string.repeat_without_interval), reminder.getRepeatType().getName(this)));
            }
        } else {
            repeatText.setText(R.string.repeat_off);
        }
    }

    // On clicking repeat interval button
    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.enter_number);

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(R.string.dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        repeatSwitch.setChecked(true);
                        reminder.setShouldRepeat(true);
                        if (input.getText().toString().length() == 0) {
                            reminder.setRepeatInterval(1);
                            repeatNoText.setText(String.valueOf(String.valueOf(reminder.getRepeatInterval())));
                            repeatText.setText(String.format(getString(R.string.repeat_without_interval), reminder.getRepeatType().getName(ReminderActivity.this)));
                        } else {
                            reminder.setRepeatInterval(Integer.valueOf(input.getText().toString().trim()));
                            repeatNoText.setText(String.valueOf(String.valueOf(reminder.getRepeatInterval())));
                            if (reminder.getRepeatInterval() > 1) {
                                repeatText.setText(String.format(getString(R.string.repeat_with_interval), String.valueOf(reminder.getRepeatInterval()), reminder.getRepeatType().getName(ReminderActivity.this)));
                            } else {
                                repeatText.setText(String.format(getString(R.string.repeat_without_interval), reminder.getRepeatType().getName(ReminderActivity.this)));
                            }
                        }
                    }
                });
        alert.setNeutralButton(R.string.dialog_close, null);
        alert.show();
    }

    // On clicking repeat type button
    public void setRepeatType(View v) {
        final RepeatType[] types = RepeatType.values();
        final String[] items = new String[RepeatType.values().length];
        for (int i = 0; i < types.length; i++) {
            items[i] = types[i].getName(this);
        }

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_type);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                reminder.setRepeatType(types[item]);
                repeatTypeText.setText(reminder.getRepeatType().getName(ReminderActivity.this));
                repeatSwitch.setChecked(true);
                reminder.setShouldRepeat(true);
                if (reminder.getRepeatInterval() > 1) {
                    repeatText.setText(String.format(getString(R.string.repeat_with_interval), String.valueOf(reminder.getRepeatInterval()), reminder.getRepeatType().getName(ReminderActivity.this)));
                } else {
                    repeatText.setText(String.format(getString(R.string.repeat_without_interval), reminder.getRepeatType().getName(ReminderActivity.this)));
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void notificationFabClicked(View v) {
        reminder.toggleNotficationActive();
        if (reminder.isNotificationActive()) {
            notificationFab.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        } else {
            notificationFab.setImageResource(R.drawable.ic_notifications_off_grey_24dp);
        }

    }
}
