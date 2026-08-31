package com.example.app_marifin_javadroid.core.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for Validator utility functions.
 */
public class ValidatorTest {

    @Test
    public void testEmailValidation() {
        assertTrue(Validator.isValidEmail("user@example.com"));
        assertTrue(Validator.isValidEmail("name.lastname@domain.co.id"));
        assertFalse(Validator.isValidEmail("invalid-email"));
        assertFalse(Validator.isValidEmail("@domain.com"));
        assertFalse(Validator.isValidEmail("user@"));
        assertFalse(Validator.isValidEmail(""));
        assertFalse(Validator.isValidEmail(null));
    }

    @Test
    public void testPasswordValidation() {
        assertTrue(Validator.isValidPassword("password123"));
        assertTrue(Validator.isValidPassword("12345678"));
        assertFalse(Validator.isValidPassword("short"));
        assertFalse(Validator.isValidPassword(""));
        assertFalse(Validator.isValidPassword(null));
    }

    @Test
    public void testPasswordMatching() {
        assertTrue(Validator.doPasswordsMatch("pass12345", "pass12345"));
        assertFalse(Validator.doPasswordsMatch("pass12345", "diffPass123"));
        assertFalse(Validator.doPasswordsMatch(null, "pass12345"));
        assertFalse(Validator.doPasswordsMatch("pass12345", null));
    }

    @Test
    public void testNameValidation() {
        assertTrue(Validator.isValidName("John"));
        assertTrue(Validator.isValidName("Al"));
        assertFalse(Validator.isValidName("A"));
        assertFalse(Validator.isValidName(""));
        assertFalse(Validator.isValidName(null));
    }

    @Test
    public void testAmountValidation() {
        assertTrue(Validator.isValidAmount(new BigDecimal("10000")));
        assertTrue(Validator.isValidAmount(new BigDecimal("0.01")));
        assertFalse(Validator.isValidAmount(BigDecimal.ZERO));
        assertFalse(Validator.isValidAmount(new BigDecimal("-5000")));
        assertFalse(Validator.isValidAmount(null));
    }
}
