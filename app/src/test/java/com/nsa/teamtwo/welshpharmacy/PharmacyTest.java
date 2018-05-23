package com.nsa.teamtwo.welshpharmacy;

import com.nsa.teamtwo.welshpharmacy.data.DayOfTheWeek;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.OpeningTime;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Pharmacy;
import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.*;

public class PharmacyTest {
    @Test
    public void testGetName() {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, null, "01234567890", true);
        assertEquals("Test Pharmacy", pharmacy.getName());
    }

    @Test
    public void testGetLocation() {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, null, "01234567890", true);
        assertEquals(51, pharmacy.getLocation().latitude, 0);
        assertEquals(-3, pharmacy.getLocation().longitude, 0);
    }

    @Test
    public void testOpeningTimes() {
        HashMap<DayOfTheWeek, OpeningTime> openingTimes = new HashMap<>();
        for (DayOfTheWeek day : DayOfTheWeek.values()) {
            openingTimes.put(day, new OpeningTime(9, 5));
        }
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, openingTimes, "01234567890", true);
        assertEquals(9, pharmacy.getOpeningTime(DayOfTheWeek.MONDAY).getOpeningTime(), 0);
        assertEquals(5, pharmacy.getOpeningTime(DayOfTheWeek.MONDAY).getClosingTime(), 0);
    }

    @Test
    public void testIsOpen() {
        HashMap<DayOfTheWeek, OpeningTime> openingTimes = new HashMap<>();
        for (DayOfTheWeek day : DayOfTheWeek.values()) {
            openingTimes.put(day, new OpeningTime(9, 5));
        }
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, openingTimes, "01234567890", true);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()).toUpperCase();
        DayOfTheWeek day = DayOfTheWeek.valueOf(today);
        OpeningTime openingTime = pharmacy.getOpeningTime(day);
        if (openingTime.getOpeningTime() < 9 && openingTime.getClosingTime() > 5) assertTrue(pharmacy.isOpenNow());
        else assertFalse(pharmacy.isOpenNow());
    }

    @Test
    public void testWillOpen() {
        HashMap<DayOfTheWeek, OpeningTime> openingTimes = new HashMap<>();
        for (DayOfTheWeek day : DayOfTheWeek.values()) {
            if (day != DayOfTheWeek.SUNDAY) {
                openingTimes.put(day, new OpeningTime(9, 5));
            } else {
                openingTimes.put(day, new OpeningTime(0, 0));
            }
        }

        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, openingTimes, "01234567890", true);

        assertTrue(pharmacy.willOpen(DayOfTheWeek.MONDAY));
        assertFalse(pharmacy.willOpen(DayOfTheWeek.SUNDAY));
    }

    @Test
    public void testGetPhoneNumber() {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, null, "01234567890", true);
        assertEquals("01234567890", pharmacy.getPhoneNumber());
    }

    @Test
    public void testWelshAvailable() {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, null, "01234567890", true);
        assertTrue(pharmacy.isWelshAvailable());
    }

    @Test
    public void testDoesService() {
        Pharmacy pharmacy = new Pharmacy("Test Pharmacy", 51, -3, null, "01234567890", true);
        Service service = new Service("Test Service", "Gibberish", "This is a test service", "Gibberish2");
        pharmacy.addService(service);
        assertTrue(pharmacy.doesService(service));
    }
}
