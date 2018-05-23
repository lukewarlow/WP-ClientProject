package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.LocationFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.MarkerKeyFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.WelcomeFragment;
import com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;

import static com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler.PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;

public class SetupActivity extends BaseActivity {
    private int pageNumber;
    private LatLng location;

    public void incrementPageNumber() {
        pageNumber++;
        switch (pageNumber) {
            case 1: {
                getFragmentManager().beginTransaction().replace(R.id.content_setup, new WelcomeFragment()).commit();
                SharedPreferences sharedPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KeyValueHelper.KEY_SHOW_SETUP, false);
                editor.apply();
                break;
            }
            case 2: {
                getFragmentManager().beginTransaction().replace(R.id.content_setup, new LocationFragment()).commit();
                break;
            }
            case 3: {
                getFragmentManager().beginTransaction().replace(R.id.content_setup, new MarkerKeyFragment()).commit();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*
            Adapted from: https://developer.android.com/training/permissions/requesting.html
            and the additional provided samples.
         */
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionHandler.callback.onSuccess();
            } else {
                PermissionHandler.callback.onFail();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = 0;
        setContentView(R.layout.activity_setup);
        incrementPageNumber();
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }
}
