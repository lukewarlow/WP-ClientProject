package com.nsa.teamtwo.welshpharmacy;

/**
 * Created by c1716791 on 09/05/2018.
 */

import com.nsa.teamtwo.welshpharmacy.data.reminder.Date;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Reminder;
import com.nsa.teamtwo.welshpharmacy.data.reminder.RepeatType;
import com.nsa.teamtwo.welshpharmacy.data.reminder.Time;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReminderTest {

    @Test
    public void testText(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals("Test", myUnit.getReminderText());
    }

    @Test
    public void testDate(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals( new Date("09/05/2018").toString(), myUnit.getDate().toString());
    }

    @Test
    public void testTime(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals(new Time("22:10").toString(), myUnit.getTime().toString());
    }

    @Test
    public void testRepeat(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals(false, myUnit.shouldRepeat());

    }

    @Test
    public void testRepeatInterval(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals(0, myUnit.getRepeatInterval());

    }

    @Test
    public void testRepeatType(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);

        assertEquals(RepeatType.DAY, myUnit.getRepeatType());

    }

    @Test
    public void testNotifications(){

        Reminder myUnit = new Reminder("Test",
                new Date("09/05/2018"), new Time("22:10"), false, 0,
                RepeatType.DAY, true);


        assertEquals(true, myUnit.isNotificationActive());
        myUnit.toggleNotficationActive();
        assertEquals(false, myUnit.isNotificationActive());
        myUnit.toggleNotficationActive();
        assertEquals(true, myUnit.isNotificationActive());

    }
}
