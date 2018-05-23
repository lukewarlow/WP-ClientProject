package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.JsonParser;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;
import com.nsa.teamtwo.welshpharmacy.handlers.network.NetworkHandler;
import com.nsa.teamtwo.welshpharmacy.handlers.network.VolleyCallback;
import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import org.json.JSONObject;

import java.util.List;

import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;

public class SplashActivity extends BaseActivity {

    private String TAG = "Splash Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Util.isDarkTheme(this)) {
            ((TextView) findViewById(R.id.text_splash)).setTextColor(getResources().getColor(android.R.color.white));
        }

        //Adapted from: https://www.androidtutorialpoint.com/material-design/android-splash-screen-tutorial-create-animated-splash-screen-android-studio/
        final ImageView imageView = findViewById(R.id.splashImage);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        NetworkHandler.getServices(this, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                List<Service> retrievedServices = JsonParser.parseServiceData(result);
                if (retrievedServices != null) {
                    Util.services.addAll(retrievedServices);
                }
            }
        });

        NetworkHandler.getPharmacies(this, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                List<Pharmacy> pharmacies = JsonParser.parsePharmacyData(result, Util.services);
                if (pharmacies != null) {
                    Util.pharmacies.addAll(pharmacies);
                }
            }
        });

        imageView.startAnimation(animation);
        animation.setRepeatCount(Integer.MAX_VALUE);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences sharedPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
                boolean showSetup = sharedPreferences.getBoolean(KeyValueHelper.KEY_SHOW_SETUP, KeyValueHelper.DEFAULT_SHOW_SETUP);
                if (showSetup) {
                    Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(getBaseContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (Util.pharmacies.size() > 0) {
                    animation.cancel();
                }
            }
        });
    }
}
