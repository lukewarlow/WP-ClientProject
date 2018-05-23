package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.handlers.LocationHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.network.NetworkHandler;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.AllNotesFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.AllRemindersFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.AppInfoFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.GpDetailsFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.HelpFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.ListFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.MapFragment;
import com.nsa.teamtwo.welshpharmacy.ui.fragments.SettingsFragment;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import static com.nsa.teamtwo.welshpharmacy.handlers.PermissionHandler.PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PAGE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;

public class NavigationActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {

    private String TAG = "Navigation Activity";
    private LatLng location;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;

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

        sharedPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d(TAG, "Number of pharmacies: " + String.valueOf(Util.pharmacies.size()));
        location = getIntent().getParcelableExtra("location");
        if (location == null) {
            String postcode = sharedPreferences.getString(KeyValueHelper.KEY_CUSTOM_LOCATION, "");
            if (!postcode.isEmpty()) {
                location = LocationHandler.getLocationFromPostcode(postcode, this);
            }
        }

        boolean goToSettingsPage = false;
        boolean goToGpDetails = false;
        String page = sharedPreferences.getString(KEY_PAGE, "");
        if (page.equals("settings")) {
            goToSettingsPage = true;
        } else if (page.equals("gpDetails")) {
            goToGpDetails = true;
        }
        sharedPreferences.edit().putString(KEY_PAGE, "").commit();

        if (!NetworkHandler.hasInternet(this)) {
            navigationView.getMenu().getItem(0).setEnabled(false);
        }
        if (goToSettingsPage) {
            navigationView.getMenu().getItem(5).setChecked(true);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else if (goToGpDetails) {
            navigationView.getMenu().getItem(4).setChecked(true);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new GpDetailsFragment()).commit();
        } else {
            if (!NetworkHandler.hasInternet(this)) {
                navigationView.getMenu().getItem(1).setChecked(true);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ListFragment()).commit();
                showNoNetworkDialog();
            } else {
                getFragmentManager().beginTransaction().add(R.id.content_frame, new MapFragment()).commit();
            }
        }
    }

    public LatLng getLocation() {
        return location;
    }

    private void showNoNetworkDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_no_internet);
        dialog.setMessage(R.string.dialog_message_no_internet);
        dialog.setPositiveButton(R.string.dialog_ok, null);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        switch (id) {
            case R.id.nav_map:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();
                break;
            case R.id.nav_list:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ListFragment()).commit();
                break;
            case R.id.nav_notes:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new AllNotesFragment()).commit();
                break;
            case R.id.nav_reminders:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new AllRemindersFragment()).commit();
                break;
            case R.id.nav_gpdetails:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new GpDetailsFragment()).commit();
                break;
            case R.id.nav_settings:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
                break;
            case R.id.nav_app_info:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new AppInfoFragment()).commit();
                break;
            case R.id.nav_help:
                fragmentManager.beginTransaction().replace(R.id.content_frame, new HelpFragment()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
