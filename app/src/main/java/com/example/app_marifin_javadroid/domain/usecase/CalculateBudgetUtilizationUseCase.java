package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.local.model.BudgetWithProgress;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Domain Use Case to calculate budget utilization, remaining allowance, and 4-zone alert threshold.
 */
public class CalculateBudgetUtilizationUseCase {

    @NonNull
    public BudgetWithProgress execute(@NonNull BudgetEntity budget,
                                      @Nullable List<TransactionEntity> transactions,
                                      @Nullable List<CategoryEntity> budgetCategories) {
        BigDecimal limit = budget.getAmount() != null ? budget.getAmount() : BigDecimal.ZERO;
        BigDecimal spent = BigDecimal.ZERO;

        Set<String> categoryIds = new HashSet<>();
        if (budgetCategories != null) {
            for (CategoryEntity c : budgetCategories) {
                if (c.getId() != null) categoryIds.add(c.getId());
            }
        }

        if (transactions != null) {
            Date start = budget.getStartDate();
            Date end = budget.getEndDate();

            for (TransactionEntity tx : transactions) {
                if (!"expense".equalsIgnoreCase(tx.getType())) continue;
                if (tx.getAmount() == null) continue;

                // Date filtering
                Date txDate = tx.getTransactionDate();
                if (txDate != null && start != null && end != null) {
                    if (txDate.before(start) || txDate.after(end)) {
                        continue;
                    }
                }

                // Category filtering (if categories specified for this budget)
                if (!categoryIds.isEmpty()) {
                    if (tx.getCategoryId() == null || !categoryIds.contains(tx.getCategoryId())) {
                        continue;
                    }
                }

                spent = spent.add(tx.getAmount());
            }
        }

        BigDecimal remaining = limit.subtract(spent);

        int percentage = 0;
        if (limit.compareTo(BigDecimal.ZERO) > 0) {
            percentage = spent.multiply(BigDecimal.valueOf(100))
                    .divide(limit, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        BudgetWithProgress.StatusZone zone;
        if (percentage >= 100) {
            zone = BudgetWithProgress.StatusZone.OVER_BUDGET;
        } else if (percentage >= 90) {
            zone = BudgetWithProgress.StatusZone.DANGER;
        } else if (percentage >= 70) {
            zone = BudgetWithProgress.StatusZone.WARNING;
        } else {
            zone = BudgetWithProgress.StatusZone.SAFE;
        }

        return new BudgetWithProgress(budget, spent, remaining, percentage, zone, budgetCategories);
    }
}
