package com.nsa.teamtwo.welshpharmacy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.nsa.teamtwo.welshpharmacy.util.KeyValueHelper.KEY_PREFERENCES;

public final class Util {
    public static List<Pharmacy> pharmacies = new ArrayList<>();
    public static List<Service> services = new ArrayList<>();
    public static final double LAT_MAX = 53.6;
    public static final double LAT_MIN = 51.3;
    public static final double LONG_MAX = -2.5;
    public static final double LONG_MIN = -6;

    public static void shortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void shortToast(Context context, int stringID) {
        Util.shortToast(context, context.getString(stringID));
    }

    public static void longToast(Context context, int stringID) {
        Util.longToast(context, context.getString(stringID));
    }

    public static boolean isWithinRadius(Context context, LatLng pharmacyLocation, LatLng userLocation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        int radiusInMiles = sharedPreferences.getInt(KeyValueHelper.KEY_RADIUS, KeyValueHelper.DEFAULT_RADIUS);
        if (radiusInMiles == 0) {
            radiusInMiles = 5;
        }
        double distanceInMeters = distance(userLocation.latitude, userLocation.longitude, pharmacyLocation.latitude, pharmacyLocation.longitude);
        return radiusInMiles > (distanceInMeters * 0.000621371);
    }

    //From https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1,
                lat2, lon2,
                results);
        return results[0];
    }

    public static boolean isDarkTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        boolean autoTheme = sharedPreferences.getBoolean(KeyValueHelper.KEY_THEME_AUTO, KeyValueHelper.DEFAULT_THEME_AUTO);
        if (autoTheme) {
            double currentTime = Util.getCurrentTimeAsDouble();
            return currentTime < 7 || currentTime > 19;
        } else {
            int theme = sharedPreferences.getInt(KeyValueHelper.KEY_THEME, KeyValueHelper.THEME_LIGHT);
            return theme == KeyValueHelper.THEME_DARK;
        }
    }

    //https://stackoverflow.com/questions/17841787/invert-colors-of-drawable-android?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public static Drawable getThemedDrawable(Context context, int drawableID) {
        float[] NEGATIVE = {
                -1.0f, 0, 0, 0, 255, // red
                0, -1.0f, 0, 0, 255, // green
                0, 0, -1.0f, 0, 255, // blue
                0, 0, 0, 1.0f, 0  // alpha
        };
        Drawable drawable = context.getDrawable(drawableID);
        if (drawable != null) {
            if (Util.isDarkTheme(context)) {
                drawable.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));
            }
        }
        return drawable;
    }

    public static double getCurrentTimeAsDouble() {
        double hour = Double.valueOf(new SimpleDateFormat("HH", Locale.ENGLISH).format(new Date()));
        double minutes = Double.valueOf(new SimpleDateFormat("mm", Locale.ENGLISH).format(new Date()));
        minutes /= 100;
        minutes /= 0.6;
        return hour + minutes;
    }

    public static String getCurrentTimeFromDouble(double time) {
        int hour = (int) Math.floor(time);
        double minutes = time - hour;
        minutes *= 100;
        minutes *= 0.6;
        if (minutes != 0)
            if (minutes < 10) {
                return hour + ":" + "0" + (int) minutes;
            } else {
                return hour + ":" + (int) minutes;
            }
        else
            return String.valueOf(hour);
    }

    public static String getDayInWelsh(String dayInEnglish) {
        String dayInWelsh = "Dydd ";
        switch (dayInEnglish) {
            case "Monday":
                dayInWelsh += "Llun";
                break;
            case "Tuesday":
                dayInWelsh += "Mawrth";
                break;
            case "Wednesday":
                dayInWelsh += "Mercher";
                break;
            case "Thursday":
                dayInWelsh += "Iau";
                break;
            case "Friday":
                dayInWelsh += "Gwener";
                break;
            case "Saturday":
                dayInWelsh += "Sadwrn";
                break;
            case "Sunday":
                dayInWelsh += "Sul";
                break;
            default:
                dayInWelsh = dayInEnglish;
                break;
        }
        return dayInWelsh;
    }

    public static String getRepeatTypeInWelsh(String repeatTypeInEnglish) {
        String repeatTypeInWelsh = "";
        switch (repeatTypeInEnglish) {
            case "Minute":
                repeatTypeInWelsh = "Munud";
                break;
            case "Hour":
                repeatTypeInWelsh = "Awr";
                break;
            case "Week":
                repeatTypeInWelsh = "Wythnos";
                break;
            case "Month":
                repeatTypeInWelsh = "Mis";
                break;
            default:
                repeatTypeInWelsh = repeatTypeInEnglish;
                break;
        }
        return repeatTypeInWelsh;
    }

    public static boolean systemLanguageIsWelsh(Context context) {
        Locale current = context.getResources().getConfiguration().locale;
        return current.getDisplayLanguage().equals("Cymraeg");
    }
}
