package com.example.app_marifin_javadroid.data.local.model;

import androidx.room.ColumnInfo;

import java.math.BigDecimal;

/**
 * Aggregation result model for Category Expense breakdown.
 */
public class CategoryExpenseAggregate {

    @ColumnInfo(name = "category_id")
    private String categoryId;

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @ColumnInfo(name = "category_color")
    private String categoryColor;

    @ColumnInfo(name = "category_icon")
    private String categoryIcon;

    @ColumnInfo(name = "total_amount")
    private BigDecimal totalAmount;

    @ColumnInfo(name = "transaction_count")
    private int transactionCount;

    public CategoryExpenseAggregate() {}

    @androidx.room.Ignore
    public CategoryExpenseAggregate(String categoryId, String categoryName, String categoryColor,
                                    String categoryIcon, BigDecimal totalAmount, int transactionCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryColor() { return categoryColor; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }

    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }

    public BigDecimal getTotalAmount() { return totalAmount != null ? totalAmount : BigDecimal.ZERO; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
