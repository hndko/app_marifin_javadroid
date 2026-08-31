package com.example.app_marifin_javadroid.data.local.converters;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Room TypeConverter for BigDecimal monetary values (persisted as String for lossless precision).
 */
public class BigDecimalConverter {

    @TypeConverter
    public static String fromBigDecimal(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    @TypeConverter
    public static BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }
}
