package com.nsa.teamtwo.welshpharmacy.data.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;

import java.util.ArrayList;

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    private Context context;

    public ReminderAdapter(Context context, ArrayList<Reminder> items)
    {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Reminder reminder = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_item, parent, false);

        TextView reminderText = convertView.findViewById(R.id.text_reminder);
        reminderText.setText(reminder.getReminderText());
        return convertView;
    }
}