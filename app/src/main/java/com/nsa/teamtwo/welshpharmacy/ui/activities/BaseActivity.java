package com.nsa.teamtwo.welshpharmacy.ui.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.handlers.LocaleHandler;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import static android.content.pm.PackageManager.GET_META_DATA;

//Adapted from: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
//Accessed 19/04/2018
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "Base Activity";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHandler.setLocale(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHandler.setLocale(this);
        resetTitles();
        resetTheme();
    }

    private void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void resetTheme() {
        Resources.Theme theme = getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(R.attr.windowActionBar, value, true);
        String stringValue = TypedValue.coerceToString(value.type, value.data);
        String packageName = getLocalClassName();
        if (Util.isDarkTheme(this)) {
            if (Boolean.valueOf(stringValue)) {
                getTheme().applyStyle(R.style.AppThemeDark, true);
            } else {
                getTheme().applyStyle(R.style.AppThemeDark_NoActionBar, true);
            }
        } else {
            if (Boolean.valueOf(stringValue)) {
                getTheme().applyStyle(R.style.AppTheme, true);
            } else {
                getTheme().applyStyle(R.style.AppTheme_NoActionBar, true);
            }
        }
    }
}
