package com.nsa.teamtwo.welshpharmacy;


import com.nsa.teamtwo.welshpharmacy.data.reminder.Date;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;


/**
 * Created by c1716791 on 09/05/2018.
 */

public class DateTest {


    @Test
    public void testDate(){
         Date myUnit = new Date(9,5,2018);

        // Current date tests
        assertEquals(9, myUnit.getDay());
        assertEquals(5, myUnit.getMonth());
        assertEquals(2018, myUnit.getYear());

        //toString
        assertEquals("9/5/2018", myUnit.toString());
    }


    @Test
    public void testDateWithString(){
        Date myUnit = new Date("09/05/2018");

        // Current date tests
        assertEquals(9, myUnit.getDay());
        assertEquals(5, myUnit.getMonth());
        assertEquals(2018, myUnit.getYear());
    }

    @Test
    public void testDateWithCalender(){

        //Calender
        java.util.Date date = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Date myUnit = new Date(calendar);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Current date tests
        assertEquals(day, myUnit.getDay());
        assertEquals(month, myUnit.getMonth());
        assertEquals(year, myUnit.getYear());
    }
}
