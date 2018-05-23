package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.DayOfTheWeek;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.OpeningTime;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.nsa.teamtwo.welshpharmacy.util.Util.getThemedDrawable;

public class DisplayPharmacyActivity extends BaseActivity {

    private static final String TAG = "Pharmacy Page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final Pharmacy pharmacy = getIntent().getParcelableExtra("Pharmacy");
        if (pharmacy == null) {
            throw new IllegalArgumentException("Invalid pharmacy passed.");
        }
        Log.d(TAG, pharmacy.getName());

        ImageView nameImage = findViewById(R.id.image_name);
        nameImage.setImageDrawable(getThemedDrawable(this, R.drawable.ic_font_24dp));
        TextView pharmacyName = findViewById(R.id.text_pharmacy_name);
        pharmacyName.setText(pharmacy.getName());


        ImageView phoneImage = findViewById(R.id.image_phone);
        phoneImage.setImageDrawable(getThemedDrawable(this, R.drawable.ic_phone_24dp));
        TextView pharmacyTelephone = findViewById(R.id.text_phone_number);
        //Adapted from https://stackoverflow.com/questions/8033316/to-draw-an-underline-below-the-textview-in-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        //Accessed: 15/04/2018
        SpannableString content = new SpannableString(getString(R.string.number) + ": " + pharmacy.getPhoneNumber());
        int offset = getString(R.string.number).length() + 2;
        content.setSpan(new UnderlineSpan(), offset, pharmacy.getPhoneNumber().length() + offset, 0);
        //Adapted from https://stackoverflow.com/questions/4032676/how-can-i-change-the-color-of-a-part-of-a-textview?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        //Accessed: 15/04/2018
        content.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryAccent)), offset, pharmacy.getPhoneNumber().length() + offset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pharmacyTelephone.setText(content);

        pharmacyTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayPharmacyActivity.this);
                dialog.setTitle(R.string.call_dialog_title);
                String message = String.format(getString(R.string.call_dialog_message), pharmacy.getName());
                dialog.setMessage(message);

                dialog.setNegativeButton(R.string.dialog_negative, null);
                dialog.setPositiveButton(R.string.dialog_positive, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Adapted from: https://developer.android.com/training/basics/intents/sending.html
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + pharmacy.getPhoneNumber()));
                        startActivity(callIntent);
                    }
                });

                dialog.show();
            }
        });

        ImageView languageImage = findViewById(R.id.image_language);
        languageImage.setImageDrawable(getThemedDrawable(this, R.drawable.ic_language_24dp));
        TextView welshAvailableText = findViewById(R.id.text_welsh_available);
        if (pharmacy.isWelshAvailable()) {
            welshAvailableText.setText(R.string.welsh_available);
        } else {
            welshAvailableText.setText(R.string.welsh_unavailable);
        }

//        Opening times

        ImageView openingTimesImage = findViewById(R.id.image_opening_times);
        openingTimesImage.setImageDrawable(getThemedDrawable(this, R.drawable.ic_clock_24dp));

        List<String> openingTimes = new ArrayList<>();
        for (int i = 0; i < DayOfTheWeek.values().length; i++) {
            String data = ": " + getString(R.string.closed);
            if (pharmacy.willOpen(DayOfTheWeek.values()[i])) {
                OpeningTime openingTime = pharmacy.getOpeningTime(DayOfTheWeek.values()[i]);
                data = ": " + Util.getCurrentTimeFromDouble(openingTime.getOpeningTime()) + " - " + Util.getCurrentTimeFromDouble(openingTime.getClosingTime());
            }
            openingTimes.add(DayOfTheWeek.values()[i].getName(this) + data);
        }

        final ListView openingTimesList = findViewById(R.id.list_openingTimes);
        openingTimesList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, openingTimes));
        setListViewHeightBasedOnChildren(openingTimesList);


        List<String> services = new ArrayList<>();
        for (Service service : Util.services) {
            if (pharmacy.doesService(service)) {
                services.add(service.getName(this));
            }
        }

        ImageView serviceImage = findViewById(R.id.image_services);
        serviceImage.setImageDrawable(getThemedDrawable(this, R.drawable.ic_pharmacy_24dp));
        final ListView serviceList = findViewById(R.id.list_services);
        serviceList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, services));
        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service service = Util.services.get(position);
                if (!service.getDescription(DisplayPharmacyActivity.this).equals("Description")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayPharmacyActivity.this);
                    dialog.setTitle(service.getName(DisplayPharmacyActivity.this));
                    dialog.setMessage(service.getDescription(DisplayPharmacyActivity.this));
                    dialog.setPositiveButton(R.string.dialog_ok, null);
                    dialog.show();
                }
            }
        });
        setListViewHeightBasedOnChildren(serviceList);
    }

    //From https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view/20475821
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
