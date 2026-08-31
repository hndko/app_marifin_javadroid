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
 * Room Entity for Bills / Tagihan Rutin.
 */
@Entity(
        tableName = "bills",
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = AccountEntity.class,
                        parentColumns = "id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index("user_id"),
                @Index("due_date"),
                @Index("category_id"),
                @Index("account_id")
        }
)
public class BillEntity {

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

    @Nullable
    @ColumnInfo(name = "category_id")
    private String categoryId;

    @Nullable
    @ColumnInfo(name = "account_id")
    private String accountId;

    @NonNull
    @ColumnInfo(name = "due_date")
    private Date dueDate;

    @NonNull
    @ColumnInfo(name = "recurrence")
    private String recurrence = "monthly"; // 'once', 'weekly', 'monthly', 'yearly'

    @NonNull
    @ColumnInfo(name = "status")
    private String status = "upcoming"; // 'upcoming', 'due', 'paid', 'overdue'

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    public BillEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public BillEntity(@NonNull String id, @NonNull String userId, @NonNull String name,
                      @NonNull BigDecimal amount, @NonNull Date dueDate,
                      @NonNull String recurrence, @NonNull String status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
        this.recurrence = recurrence;
        this.status = status;
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

    @Nullable
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(@Nullable String categoryId) { this.categoryId = categoryId; }

    @Nullable
    public String getAccountId() { return accountId; }
    public void setAccountId(@Nullable String accountId) { this.accountId = accountId; }

    @NonNull
    public Date getDueDate() { return dueDate; }
    public void setDueDate(@NonNull Date dueDate) { this.dueDate = dueDate; }

    @NonNull
    public String getRecurrence() { return recurrence; }
    public void setRecurrence(@NonNull String recurrence) { this.recurrence = recurrence; }

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
