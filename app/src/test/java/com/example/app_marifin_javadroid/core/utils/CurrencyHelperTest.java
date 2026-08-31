package com.example.app_marifin_javadroid.core.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Comprehensive Unit Tests for CurrencyHelper.
 */
public class CurrencyHelperTest {

    @Test
    public void testFormatRupiah_StandardAmount() {
        BigDecimal amount = new BigDecimal("1683296");
        String result = CurrencyHelper.formatRupiah(amount);
        assertEquals("Rp1.683.296", result);
    }

    @Test
    public void testFormatRupiah_ZeroAndNull() {
        assertEquals("Rp0", CurrencyHelper.formatRupiah(BigDecimal.ZERO));
        assertEquals("Rp0", CurrencyHelper.formatRupiah(null));
    }

    @Test
    public void testFormatRupiah_LargeAmount() {
        BigDecimal amount = new BigDecimal("1000000000");
        String result = CurrencyHelper.formatRupiah(amount);
        assertEquals("Rp1.000.000.000", result);
    }

    @Test
    public void testFormatSignedRupiah() {
        assertEquals("+Rp1.500.000", CurrencyHelper.formatSignedRupiah(new BigDecimal("1500000")));
        assertEquals("-Rp50.000", CurrencyHelper.formatSignedRupiah(new BigDecimal("-50000")));
        assertEquals("Rp0", CurrencyHelper.formatSignedRupiah(BigDecimal.ZERO));
        assertEquals("Rp0", CurrencyHelper.formatSignedRupiah(null));
    }

    @Test
    public void testParseRupiah() {
        assertEquals(new BigDecimal("1500000"), CurrencyHelper.parseRupiah("Rp 1.500.000"));
        assertEquals(new BigDecimal("1500000"), CurrencyHelper.parseRupiah("1.500.000"));
        assertEquals(new BigDecimal("1500000"), CurrencyHelper.parseRupiah("1500000"));
        assertEquals(BigDecimal.ZERO, CurrencyHelper.parseRupiah(""));
        assertEquals(BigDecimal.ZERO, CurrencyHelper.parseRupiah(null));
        assertEquals(BigDecimal.ZERO, CurrencyHelper.parseRupiah("invalid"));
    }

    @Test
    public void testCalculateNetCashFlow() {
        BigDecimal income = new BigDecimal("5000000");
        BigDecimal expense = new BigDecimal("1500000");
        assertEquals(new BigDecimal("3500000"), CurrencyHelper.calculateNetCashFlow(income, expense));

        // Negative cash flow
        BigDecimal smallIncome = new BigDecimal("1000000");
        BigDecimal largeExpense = new BigDecimal("2500000");
        assertEquals(new BigDecimal("-1500000"), CurrencyHelper.calculateNetCashFlow(smallIncome, largeExpense));

        // Null handling
        assertEquals(new BigDecimal("5000000"), CurrencyHelper.calculateNetCashFlow(income, null));
        assertEquals(new BigDecimal("-1500000"), CurrencyHelper.calculateNetCashFlow(null, expense));
    }

    @Test
    public void testCalculatePercentage() {
        assertEquals(50.0, CurrencyHelper.calculatePercentage(new BigDecimal("500"), new BigDecimal("1000")), 0.01);
        assertEquals(33.3, CurrencyHelper.calculatePercentage(new BigDecimal("100"), new BigDecimal("300")), 0.01);
        assertEquals(0.0, CurrencyHelper.calculatePercentage(null, new BigDecimal("1000")), 0.01);
        assertEquals(0.0, CurrencyHelper.calculatePercentage(new BigDecimal("500"), BigDecimal.ZERO), 0.01);
        assertEquals(0.0, CurrencyHelper.calculatePercentage(new BigDecimal("500"), null), 0.01);
    }
}
