package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Room Entity for Financial Goals / Target Finansial.
 */
@Entity(
        tableName = "financial_goals",
        foreignKeys = {
                @ForeignKey(
                        entity = AccountEntity.class,
                        parentColumns = "id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("user_id"), @Index("account_id")}
)
public class GoalEntity {

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
    @ColumnInfo(name = "target_amount")
    private BigDecimal targetAmount;

    @NonNull
    @ColumnInfo(name = "current_amount")
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Nullable
    @ColumnInfo(name = "deadline")
    private Date deadline;

    @Nullable
    @ColumnInfo(name = "account_id")
    private String accountId;

    @NonNull
    @ColumnInfo(name = "status")
    private String status = "in_progress"; // 'in_progress', 'achieved', 'cancelled'

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    public GoalEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public GoalEntity(@NonNull String id, @NonNull String userId, @NonNull String name,
                      @NonNull BigDecimal targetAmount, @NonNull BigDecimal currentAmount,
                      @Nullable Date deadline) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.status = "in_progress";
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
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(@NonNull BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    @NonNull
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(@NonNull BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    @Nullable
    public Date getDeadline() { return deadline; }
    public void setDeadline(@Nullable Date deadline) { this.deadline = deadline; }

    @Nullable
    public String getAccountId() { return accountId; }
    public void setAccountId(@Nullable String accountId) { this.accountId = accountId; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }
}
