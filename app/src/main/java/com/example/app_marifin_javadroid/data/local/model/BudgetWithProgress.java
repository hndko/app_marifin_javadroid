package com.example.app_marifin_javadroid.data.local.model;

import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite model representing a Budget along with calculated progress and alert status.
 */
public class BudgetWithProgress {

    public enum StatusZone {
        SAFE,        // < 70% (Green)
        WARNING,     // 70% - 89% (Amber/Yellow)
        DANGER,      // 90% - 99% (Orange)
        OVER_BUDGET  // >= 100% (Red)
    }

    private BudgetEntity budget;
    private BigDecimal spentAmount = BigDecimal.ZERO;
    private BigDecimal remainingAmount = BigDecimal.ZERO;
    private int percentage = 0;
    private StatusZone statusZone = StatusZone.SAFE;
    private List<CategoryEntity> categories = new ArrayList<>();

    public BudgetWithProgress() {}

    public BudgetWithProgress(BudgetEntity budget, BigDecimal spentAmount, BigDecimal remainingAmount,
                              int percentage, StatusZone statusZone, List<CategoryEntity> categories) {
        this.budget = budget;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentage = percentage;
        this.statusZone = statusZone;
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    public BudgetEntity getBudget() { return budget; }
    public void setBudget(BudgetEntity budget) { this.budget = budget; }

    public BigDecimal getSpentAmount() { return spentAmount != null ? spentAmount : BigDecimal.ZERO; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getRemainingAmount() { return remainingAmount != null ? remainingAmount : BigDecimal.ZERO; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public StatusZone getStatusZone() { return statusZone; }
    public void setStatusZone(StatusZone statusZone) { this.statusZone = statusZone; }

    public List<CategoryEntity> getCategories() { return categories; }
    public void setCategories(List<CategoryEntity> categories) { this.categories = categories; }
}
