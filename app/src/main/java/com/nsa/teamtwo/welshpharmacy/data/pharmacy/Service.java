package com.nsa.teamtwo.welshpharmacy.data.pharmacy;

import android.content.Context;

import com.nsa.teamtwo.welshpharmacy.util.Util;

import java.io.Serializable;

public class Service implements Serializable {
    private String name;
    private String welshName;
    private String description;
    private String welshDescription;

    public Service(String name, String welshName, String description, String welshDescription) {
        this.name = name;
        this.welshName = welshName;
        this.description = description;
        this.welshDescription = welshDescription;
    }

    public String getEnglishName() {
        return name;
    }

    public String getName(Context context) {
        if (Util.systemLanguageIsWelsh(context)) {
            return welshName;
        } else {
            return name;
        }
    }

    public String getDescription(Context context) {
        if (Util.systemLanguageIsWelsh(context)) {
            return welshDescription;
        } else {
            return description;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        else if (o instanceof Service) {
            Service otherService = (Service) o;
            return name.toLowerCase().equals(otherService.name.toLowerCase());
        } else if (o instanceof String) {
            String serviceName = (String) o;
            return name.toLowerCase().equals(serviceName.toLowerCase());
        }
        else return false;
    }
}