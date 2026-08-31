package com.example.app_marifin_javadroid.core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Date Utility supporting Indonesian Locale, ISO formats, relative labels, and period calculations.
 */
public final class DateHelper {

    public static final Locale LOCALE_ID = new Locale("id", "ID");
    public static final String FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_DATE_ONLY = "yyyy-MM-dd";
    public static final String FORMAT_DISPLAY_FULL = "EEEE, dd MMMM yyyy";
    public static final String FORMAT_DISPLAY_SHORT = "dd MMM yyyy";
    public static final String FORMAT_MONTH_YEAR = "MMMM yyyy";

    private DateHelper() {
        // Prevent instantiation
    }

    /**
     * Formats Date to Indonesian display full date (e.g. "Selasa, 04 Agustus 2026").
     */
    @NonNull
    public static String formatDisplayFull(@Nullable Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DISPLAY_FULL, LOCALE_ID);
        return sdf.format(date);
    }

    /**
     * Formats Date to short Indonesian format (e.g. "04 Agu 2026").
     */
    @NonNull
    public static String formatDisplayShort(@Nullable Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DISPLAY_SHORT, LOCALE_ID);
        return sdf.format(date);
    }

    /**
     * Formats Date to Month & Year (e.g. "Agustus 2026").
     */
    @NonNull
    public static String formatMonthYear(@Nullable Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_MONTH_YEAR, LOCALE_ID);
        return sdf.format(date);
    }

    /**
     * Formats Date to ISO-8601 UTC string for Supabase sync.
     */
    @NonNull
    public static String formatToIsoUtc(@Nullable Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ISO_8601, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    /**
     * Parses ISO-8601 UTC string to Date.
     */
    @Nullable
    public static Date parseIsoUtc(@Nullable String isoString) {
        if (isoString == null || isoString.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ISO_8601, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(isoString);
        } catch (ParseException e) {
            // Fallback to DATE_ONLY format
            try {
                SimpleDateFormat fallbackSdf = new SimpleDateFormat(FORMAT_DATE_ONLY, Locale.US);
                return fallbackSdf.parse(isoString);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * Returns relative date label ("Hari ini", "Kemarin", "Besok") or formatted short date.
     */
    @NonNull
    public static String getRelativeDateLabel(@Nullable Date date) {
        if (date == null) return "-";

        Calendar target = Calendar.getInstance();
        target.setTime(date);

        Calendar today = Calendar.getInstance();

        if (isSameDay(target, today)) {
            return "Hari ini";
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(target, yesterday)) {
            return "Kemarin";
        }

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        if (isSameDay(target, tomorrow)) {
            return "Besok";
        }

        return formatDisplayShort(date);
    }

    /**
     * Checks if two calendars represent the same calendar day.
     */
    public static boolean isSameDay(@NonNull Calendar cal1, @NonNull Calendar cal2) {
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the start boundary (00:00:00.000) of the current month.
     */
    @NonNull
    public static Date getStartOfMonth(@NonNull Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Returns the end boundary (23:59:59.999) of the current month.
     */
    @NonNull
    public static Date getEndOfMonth(@NonNull Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Formats a date range into a readable string (e.g. "25 Agu - 24 Sep 2026").
     */
    @NonNull
    public static String formatDateRange(@NonNull Date startDate, @NonNull Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
            SimpleDateFormat startSdf = new SimpleDateFormat("dd MMM", LOCALE_ID);
            SimpleDateFormat endSdf = new SimpleDateFormat("dd MMM yyyy", LOCALE_ID);
            return startSdf.format(startDate) + " - " + endSdf.format(endDate);
        } else {
            SimpleDateFormat fullSdf = new SimpleDateFormat("dd MMM yyyy", LOCALE_ID);
            return fullSdf.format(startDate) + " - " + fullSdf.format(endDate);
        }
    }
}
