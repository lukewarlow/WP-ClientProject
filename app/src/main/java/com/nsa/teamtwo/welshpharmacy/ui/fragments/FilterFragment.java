package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.FilterCallback;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends DialogFragment {
    private ArrayList<Service> list = new ArrayList<>();
    private boolean welshAvailableOnly;
    private boolean openOnly;
    private FilterCallback callback;
    private View view;

    public void setCallback(FilterCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int shifter = 4;
        String[] names = new String[Util.services.size() - shifter];

        for (int i = 0; i < Util.services.size() - shifter; i++) {
            names[i] = Util.services.get(i + shifter).getName(getActivity());
        }

        boolean[] checkedItems = null;
        if (!list.isEmpty()) {
            checkedItems = new boolean[Util.services.size() - shifter];
            for (int i = 0; i < checkedItems.length; i++) {
                if (list.contains(Util.services.get(i + shifter))) {
                    checkedItems[i] = true;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_filters, null);
        final LinearLayout serviceLayout = view.findViewById(R.id.layout_services);
        CheckBox welshAvailable = view.findViewById(R.id.checkbox_welsh_available);
        welshAvailable.setChecked(welshAvailableOnly);
        CheckBox pharmacyOpen = view.findViewById(R.id.checkbox_pharmacy_open);
        pharmacyOpen.setChecked(openOnly);

        for (int i = 0; i < Util.services.size() - shifter; i++) {
            CheckBox checkBox = new CheckBox(getActivity());
            if (checkedItems != null) {
                checkBox.setChecked(checkedItems[i]);
            }
            checkBox.setTextSize(22);
            checkBox.setText(Util.services.get(i + shifter).getName(getActivity()));
            serviceLayout.addView(checkBox);
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                welshAvailableOnly = ((CheckBox) view.findViewById(R.id.checkbox_welsh_available)).isChecked();
                openOnly = ((CheckBox) view.findViewById(R.id.checkbox_pharmacy_open)).isChecked();
                list.clear();
                for (int i = 0; i < Util.services.size() - shifter; i++) {
                    CheckBox checkBox = (CheckBox) serviceLayout.getChildAt(i);
                    if (checkBox.isChecked()) list.add(Util.services.get(i + shifter));
                }
                callback.onFilterSelected(welshAvailableOnly, openOnly, list);
            }
        });

        builder.setNeutralButton(R.string.dialog_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                boolean welshAvailableOnly = ((CheckBox) view.findViewById(R.id.checkbox_welsh_available)).isChecked();
//                boolean openOnly = ((CheckBox) view.findViewById(R.id.checkbox_pharmacy_open)).isChecked();
//                callback.onFilterSelected(welshAvailableOnly, openOnly, null);
                list.clear();
                welshAvailableOnly = false;
                openOnly = false;
                callback.onFilterSelected(false, false, null);
            }
        });

        return builder.create();
    }
}