package com.nsa.teamtwo.welshpharmacy;

import com.nsa.teamtwo.welshpharmacy.data.pharmacy.Service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceTest {
    @Test
    public void testName() {
        Service service = new Service("Test Service", "Gibberish", "This is a test service", "Gibberish2");
        assertEquals("Test Service", service.getEnglishName());
    }

    @Test
    public void testEquals() {
        Service service = new Service("Test Service", "Gibberish", "This is a test service", "Gibberish2");
        assertTrue(service.equals(new Service("Test Service", "Gibberish", "This is a test service", "Gibberish2")));
    }

    @Test
    public void testEqualsString() {
        Service service = new Service("Test Service", "Gibberish", "This is a test service", "Gibberish2");
        assertTrue(service.equals("Test Service"));
    }
}
