package com.example.app_marifin_javadroid.data.local.model;

import androidx.room.ColumnInfo;

import java.math.BigDecimal;

/**
 * Aggregation result model for monthly expenses over time.
 */
public class MonthlyExpenseAggregate {

    @ColumnInfo(name = "month_year")
    private String monthYear; // e.g. "2026-08" or "Agu"

    @ColumnInfo(name = "total_amount")
    private BigDecimal totalAmount;

    public MonthlyExpenseAggregate() {}

    @androidx.room.Ignore
    public MonthlyExpenseAggregate(String monthYear, BigDecimal totalAmount) {
        this.monthYear = monthYear;
        this.totalAmount = totalAmount;
    }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    public BigDecimal getTotalAmount() { return totalAmount != null ? totalAmount : BigDecimal.ZERO; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
