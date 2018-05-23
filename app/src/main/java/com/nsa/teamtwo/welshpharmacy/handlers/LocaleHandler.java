package com.nsa.teamtwo.welshpharmacy.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;

//Adapted from: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
//Accessed on 19/04/2018
public class LocaleHandler {

    public static Context setLocale(Context context) {
        return setNewLocale(context, getLanguage(context));
    }

    public static Context setNewLocale(Context context, String languageCode) {
        persistLanguage(context, languageCode);
        return updateResources(context, languageCode);
    }

    public static String getLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString(KeyValueHelper.KEY_LANGUAGE, KeyValueHelper.DEFAULT_LANGUAGE);
    }

    private static void persistLanguage(Context context, String languageCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeyValueHelper.KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    private static Context updateResources(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }
}
