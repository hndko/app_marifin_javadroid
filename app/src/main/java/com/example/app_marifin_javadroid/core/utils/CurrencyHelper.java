package com.example.app_marifin_javadroid.core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Currency Utility for formatting and parsing Indonesian Rupiah (IDR) using BigDecimal.
 * Strictly avoids floating point numbers to prevent precision loss.
 */
public final class CurrencyHelper {

    private static final Locale LOCALE_ID = new Locale("id", "ID");
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

    private CurrencyHelper() {
        // Prevent instantiation
    }

    /**
     * Formats BigDecimal amount to Indonesian Rupiah standard format without decimals.
     * Example: 1683296 -> "Rp1.683.296"
     */
    @NonNull
    public static String formatRupiah(@Nullable BigDecimal amount) {
        if (amount == null) {
            return "Rp0";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(LOCALE_ID);
        symbols.setCurrencySymbol("Rp");
        symbols.setGroupingSeparator('.');
        symbols.setMonetaryDecimalSeparator(',');

        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(LOCALE_ID);
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        String formatted = formatter.format(amount);
        // Replace non-breaking spaces or standard spaces between Rp and number if any
        return formatted.replace("\u00A0", "").replace(" ", "");
    }

    /**
     * Formats BigDecimal amount with explicit +/- sign.
     * Example: +1500000 -> "+Rp1.500.000", -50000 -> "-Rp50.000", 0 -> "Rp0"
     */
    @NonNull
    public static String formatSignedRupiah(@Nullable BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return formatRupiah(BigDecimal.ZERO);
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return "+" + formatRupiah(amount);
        } else {
            return "-" + formatRupiah(amount.abs());
        }
    }

    /**
     * Parses formatted currency string (e.g. "Rp 1.500.000" or "1.500.000" or "1500000") to BigDecimal.
     * Returns BigDecimal.ZERO if input is null or invalid.
     */
    @NonNull
    public static BigDecimal parseRupiah(@Nullable String input) {
        if (input == null || input.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String clean = input.replaceAll("[^0-9-]", "");
        if (clean.isEmpty() || clean.equals("-")) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(clean);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculates Net Cash Flow: Total Income - Total Expense.
     */
    @NonNull
    public static BigDecimal calculateNetCashFlow(@Nullable BigDecimal totalIncome, @Nullable BigDecimal totalExpense) {
        BigDecimal income = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        BigDecimal expense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        return income.subtract(expense);
    }

    /**
     * Calculates percentage ratio of part against total: (part / total) * 100.
     * Returns 0 if total is zero or negative.
     */
    public static double calculatePercentage(@Nullable BigDecimal part, @Nullable BigDecimal total) {
        if (part == null || total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        return part.divide(total, 4, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
