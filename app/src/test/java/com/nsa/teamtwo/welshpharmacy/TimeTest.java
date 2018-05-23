package com.nsa.teamtwo.welshpharmacy;

/**
 * Created by c1716791 on 09/05/2018.
 */

import com.nsa.teamtwo.welshpharmacy.data.reminder.Time;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class TimeTest {

    @Test
    public void testTime(){
        Time myUnit = new Time(21,55);

        // Current time tests
        assertEquals(21, myUnit.getHour());
        assertEquals(55, myUnit.getMinute());

        //toString
        assertEquals("21:55", myUnit.toString());
    }

    @Test
    public void testDateWithString(){
        Time myUnit = new Time("21:55");

        // Current time tests
        assertEquals(21, myUnit.getHour());
        assertEquals(55, myUnit.getMinute());
    }

    @Test
    public void testDateWithCalender(){

        //Calender
        java.util.Date time = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        Time myUnit = new Time(calendar);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Current date tests
        assertEquals(hour, myUnit.getHour());
        assertEquals(minute, myUnit.getMinute());
    }


}
