package com.example.app_marifin_javadroid.data.local.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Room TypeConverter for Date objects (persisted as Long epoch timestamp).
 */
public class DateConverter {

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
