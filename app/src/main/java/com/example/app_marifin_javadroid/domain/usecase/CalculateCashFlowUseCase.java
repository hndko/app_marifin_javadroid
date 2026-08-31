package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * UseCase for calculating Cash Flow, Net Cash Flow, and Savings Rate.
 * Rules:
 * 1. Income adds to Total Income.
 * 2. Expense adds to Total Expense.
 * 3. Transfer is strictly excluded from Total Income and Total Expense.
 * 4. Net Cash Flow = Total Income - Total Expense.
 * 5. Uses BigDecimal for zero floating-point loss.
 */
public class CalculateCashFlowUseCase {

    public static class CashFlowResult {
        private final BigDecimal totalIncome;
        private final BigDecimal totalExpense;
        private final BigDecimal netCashFlow;
        private final BigDecimal savingsRate; // Percentage e.g. 25.50%

        public CashFlowResult(@NonNull BigDecimal totalIncome, @NonNull BigDecimal totalExpense,
                              @NonNull BigDecimal netCashFlow, @NonNull BigDecimal savingsRate) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.netCashFlow = netCashFlow;
            this.savingsRate = savingsRate;
        }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense; }
        public BigDecimal getNetCashFlow() { return netCashFlow; }
        public BigDecimal getSavingsRate() { return savingsRate; }
    }

    public CashFlowResult execute(@Nullable List<TransactionEntity> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        if (transactions != null) {
            for (TransactionEntity tx : transactions) {
                if (tx.getAmount() == null || tx.getType() == null) continue;

                if ("income".equalsIgnoreCase(tx.getType())) {
                    totalIncome = totalIncome.add(tx.getAmount());
                } else if ("expense".equalsIgnoreCase(tx.getType())) {
                    totalExpense = totalExpense.add(tx.getAmount());
                }
                // 'transfer' is ignored per Financial Domain Rules
            }
        }

        BigDecimal netCashFlow = totalIncome.subtract(totalExpense);
        BigDecimal savingsRate = BigDecimal.ZERO;

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netCashFlow.multiply(BigDecimal.valueOf(100))
                    .divide(totalIncome, 2, RoundingMode.HALF_UP);
        }

        return new CashFlowResult(totalIncome, totalExpense, netCashFlow, savingsRate);
    }
}
