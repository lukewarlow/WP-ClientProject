package com.nsa.teamtwo.welshpharmacy.data.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.nsa.teamtwo.welshpharmacy.R;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String channelID = "com.nsa.teamtwo.welshpharmacy";

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderText = intent.getStringExtra("reminderText");
        String reminderID = intent.getStringExtra("reminderID");
        if (reminderText != null && !reminderText.isEmpty()
                && reminderID != null && !reminderID.isEmpty()) {
            NotificationCompat.Builder builder;
            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager.getNotificationChannel(channelID) == null) {
                    NotificationChannel reminderChannel = new NotificationChannel(channelID, context.getString(R.string.reminders), NotificationManager.IMPORTANCE_HIGH);
                    reminderChannel.setDescription("Reminders");
                    notificationManager.createNotificationChannel(reminderChannel);
                }
            }

            builder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                    .setContentTitle(context.getString(R.string.reminder))
                    .setContentText(reminderText)
                    .setDefaults(Notification.DEFAULT_ALL);

            if (Build.VERSION.SDK_INT >= 26) {
                builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
            } else {
                builder.setPriority(Notification.PRIORITY_HIGH);
            }


            notificationManager.notify(reminderID, 1, builder.build());
        }
    }
}
