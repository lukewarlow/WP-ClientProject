package com.nsa.teamtwo.welshpharmacy.data;

import android.content.Context;

import com.nsa.teamtwo.welshpharmacy.util.Util;

public enum DayOfTheWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public String getEnglishName() {
        String name = this.toString().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public String getName(Context context) {
        if (Util.systemLanguageIsWelsh(context)) {
            return Util.getDayInWelsh(getEnglishName());
        } else {
            return getEnglishName();
        }
    }
}
