package com.nsa.teamtwo.welshpharmacy.data.pharmacy;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PharmacyAdapter extends ArrayAdapter<Pharmacy> {

    public PharmacyAdapter(Context context, List<Pharmacy> items)
    {
        super(context, 0, new ArrayList<>(items));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Pharmacy pharmacy = getItem(position);

        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.pharmacy_item, parent, false);

        TextView name = convertView.findViewById(R.id.text_pharmacy_name);
        name.setText(pharmacy.getName());

        ImageView imageLanguage = convertView.findViewById(R.id.image_language);
        if (pharmacy.isWelshAvailable()) {
            imageLanguage.setImageDrawable(Util.getThemedDrawable(getContext(), R.drawable.ic_language_24dp));
        } else {
            imageLanguage.setImageDrawable(null);
        }

        TextView textOpen = convertView.findViewById(R.id.text_open);
        if (pharmacy.isOpenNow()) {
            textOpen.setText(R.string.open_status_open);
            textOpen.setTextColor(Color.GREEN);
        } else {
            textOpen.setText(R.string.open_status_closed);
            textOpen.setTextColor(Color.RED);
        }
        return convertView;
    }

}
