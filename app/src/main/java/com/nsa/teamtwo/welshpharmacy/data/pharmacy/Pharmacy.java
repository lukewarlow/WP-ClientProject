package com.nsa.teamtwo.welshpharmacy.data.pharmacy;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.nsa.teamtwo.welshpharmacy.R;
import com.nsa.teamtwo.welshpharmacy.data.DayOfTheWeek;
import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Pharmacy implements Parcelable {
    private String name;
    private LatLng location;
    private HashMap<DayOfTheWeek, OpeningTime> openingTimes;
    private String phoneNumber;
    private boolean welshAvailable;
    private List<Service> services;

    public Pharmacy(String name, double lat, double lng, HashMap<DayOfTheWeek, OpeningTime> openingTimes, String phoneNumber, boolean welshAvailable) {
        this.name = name;
        this.location = new LatLng(lat, lng);
        this.openingTimes = openingTimes;
        this.phoneNumber = phoneNumber;
        this.welshAvailable = welshAvailable;
        services = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public Pharmacy(Parcel in) {
        name = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        openingTimes = (HashMap<DayOfTheWeek, OpeningTime>) in.readSerializable();
        phoneNumber = in.readString();
        welshAvailable = (Boolean) in.readSerializable();
        services = new ArrayList<>();
        in.readList(services, Service.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public OpeningTime getOpeningTime(DayOfTheWeek dayOfWeek) {
        return openingTimes.get(dayOfWeek);
    }

    public Pair<String, OpeningTime> getNextOpeningTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()).toUpperCase();

        while (true) {
            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()).toUpperCase();

            if (day.equals(today)) {
                if (Util.getCurrentTimeAsDouble() < getOpeningTime(DayOfTheWeek.valueOf(today)).getClosingTime()) {
                    return new Pair<>(context.getString(R.string.today), getOpeningTime(DayOfTheWeek.valueOf(day)));
                }
            } else if (willOpen(DayOfTheWeek.valueOf(day))) {
                if (Util.systemLanguageIsWelsh(context)) {
                    return new Pair<>(Util.getDayInWelsh(day).toLowerCase(), getOpeningTime(DayOfTheWeek.valueOf(day)));
                } else {
                    return new Pair<>(day.toLowerCase(), getOpeningTime(DayOfTheWeek.valueOf(day)));
                }
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public boolean willOpen(DayOfTheWeek dayOfTheWeek) {
        double openingTime = getOpeningTime(dayOfTheWeek).getOpeningTime();
        double closingTime = getOpeningTime(dayOfTheWeek).getClosingTime();

        return openingTime != closingTime;
    }

    public boolean isOpenNow() {
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date()).toUpperCase();
        double openingTime = openingTimes.get(DayOfTheWeek.valueOf(today)).getOpeningTime();
        double closingTime = openingTimes.get(DayOfTheWeek.valueOf(today)).getClosingTime();
        double currentTime = Util.getCurrentTimeAsDouble();
        if (openingTime == closingTime) {
            return false;
        } else {
            return currentTime > openingTime && currentTime < closingTime;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Boolean isWelshAvailable() {
        return welshAvailable;
    }

    public void addService(Service service) {
        if (!services.contains(service)) {
            services.add(service);
        } else {
            Log.d("Pharmacy", service.getEnglishName() + " already added to pharmacy.");
        }
    }

    public Boolean doesService(Service service) {
        return services.contains(service);
    }

    public List<Service> getServices() {
        return services;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeSerializable(openingTimes);
        dest.writeString(phoneNumber);
        dest.writeSerializable(welshAvailable);
        dest.writeList(services);
    }

    public static final Creator<Pharmacy> CREATOR = new Creator<Pharmacy>() {
        @Override
        public Pharmacy createFromParcel(Parcel source) {
            return new Pharmacy(source);
        }

        @Override
        public Pharmacy[] newArray(int size) {
            return new Pharmacy[size];
        }
    };
}
