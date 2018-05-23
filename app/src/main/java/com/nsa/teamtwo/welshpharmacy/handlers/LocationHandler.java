package com.nsa.teamtwo.welshpharmacy.handlers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import java.io.IOException;
import java.util.List;

public class LocationHandler {
    private static final String TAG = "Location handler";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private FusedLocationProviderClient locationProviderClient;
    private Location currentLocation;

    private Context context;
    private Activity activity;
    private UtilCallback callback;

    public LocationHandler(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    public void setCallback(UtilCallback callback) {
        this.callback = callback;
    }

    private boolean serviceCheck() {
        Log.d(TAG, "Method: serviceCheck, Checking google services version");

        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (availability == ConnectionResult.SUCCESS) {
            //User has agreed or the application has access to maps
            Log.d(TAG, "Method: serviceCheck, Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(availability)) {
            // an error has been thrown
            Log.d(TAG, "Method: serviceCheck, Error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, availability, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Util.shortToast(context, R.string.service_check_no_connection);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void getDeviceLocation(final Activity activity) {
        Log.d(TAG, "Method: getDeviceLocation, getting the devices current location");
        if (serviceCheck()) {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            try {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    final Task location = locationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                currentLocation = (Location) task.getResult();
                                if (currentLocation != null) {
                                    Log.d(TAG, "Method: onComplete in getDeviceLocation, found location.");
                                    if (callback != null)
                                        callback.onSuccess(currentLocation);
                                    LocationHandler.showDialogIfOutOfWales(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), activity);
                                } else {
                                    if (callback != null)
                                        callback.onFail();
                                    Log.d(TAG, "Method: onComplete in getDeviceLocation, location not found.");
                                }
                            } else {
                                Log.d(TAG, "Method: onComplete in getDeviceLocation, current location has not been found");
                                Util.shortToast(context, R.string.location_not_found);
                                if (callback != null)
                                    callback.onFail();
                            }
                        }
                    });
                }
            } catch (SecurityException e) {
                Log.d(TAG, "getDeviceLocation: Security exception: " + e.getMessage());

            }
        }
    }

    public static LatLng getLocationFromPostcode(String postcode, Activity activity) {
        LatLng location = null;
        Geocoder geocoder = new Geocoder(activity);
        Address address;
        try {
            List<Address> addressList = geocoder.getFromLocationName(postcode, 1);
            if (addressList.size() > 0) {
                address = addressList.get(0);
                location = new LatLng(address.getLatitude(), address.getLongitude());
                LocationHandler.showDialogIfOutOfWales(location, activity);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return location;
    }

    private static void showDialogIfOutOfWales(LatLng location, Activity activity) {
        if (location != null) {
            if (location.latitude < Util.LAT_MIN || location.latitude > Util.LAT_MAX
                    || location.longitude < Util.LONG_MIN || location.longitude > Util.LONG_MAX) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle(R.string.dialog_title_out_of_wales);
                dialog.setMessage(R.string.dialog_message_out_of_wales);
                dialog.setPositiveButton(R.string.dialog_ok, null);
                dialog.show();
            }
        }
    }
}
