package com.nsa.teamtwo.welshpharmacy.data.pharmacy;

import java.io.Serializable;

public class OpeningTime implements Serializable {
    private double openingTime;
    private double closingTime;

    public OpeningTime(double openingTime, double closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public double getOpeningTime() {
        return openingTime;
    }

    public double getClosingTime() {
        return closingTime;
    }
}
