package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.math.BigDecimal;

/**
 * Cross-Reference Entity for Many-to-Many relationship between Budgets and Categories.
 */
@Entity(
        tableName = "budget_categories",
        primaryKeys = {"budget_id", "category_id"},
        foreignKeys = {
                @ForeignKey(
                        entity = BudgetEntity.class,
                        parentColumns = "id",
                        childColumns = "budget_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("budget_id"),
                @Index("category_id")
        }
)
public class BudgetCategoryCrossRefEntity {

    @NonNull
    @ColumnInfo(name = "budget_id")
    private String budgetId;

    @NonNull
    @ColumnInfo(name = "category_id")
    private String categoryId;

    @NonNull
    @ColumnInfo(name = "allocated_amount")
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    public BudgetCategoryCrossRefEntity(@NonNull String budgetId, @NonNull String categoryId, @NonNull BigDecimal allocatedAmount) {
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.allocatedAmount = allocatedAmount;
    }

    @androidx.room.Ignore
    public BudgetCategoryCrossRefEntity(@NonNull String budgetId, @NonNull String categoryId) {
        this(budgetId, categoryId, BigDecimal.ZERO);
    }

    @NonNull
    public String getBudgetId() { return budgetId; }
    public void setBudgetId(@NonNull String budgetId) { this.budgetId = budgetId; }

    @NonNull
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(@NonNull String categoryId) { this.categoryId = categoryId; }

    @NonNull
    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(@NonNull BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; }
}
