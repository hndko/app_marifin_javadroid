package com.example.app_marifin_javadroid.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object for Supabase budgets table.
 */
public class BudgetDto {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("amount_limit")
    private BigDecimal amountLimit;

    @SerializedName("period")
    private String period = "monthly"; // 'monthly', 'weekly', 'custom'

    @SerializedName("start_date")
    private Date startDate;

    @SerializedName("end_date")
    private Date endDate;

    @SerializedName("alert_threshold")
    private int alertThreshold = 80;

    @SerializedName("is_active")
    private boolean isActive = true;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public BudgetDto() {}

    public BudgetDto(String id, String userId, String name, BigDecimal amountLimit,
                     String period, Date startDate, Date endDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.amountLimit = amountLimit;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertThreshold = 80;
        this.isActive = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmountLimit() { return amountLimit; }
    public void setAmountLimit(BigDecimal amountLimit) { this.amountLimit = amountLimit; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
