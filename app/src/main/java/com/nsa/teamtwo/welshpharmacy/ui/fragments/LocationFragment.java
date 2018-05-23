package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.handlers.LocationHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler;
import com.nsa.teamtwo.welshpharmacy.ui.activities.SetupActivity;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import static android.content.Context.MODE_PRIVATE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_CUSTOM_LOCATION;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;


public class LocationFragment extends Fragment {

    private LatLng location;
    private int distanceScaler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View loadView = inflater.inflate(R.layout.fragment_location, container, false);

        final TextView locationRequestText = loadView.findViewById(R.id.text_location_request);
        final RelativeLayout postcodeGroup = loadView.findViewById(R.id.layout_postcode);
        final EditText editPostcode = loadView.findViewById(R.id.edit_text_postcode);
        final Button postcodeButton = loadView.findViewById(R.id.button_use_postcode);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.location_dialog_title);
        dialog.setMessage(R.string.location_permission_request);

        dialog.setNeutralButton(R.string.dialog_ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionHandler permissionHandler = new PermissionHandler();
                permissionHandler.requestLocationPermission((AppCompatActivity) getActivity(), new UtilCallback() {
                    @Override
                    public void onSuccess(Object... obj) {
                        locationRequestText.setText(R.string.location_permission_granted);
                    }

                    @Override
                    public void onFail(Object... obj) {
                        locationRequestText.setText(R.string.location_permission_denied);
                        postcodeGroup.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        dialog.show();

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        distanceScaler = KeyValueHelper.DEFAULT_RADIUS / 10;
        postcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postcode = editPostcode.getText().toString().trim();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (postcode.isEmpty()) {
                    editor.putString(KEY_CUSTOM_LOCATION, postcode);
                    editor.commit();
                    Util.longToast(getActivity(), R.string.postcode_cleared);
                } else {
                    LatLng location = LocationHandler.getLocationFromPostcode(postcode, getActivity());
                    if (location != null) {
                        editor.putString(KeyValueHelper.KEY_CUSTOM_LOCATION, postcode);
                        editor.commit();
                        Util.longToast(getActivity(), R.string.location_found);
                    } else {
                        editPostcode.setError(getResources().getString(R.string.invalid_postcode));
                        Util.longToast(getActivity(), R.string.invalid_postcode);
                    }
                }
            }
        });

        final TextView selectedRadiusText = loadView.findViewById(R.id.text_selected_radius);
        SeekBar radiusSeekBar = loadView.findViewById(R.id.seek_radius);
        radiusSeekBar.setProgress(KeyValueHelper.DEFAULT_RADIUS / distanceScaler);
        selectedRadiusText.setText(String.format(getString(R.string.miles), String.valueOf(radiusSeekBar.getProgress() * distanceScaler)));
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                int distance;
                if (progress == 0) {
                    distance = 5;
                } else {
                    distance = progress * distanceScaler;
                }
                selectedRadiusText.setText(String.format(getString(R.string.miles), String.valueOf(distance)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int distance = progress * distanceScaler;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(KeyValueHelper.KEY_RADIUS, distance);
                editor.commit();
            }
        });

        TextView maxMiles = loadView.findViewById(R.id.text_max_miles);
        maxMiles.setText(String.valueOf(distanceScaler * 10));
        return loadView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FloatingActionButton nextButton = getActivity().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupActivity setupActivity = (SetupActivity) getActivity();
                setupActivity.setLocation(location);
                setupActivity.incrementPageNumber();
            }
        });
    }
}
