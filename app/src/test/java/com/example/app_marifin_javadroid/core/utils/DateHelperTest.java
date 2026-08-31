package com.example.app_marifin_javadroid.core.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for DateHelper formatting and calculation utilities.
 */
public class DateHelperTest {

    @Test
    public void testFormatDisplayFull() {
        Calendar cal = Calendar.getInstance(DateHelper.LOCALE_ID);
        cal.set(2026, Calendar.AUGUST, 4, 10, 0, 0); // 4 Agustus 2026 is Tuesday
        String formatted = DateHelper.formatDisplayFull(cal.getTime());
        assertTrue(formatted.toLowerCase().contains("selasa"));
        assertTrue(formatted.contains("04") || formatted.contains("4"));
        assertTrue(formatted.toLowerCase().contains("agustus"));
        assertTrue(formatted.contains("2026"));
    }

    @Test
    public void testFormatDisplayShort() {
        Calendar cal = Calendar.getInstance(DateHelper.LOCALE_ID);
        cal.set(2026, Calendar.AUGUST, 4);
        String formatted = DateHelper.formatDisplayShort(cal.getTime());
        assertTrue(formatted.contains("2026"));
    }

    @Test
    public void testIsoUtcParsingAndFormatting() {
        Date now = new Date();
        String iso = DateHelper.formatToIsoUtc(now);
        assertNotNull(iso);
        assertTrue(iso.contains("T") && iso.endsWith("Z"));

        Date parsed = DateHelper.parseIsoUtc(iso);
        assertNotNull(parsed);
        // Ensure within 1 second accuracy
        assertEquals(now.getTime() / 1000, parsed.getTime() / 1000);
    }

    @Test
    public void testStartAndEndOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.FEBRUARY, 15);
        Date testDate = cal.getTime();

        Date start = DateHelper.getStartOfMonth(testDate);
        Date end = DateHelper.getEndOfMonth(testDate);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        assertEquals(1, startCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, startCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, startCal.get(Calendar.MINUTE));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        assertEquals(28, endCal.get(Calendar.DAY_OF_MONTH)); // 2026 Feb has 28 days
        assertEquals(23, endCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, endCal.get(Calendar.MINUTE));
    }

    @Test
    public void testFormatDateRange() {
        Calendar start = Calendar.getInstance();
        start.set(2026, Calendar.AUGUST, 25);
        Calendar end = Calendar.getInstance();
        end.set(2026, Calendar.SEPTEMBER, 24);

        String range = DateHelper.formatDateRange(start.getTime(), end.getTime());
        assertTrue(range.contains("25"));
        assertTrue(range.contains("24"));
        assertTrue(range.contains("2026"));
    }
}
