package com.nsa.teamtwo.welshpharmacy.handlers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

public class PermissionHandler {
    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    public static UtilCallback callback;

    public void requestLocationPermission(final Activity activity, final UtilCallback callback) {
        PermissionHandler.callback = callback;
//         Request the permission
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    public static boolean isLocationAccessGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
