package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Domain Use Case to generate comprehensive Financial Report data and KPI ratios.
 */
public class GenerateFinancialReportUseCase {

    @NonNull
    public FinancialReportData execute(@NonNull String periodLabel,
                                       @NonNull Date startDate,
                                       @NonNull Date endDate,
                                       @Nullable List<TransactionEntity> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        int count = 0;

        if (transactions != null) {
            for (TransactionEntity tx : transactions) {
                if (tx.getAmount() == null || tx.getTransactionDate() == null) continue;

                // Date filtering
                if (tx.getTransactionDate().before(startDate) || tx.getTransactionDate().after(endDate)) {
                    continue;
                }

                if ("income".equalsIgnoreCase(tx.getType())) {
                    totalIncome = totalIncome.add(tx.getAmount());
                    count++;
                } else if ("expense".equalsIgnoreCase(tx.getType()) || "bill".equalsIgnoreCase(tx.getType())) {
                    totalExpense = totalExpense.add(tx.getAmount());
                    count++;
                }
            }
        }

        BigDecimal netCashFlow = totalIncome.subtract(totalExpense);

        int savingsRate = 0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0 && netCashFlow.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netCashFlow.multiply(BigDecimal.valueOf(100))
                    .divide(totalIncome, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        long diffMillis = Math.max(1, endDate.getTime() - startDate.getTime());
        long days = Math.max(1, TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS));

        BigDecimal avgDailyExpense = totalExpense.divide(BigDecimal.valueOf(days), 0, RoundingMode.HALF_UP);
        String dateRangeLabel = DateHelper.formatDateRange(startDate, endDate);

        return new FinancialReportData(
                periodLabel,
                dateRangeLabel,
                startDate,
                endDate,
                totalIncome,
                totalExpense,
                netCashFlow,
                savingsRate,
                avgDailyExpense,
                count
        );
    }
}
