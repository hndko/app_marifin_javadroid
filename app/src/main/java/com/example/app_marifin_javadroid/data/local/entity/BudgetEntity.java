package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Room Entity for Budgets.
 */
@Entity(
        tableName = "budgets",
        indices = {@Index("user_id"), @Index({"start_date", "end_date"})}
)
public class BudgetEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "amount")
    private BigDecimal amount;

    @NonNull
    @ColumnInfo(name = "period_type")
    private String periodType = "monthly"; // 'weekly', 'monthly', 'yearly', 'custom'

    @NonNull
    @ColumnInfo(name = "start_date")
    private Date startDate;

    @NonNull
    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "alert_threshold")
    private int alertThreshold = 80;

    @ColumnInfo(name = "is_active")
    private boolean isActive = true;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    public BudgetEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public BudgetEntity(@NonNull String id, @NonNull String userId, @NonNull String name,
                        @NonNull BigDecimal amount, @NonNull String periodType,
                        @NonNull Date startDate, @NonNull Date endDate, int alertThreshold) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertThreshold = alertThreshold;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public BigDecimal getAmount() { return amount; }
    public void setAmount(@NonNull BigDecimal amount) { this.amount = amount; }

    @NonNull
    public String getPeriodType() { return periodType; }
    public void setPeriodType(@NonNull String periodType) { this.periodType = periodType; }

    @NonNull
    public Date getStartDate() { return startDate; }
    public void setStartDate(@NonNull Date startDate) { this.startDate = startDate; }

    @NonNull
    public Date getEndDate() { return endDate; }
    public void setEndDate(@NonNull Date endDate) { this.endDate = endDate; }

    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }
}
