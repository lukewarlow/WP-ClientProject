package com.nsa.teamtwo.welshpharmacy.data.reminder;

import android.content.Context;

import com.nsa.teamtwo.welshpharmacy.util.Util;

public enum RepeatType {
    MINUTE, HOUR, DAY, WEEK, MONTH;

    public String getEnglishName() {
        String name = toString().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String getName(Context context) {
        if (Util.systemLanguageIsWelsh(context)) {
            return Util.getRepeatTypeInWelsh(getEnglishName());
        } else {
            return getEnglishName();
        }
    }
}
