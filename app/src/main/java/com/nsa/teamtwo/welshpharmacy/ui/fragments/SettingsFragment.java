package com.nsa.teamtwo.welshpharmacy.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.handlers.LocaleHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.LocationHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import static android.content.Context.MODE_PRIVATE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_CUSTOM_LOCATION;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PAGE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;
import static com.nsa.teamtwo.welshpharmacy.util.Util.getThemedDrawable;

public class SettingsFragment extends Fragment {

    private Switch locationPermissionSwitch;
    private Switch autoChangeSwitch;
    private EditText editPostcode;
    private SharedPreferences sharedPreferences;
    private int distanceScaler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View loadView = inflater.inflate(R.layout.fragment_settings, container, false);

        ImageView fontImage = loadView.findViewById(R.id.image_font);
        fontImage.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_font_24dp));
        ImageButton imageButtonFontSize = loadView.findViewById(R.id.button_fontsize);
        imageButtonFontSize.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_settings_24dp));
        imageButtonFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_DISPLAY_SETTINGS), 0);
            }
        });

        ImageView languageImage = loadView.findViewById(R.id.image_language);
        languageImage.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_language_24dp));
        Switch languageSwitch = loadView.findViewById(R.id.switch_language);
        String locale = LocaleHandler.getLanguage(getActivity());
        if (locale.equals("cy")) {
            languageSwitch.setChecked(true);
        }
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locale = LocaleHandler.getLanguage(getActivity());

                if (locale.equals("en")) {
                    setNewLocale("cy");
                } else {
                    setNewLocale("en");
                }
            }
        });

        distanceScaler = KeyValueHelper.DEFAULT_RADIUS / 10;

        sharedPreferences = getActivity().getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        ImageView locationImage = loadView.findViewById(R.id.image_location);
        locationImage.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_gps_24dp));
        locationPermissionSwitch = loadView.findViewById(R.id.switch_location_permission);
        locationPermissionSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationSwitchClicked(v);
            }
        });
        if (PermissionHandler.isLocationAccessGranted(getActivity())) {
            loadView.findViewById(R.id.layout_postcode).setVisibility(View.GONE);
            locationPermissionSwitch.setChecked(true);
            locationPermissionSwitch.callOnClick();
        }

        editPostcode = loadView.findViewById(R.id.edit_text_postcode);
        editPostcode.setText(sharedPreferences.getString(KEY_CUSTOM_LOCATION, ""));

        Button buttonGetLocation = loadView.findViewById(R.id.button_use_postcode);
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostcodeButtonPressed();
            }
        });

        final TextView selectedRadiusText = loadView.findViewById(R.id.text_selected_radius);
        SeekBar radiusSeekBar = loadView.findViewById(R.id.seek_radius);
        int progress = sharedPreferences.getInt(KeyValueHelper.KEY_RADIUS, KeyValueHelper.DEFAULT_RADIUS) / distanceScaler;
        radiusSeekBar.setProgress(progress);
        if (progress == 0) {
            selectedRadiusText.setText(String.format(getString(R.string.miles), String.valueOf(5)));
        } else {
            selectedRadiusText.setText(String.format(getString(R.string.miles), String.valueOf(progress * distanceScaler)));
        }
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
                editor.putString(KEY_PAGE, "settings");
                editor.commit();
                getActivity().recreate();
            }
        });

        TextView maxMiles = loadView.findViewById(R.id.text_max_miles);
        maxMiles.setText(String.valueOf(distanceScaler * 10));

        ImageView themeImage = loadView.findViewById(R.id.image_theme);
        themeImage.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_theme_24dp));

        Button whiteThemeButton = loadView.findViewById(R.id.button_white_theme);
        whiteThemeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(KeyValueHelper.KEY_THEME, KeyValueHelper.THEME_LIGHT);
                editor.commit();
                autoChangeSwitch.setChecked(false);
                autoChangeSwitch.callOnClick();
            }
        });

        Button blackThemeButton = loadView.findViewById(R.id.button_black_theme);
        blackThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(KeyValueHelper.KEY_THEME, KeyValueHelper.THEME_DARK);
                editor.commit();
                autoChangeSwitch.setChecked(false);
                autoChangeSwitch.callOnClick();
            }
        });

        autoChangeSwitch = loadView.findViewById(R.id.switch_auto_theme);
        autoChangeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAutoThemeSwitchClicked(v);
            }
        });

        boolean autoTheme = sharedPreferences.getBoolean(KeyValueHelper.KEY_THEME_AUTO, KeyValueHelper.DEFAULT_THEME_AUTO);
        if (autoTheme) {
            autoChangeSwitch.setChecked(true);
            autoChangeSwitch.setText(getString(R.string.dialog_positive));
        }

        ImageView resetImage = loadView.findViewById(R.id.image_reset);
        resetImage.setImageDrawable(getThemedDrawable(getActivity(),R.drawable.ic_reset_24dp));

        Button resetButton = loadView.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Need to change this so it goes to the app info page
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_SETTINGS), 0);
            }
        });

        return loadView;
    }

    private void onPostcodeButtonPressed() {
        String postcode = editPostcode.getText().toString().trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (postcode.isEmpty()) {
            editor.putString(KEY_CUSTOM_LOCATION, postcode);
            editor.putString(KEY_PAGE, "settings");
            sharedPreferences.edit().putString(KEY_PAGE, "settings").commit();
            editor.commit();
            Util.longToast(getActivity(), R.string.postcode_cleared);
            getActivity().recreate();
        } else {
            LatLng location = LocationHandler.getLocationFromPostcode(postcode, getActivity());
            if (location != null) {
                editor.putString(KeyValueHelper.KEY_CUSTOM_LOCATION, postcode);
                sharedPreferences.edit().putString(KEY_PAGE, "settings").commit();
                editor.commit();
                Util.longToast(getActivity(), R.string.location_found);
                getActivity().recreate();
            } else {
                editPostcode.setError(getResources().getString(R.string.invalid_postcode));
                Util.longToast(getActivity(), R.string.invalid_postcode);
            }
        }
    }

    private void onLocationSwitchClicked(View view) {
        boolean state = (((Switch) view).isChecked());
        if (state) {
            if (!PermissionHandler.isLocationAccessGranted(getActivity())) {
                PermissionHandler permissionHandler = new PermissionHandler();
                permissionHandler.requestLocationPermission(getActivity(), new UtilCallback() {
                    @Override
                    public void onSuccess(Object... obj) {
                        getActivity().findViewById(R.id.layout_postcode).setVisibility(View.GONE);
                        locationPermissionSwitch.callOnClick();
                        sharedPreferences.edit().putString(KEY_PAGE, "settings").commit();
                        getActivity().recreate();
                    }

                    @Override
                    public void onFail(Object... obj) {
                        locationPermissionSwitch.setChecked(false);
                        locationPermissionSwitch.callOnClick();
                    }
                });
            } else {
                locationPermissionSwitch.setText(getString(R.string.allow));
                locationPermissionSwitch.setEnabled(false);
            }
        } else {
            locationPermissionSwitch.setText(getString(R.string.deny));
            locationPermissionSwitch.setEnabled(true);
        }
    }

    private void onAutoThemeSwitchClicked(View view) {
        boolean state = (((Switch) view).isChecked());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KeyValueHelper.KEY_THEME_AUTO, state);
        editor.putString(KEY_PAGE, "settings");
        editor.commit();

        if (state) {
            ((Switch) view).setText(getString(R.string.dialog_positive));
        } else {
            ((Switch) view).setText(getString(R.string.dialog_negative));
        }
        getActivity().recreate();
    }

    private void setNewLocale(String language) {
        LocaleHandler.setNewLocale(getActivity(), language);
        sharedPreferences.edit().putString(KEY_PAGE, "settings").commit();
        getActivity().recreate();
    }
}
