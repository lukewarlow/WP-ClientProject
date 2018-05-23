package com.nsa.teamtwo.welshpharmacy.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.map.PharmacyMarker;
import com.nsa.teamtwo.welshpharmacy.data.map.PharmacyMarkerRenderer;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.OpeningTime;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.ui.activities.DisplayPharmacyActivity;
import com.nsa.teamtwo.welshpharmacy.ui.activities.NavigationActivity;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;
import com.nsa.teamtwo.welshpharmacy.util.UtilCallback;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MapHandler implements OnMapReadyCallback {
    private static final String TAG = "Map";
    private static final float DEFAULT_ZOOM = 13.5f;
    private static final float LESS_ZOOM = 7.5f;

    private GoogleMap map;
    private List<PharmacyMarker> markers;

    private MapFragment mapFragment;
    private ClusterManager<PharmacyMarker> clusterManager;
    private Context context;
    private Activity activity;
    private LatLng location;

    private MapStyleOptions style;

    public MapHandler(Activity activity) {
        this.context = activity.getBaseContext();
        this.activity = activity;

        if (Util.isDarkTheme(context)) {
            style = MapStyleOptions.loadRawResourceStyle(context, R.raw.night_json);
        } else {
            style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json);
        }
        mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
    }

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        boolean success = map.setMapStyle(style);
        if (!success) {
            Log.e(TAG, "Styling Error");
        }

        //Adapted from https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
        //Accessed: 16/04/2018
        clusterManager = new ClusterManager<>(context, map);
        clusterManager.setRenderer(new PharmacyMarkerRenderer(context, map, clusterManager));
        clusterManager.setAnimation(false);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MapHandler.PharmacyInfoWindowAdapter());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "onClusterItemInfoWindowClick");
                final Pharmacy pharmacy = (Pharmacy) marker.getTag();
                Intent intent = new Intent(context, DisplayPharmacyActivity.class);
                intent.putExtra("Pharmacy", pharmacy);
                context.startActivity(intent);
            }
        });

        markers = new ArrayList<>();
        addPharmacyMapMarkers();
        plotGP();
        enableMyLocation();
    }

    private void addPharmacyMapMarkers() {
        int scaler = 100;

        BitmapDrawable bitmapDrawableGreen = (BitmapDrawable) context.getResources().getDrawable(R.drawable.pharmacy_marker_green);
        Bitmap greenMarker = Bitmap.createScaledBitmap(bitmapDrawableGreen.getBitmap(), scaler, scaler, false);
        BitmapDrawable bitmapDrawableGreenGreyed = (BitmapDrawable) context.getResources().getDrawable(R.drawable.pharmacy_marker_green_greyed);
        Bitmap greenMarkerGreyed = Bitmap.createScaledBitmap(bitmapDrawableGreenGreyed.getBitmap(), scaler, scaler, false);

        BitmapDrawable bitmapDrawableRed = (BitmapDrawable) context.getResources().getDrawable(R.drawable.pharmacy_marker_red);
        Bitmap redMarker = Bitmap.createScaledBitmap(bitmapDrawableRed.getBitmap(), scaler, scaler, false);
        BitmapDrawable bitmapDrawableRedGreyed = (BitmapDrawable) context.getResources().getDrawable(R.drawable.pharmacy_marker_red_greyed);
        Bitmap redMarkerGreyed = Bitmap.createScaledBitmap(bitmapDrawableRedGreyed.getBitmap(), scaler, scaler, false);


        for (Pharmacy pharmacy : Util.pharmacies) {
            PharmacyMarker item;
            if (pharmacy.isOpenNow()) {
                if (pharmacy.isWelshAvailable()) {
                    item = new PharmacyMarker(pharmacy.getLocation(), pharmacy.getName(), BitmapDescriptorFactory.fromBitmap(greenMarker), pharmacy);
                } else {
                    item = new PharmacyMarker(pharmacy.getLocation(), pharmacy.getName(), BitmapDescriptorFactory.fromBitmap(redMarker), pharmacy);
                }
            } else {
                if (pharmacy.isWelshAvailable()) {
                    item = new PharmacyMarker(pharmacy.getLocation(), pharmacy.getName(), BitmapDescriptorFactory.fromBitmap(greenMarkerGreyed), pharmacy);
                } else {
                    item = new PharmacyMarker(pharmacy.getLocation(), pharmacy.getName(), BitmapDescriptorFactory.fromBitmap(redMarkerGreyed), pharmacy);
                }
            }
            clusterManager.addItem(item);
            markers.add(item);
        }

        clusterManager.cluster();
    }

    private void addLocationMarker(LatLng latLng) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.man);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 140, 140, false);
        map.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
    }

    private void plotGP() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(KeyValueHelper.KEY_PREFERENCES, Context.MODE_PRIVATE);
        String address = sharedPreferences.getString(KeyValueHelper.KEY_GP_ADDRESS, "");
        if (!address.isEmpty()) {
            LatLng location = LocationHandler.getLocationFromPostcode(address, activity);
            Log.d(TAG, address);
            if (location != null) {
                BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.stethoscope);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
                map.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableMyLocation() {
        Log.d(TAG, "Method: enableMyLocation");
        LocationHandler locationHandler = new LocationHandler(activity);

        locationHandler.getDeviceLocation(activity);

        locationHandler.setCallback(new UtilCallback() {
            @Override
            public void onSuccess(Object... obj) {
                Location location = (Location) obj[0];
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM);
                filterByRadius(latLng);
            }

            @Override
            public void onFail(Object... obj) {
                LatLng latLng = new LatLng(52.374712, -3.727406);
                moveCamera(latLng, 7.5f);
                filterByRadius(latLng);
            }
        });


        if (PermissionHandler.isLocationAccessGranted(context)) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            } else {
                Log.e(TAG, "Map is null");
            }
        } else {
            LatLng location = ((NavigationActivity) activity).getLocation();
            if (location != null) {
                filterByRadius(location);
                moveCamera(location, DEFAULT_ZOOM);
            } else {
                location = new LatLng(52.374712, -3.727406);
                filterByRadius(location);
                moveCamera(location, LESS_ZOOM);
            }
        }
    }

    public LatLng getLocation() {
        return location;
    }

    private void filterByRadius(LatLng location) {
        clusterManager.clearItems();

        for (PharmacyMarker marker : markers) {
            Pharmacy pharmacy = marker.getPharmacy();
            boolean needsRemoving = !Util.isWithinRadius(context, pharmacy.getLocation(), location);
            if (needsRemoving) {
                clusterManager.removeItem(marker);
            } else {
                clusterManager.addItem(marker);
            }
        }
        clusterManager.cluster();
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "Method: moveCamera, Moving the camera to Lat " + latLng.latitude +
                " and Lng " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        addLocationMarker(latLng);
        location = latLng;
    }

    public List<PharmacyMarker> getMarkers() {
        return markers;
    }

    public ClusterManager getClusterManager() {
        return clusterManager;
    }

    class PharmacyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View view;

        PharmacyInfoWindowAdapter() {
            view = activity.getLayoutInflater().inflate(R.layout.pharmacy_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Pharmacy pharmacy = (Pharmacy) marker.getTag();
            boolean isOpen = pharmacy.isOpenNow();
            String openStatus = "";
            String nextOpen = "";
            if (isOpen) {
                openStatus = context.getString(R.string.open_status_open);
            } else {
                Pair<String, OpeningTime> openingTime = pharmacy.getNextOpeningTime(context);
                openStatus = context.getString(R.string.open_status_closed);
                nextOpen = String.format(context.getString(R.string.next_open), openingTime.first, Util.getCurrentTimeFromDouble(openingTime.second.getOpeningTime()));
                TextView nextOpenView = view.findViewById(R.id.next_open);
                nextOpenView.setVisibility(View.VISIBLE);
                nextOpenView.setText(nextOpen);
            }

            String phoneNumber = String.format(context.getString(R.string.number) + " %s", pharmacy.getPhoneNumber());

            TextView titleView = view.findViewById(R.id.title);
            titleView.setText(pharmacy.getName());
            TextView phoneNumberView = view.findViewById(R.id.phone_number);
            phoneNumberView.setText(phoneNumber);
            TextView openStatusView = view.findViewById(R.id.open_status);
            openStatusView.setText(openStatus);
            return view;
        }
    }
}
