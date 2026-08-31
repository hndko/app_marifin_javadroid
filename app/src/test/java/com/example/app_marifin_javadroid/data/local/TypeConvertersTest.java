package com.example.app_marifin_javadroid.data.local;

import com.example.app_marifin_javadroid.data.local.converters.BigDecimalConverter;
import com.example.app_marifin_javadroid.data.local.converters.DateConverter;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for Room Type Converters (Lossless precision for BigDecimal and epoch Date conversion).
 */
public class TypeConvertersTest {

    @Test
    public void testBigDecimalConverter() {
        BigDecimal original = new BigDecimal("1234567890123.45");
        String stringVal = BigDecimalConverter.fromBigDecimal(original);
        assertEquals("1234567890123.45", stringVal);

        BigDecimal parsed = BigDecimalConverter.toBigDecimal(stringVal);
        assertEquals(original, parsed);

        assertNull(BigDecimalConverter.fromBigDecimal(null));
        assertNull(BigDecimalConverter.toBigDecimal(null));
    }

    @Test
    public void testDateConverter() {
        Date original = new Date(1777777777000L);
        Long epoch = DateConverter.fromDate(original);
        assertEquals(Long.valueOf(1777777777000L), epoch);

        Date parsed = DateConverter.toDate(epoch);
        assertEquals(original, parsed);

        assertNull(DateConverter.fromDate(null));
        assertNull(DateConverter.toDate(null));
    }
}
