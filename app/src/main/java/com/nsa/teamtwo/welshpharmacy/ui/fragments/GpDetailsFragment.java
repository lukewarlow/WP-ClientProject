package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.handlers.DialogHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.LocationHandler;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_DR_NAME;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_GP_ADDRESS;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_GP_NAME;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_GP_TELPHONE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PAGE;

public class GpDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences sharedPref = getActivity().getSharedPreferences(KeyValueHelper.KEY_PREFERENCES,Context.MODE_PRIVATE);
        View loadView = inflater.inflate(R.layout.fragment_gp_details, container, false);

        ImageView nameImage = loadView.findViewById(R.id.image_name);
        nameImage.setImageDrawable(Util.getThemedDrawable(getActivity(), R.drawable.ic_font_24dp));
        final EditText gpName = loadView.findViewById(R.id.gp_name);

        ImageView drNameImage = loadView.findViewById(R.id.image_dr_name);
        drNameImage.setImageDrawable(Util.getThemedDrawable(getActivity(), R.drawable.ic_person_outline_24dp));
        final EditText doctorName = loadView.findViewById(R.id.doctor_name);

        ImageView addressImage = loadView.findViewById(R.id.image_address);
        addressImage.setImageDrawable(Util.getThemedDrawable(getActivity(), R.drawable.ic_gps_24dp));
        final EditText gpAddress = loadView.findViewById(R.id.text_address);

        ImageView phoneImage = loadView.findViewById(R.id.image_phone);
        phoneImage.setImageDrawable(Util.getThemedDrawable(getActivity(), R.drawable.ic_phone_24dp));
        final EditText gpTelephone = loadView.findViewById(R.id.gp_telephone);

        String gpNameText = sharedPref.getString(KEY_GP_NAME, "");
        if (!gpNameText.isEmpty()) {
            gpName.setText(gpNameText);
            String gpAddressText = sharedPref.getString(KEY_GP_ADDRESS, "");
            gpAddress.setText(gpAddressText);
            String gpTelephoneText = sharedPref.getString(KEY_GP_TELPHONE, "");
            gpTelephone.setText(gpTelephoneText);
            String doctorNameText = sharedPref.getString(KEY_DR_NAME, "");
            doctorName.setText(doctorNameText);
        }

        Button saveGPDetails = loadView.findViewById(R.id.save_button);

        //   Save button
        saveGPDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                LatLng location = LocationHandler.getLocationFromPostcode(gpAddress.getText().toString(), getActivity());
                if (location != null) {
                    editor.putString(KEY_GP_NAME, gpName.getText().toString());
                    editor.putString(KEY_GP_ADDRESS, gpAddress.getText().toString());
                    editor.putString(KEY_GP_TELPHONE, gpTelephone.getText().toString());
                    editor.putString(KEY_DR_NAME, doctorName.getText().toString());
                    editor.putString(KEY_PAGE, "gpDetails");
                    editor.commit();

                    Util.shortToast(getActivity(), R.string.gp_details_saved);
                    getActivity().recreate();
                } else {
                    gpAddress.setError(getResources().getString(R.string.invalid_postcode));
                    Util.longToast(getActivity(), R.string.invalid_postcode);
                }
            }
        });

        Button clearGPDetails = loadView.findViewById(R.id.clear_button);
        clearGPDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogHandler.showConfirmationDialog(getActivity(), R.string.delete_gp_details_dialog_msg, R.string.delete, R.string.cancel, new UtilCallback() {
                    @Override
                    public void onSuccess(Object... o) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(KEY_GP_NAME, "");
                        editor.putString(KEY_GP_ADDRESS, "");
                        editor.putString(KEY_GP_TELPHONE, "");
                        editor.putString(KEY_DR_NAME, "");
                        editor.putString(KEY_PAGE, "gpDetails");
                        editor.commit();

                        Util.shortToast(getActivity(),getString(R.string.gp_details_cleared));
                        getActivity().recreate();
                    }

                    @Override
                    public void onFail(Object... o) {
                    }
                });
            }
        });

        return loadView;
    }
}
