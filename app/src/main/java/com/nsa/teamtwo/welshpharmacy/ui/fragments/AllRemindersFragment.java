package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Date;
import com.nsa.teamtwo.welshpharmacy.data.reminder.NotificationReceiver;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Time;
import com.nsa.teamtwo.welshpharmacy.ui.SwipeDetector;
import com.nsa.teamtwo.welshpharmacy.ui.activities.ReminderActivity;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Reminder;
import com.nsa.teamtwo.welshpharmacy.data.reminder.ReminderAdapter;
import com.nsa.teamtwo.welshpharmacy.data.reminder.ReminderDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AllRemindersFragment extends Fragment {
    private static final String TAG = "All Reminders fragment";

    private ListView listView;
    private ReminderDB reminderDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_all_reminders, container, false);
        reminderDB = new ReminderDB(getActivity());
        listView = loadView.findViewById(R.id.list_reminders);
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        Reminder reminder = (Reminder) listView.getItemAtPosition(position);
                        showDeleteConfirmationDialog(reminder);
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ReminderActivity.class);
                Reminder reminder = (Reminder) listView.getItemAtPosition(position);
                intent.putExtra("reminderText", reminder.getReminderText());
                intent.putExtra("date", reminder.getDate().toString());
                intent.putExtra("time", reminder.getTime().toString());
                startActivity(intent);
                return false;
            }
        });
        populateListView(loadView);

        FloatingActionButton floatingActionButton = loadView.findViewById(R.id.fab_add_reminder);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReminderActivity.class);
                startActivity(intent);
            }
        });
        return loadView;
    }

    private void showDeleteConfirmationDialog(final Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_reminder_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteReminder(reminder);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteReminder(Reminder reminder) {
        if (reminder.isNotificationActive()) {
            String tag = reminder.getReminderText() + reminder.getDate().toString() + reminder.getTime().toString();
            Log.d(TAG, tag);
            //From https://stackoverflow.com/questions/44216509/how-to-display-notification-at-a-particular-time?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE); //request alarm service
            Intent notificationAlert = new Intent(getActivity(), NotificationReceiver.class); //intent to connection at broadcastReceiver class
            notificationAlert.putExtra("reminderText", reminder.getReminderText());
            notificationAlert.putExtra("reminderID", tag);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationAlert, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
        reminderDB.deleteReminder(reminder);
        populateListView(getView());
    }

    private void populateListView(View view) {
        Log.d(TAG, "Displaying data in the ListView.");
        ArrayList<Reminder> reminders = reminderDB.getReminders();

        TextView noRemindersText = view.findViewById(R.id.text_no_reminder);
        if (reminders.size() > 0) {
            noRemindersText.setVisibility(View.INVISIBLE);
        } else {
            noRemindersText.setVisibility(View.VISIBLE);
        }
        ListAdapter adapter = new ReminderAdapter(getActivity(), reminders);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView(getView());
    }
}
