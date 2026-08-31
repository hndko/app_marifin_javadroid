package com.example.app_marifin_javadroid.core.utils;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Universal Form & Input Validator for MariFin.
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    private Validator() {
        // Prevent instantiation
    }

    public static boolean isValidEmail(@Nullable String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(@Nullable String password) {
        if (password == null) return false;
        return password.trim().length() >= 8;
    }

    public static boolean doPasswordsMatch(@Nullable String password, @Nullable String confirmation) {
        if (password == null || confirmation == null) return false;
        return password.equals(confirmation);
    }

    public static boolean isValidName(@Nullable String name) {
        if (name == null) return false;
        return name.trim().length() >= 2;
    }

    public static boolean isValidAmount(@Nullable BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
