package com.example.app_marifin_javadroid.domain.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain model representing aggregated financial report metrics.
 */
public class FinancialReportData {

    private String periodLabel;
    private String dateRangeLabel;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;
    private BigDecimal netCashFlow = BigDecimal.ZERO;
    private int savingsRate = 0;
    private BigDecimal avgDailyExpense = BigDecimal.ZERO;
    private int transactionCount = 0;

    public FinancialReportData() {}

    public FinancialReportData(String periodLabel, String dateRangeLabel, Date startDate, Date endDate,
                               BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netCashFlow,
                               int savingsRate, BigDecimal avgDailyExpense, int transactionCount) {
        this.periodLabel = periodLabel;
        this.dateRangeLabel = dateRangeLabel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        this.totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        this.netCashFlow = netCashFlow != null ? netCashFlow : BigDecimal.ZERO;
        this.savingsRate = savingsRate;
        this.avgDailyExpense = avgDailyExpense != null ? avgDailyExpense : BigDecimal.ZERO;
        this.transactionCount = transactionCount;
    }

    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }

    public String getDateRangeLabel() { return dateRangeLabel; }
    public void setDateRangeLabel(String dateRangeLabel) { this.dateRangeLabel = dateRangeLabel; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }

    public BigDecimal getNetCashFlow() { return netCashFlow; }
    public void setNetCashFlow(BigDecimal netCashFlow) { this.netCashFlow = netCashFlow; }

    public int getSavingsRate() { return savingsRate; }
    public void setSavingsRate(int savingsRate) { this.savingsRate = savingsRate; }

    public BigDecimal getAvgDailyExpense() { return avgDailyExpense; }
    public void setAvgDailyExpense(BigDecimal avgDailyExpense) { this.avgDailyExpense = avgDailyExpense; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
