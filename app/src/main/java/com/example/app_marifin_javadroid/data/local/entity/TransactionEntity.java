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
 * Room Entity for Transactions (Income, Expense, Transfer In, Transfer Out, Bill).
 */
@Entity(
        tableName = "transactions",
        foreignKeys = {
                @ForeignKey(
                        entity = AccountEntity.class,
                        parentColumns = "id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.RESTRICT
                ),
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index("user_id"),
                @Index("transaction_date"),
                @Index("type"),
                @Index("category_id"),
                @Index("account_id"),
                @Index("transfer_group_id")
        }
)
public class TransactionEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "account_id")
    private String accountId;

    @Nullable
    @ColumnInfo(name = "category_id")
    private String categoryId;

    @NonNull
    @ColumnInfo(name = "type")
    private String type; // 'expense', 'income', 'transfer_in', 'transfer_out', 'bill'

    @NonNull
    @ColumnInfo(name = "amount")
    private BigDecimal amount;

    @NonNull
    @ColumnInfo(name = "currency")
    private String currency = "IDR";

    @Nullable
    @ColumnInfo(name = "merchant")
    private String merchant;

    @Nullable
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "transaction_date")
    private Date transactionDate = new Date();

    @NonNull
    @ColumnInfo(name = "source")
    private String source = "manual"; // 'manual', 'ai', 'recurring', 'import'

    @ColumnInfo(name = "attachment_count")
    private int attachmentCount = 0;

    @Nullable
    @ColumnInfo(name = "transfer_group_id")
    private String transferGroupId;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    @Nullable
    @ColumnInfo(name = "deleted_at")
    private Date deletedAt;

    public TransactionEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public TransactionEntity(@NonNull String id, @NonNull String userId, @NonNull String accountId,
                             @Nullable String categoryId, @NonNull String type, @NonNull BigDecimal amount,
                             @Nullable String merchant, @Nullable String description, @NonNull Date transactionDate) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.merchant = merchant;
        this.description = description;
        this.transactionDate = transactionDate;
        this.currency = "IDR";
        this.source = "manual";
        this.attachmentCount = 0;
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
    public String getAccountId() { return accountId; }
    public void setAccountId(@NonNull String accountId) { this.accountId = accountId; }

    @Nullable
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(@Nullable String categoryId) { this.categoryId = categoryId; }

    @NonNull
    public String getType() { return type; }
    public void setType(@NonNull String type) { this.type = type; }

    @NonNull
    public BigDecimal getAmount() { return amount; }
    public void setAmount(@NonNull BigDecimal amount) { this.amount = amount; }

    @NonNull
    public String getCurrency() { return currency; }
    public void setCurrency(@NonNull String currency) { this.currency = currency; }

    @Nullable
    public String getMerchant() { return merchant; }
    public void setMerchant(@Nullable String merchant) { this.merchant = merchant; }

    @Nullable
    public String getDescription() { return description; }
    public void setDescription(@Nullable String description) { this.description = description; }

    @NonNull
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(@NonNull Date transactionDate) { this.transactionDate = transactionDate; }

    @NonNull
    public String getSource() { return source; }
    public void setSource(@NonNull String source) { this.source = source; }

    public int getAttachmentCount() { return attachmentCount; }
    public void setAttachmentCount(int attachmentCount) { this.attachmentCount = attachmentCount; }

    @Nullable
    public String getTransferGroupId() { return transferGroupId; }
    public void setTransferGroupId(@Nullable String transferGroupId) { this.transferGroupId = transferGroupId; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }

    @Nullable
    public Date getDeletedAt() { return deletedAt; }
    public void setDeletedAt(@Nullable Date deletedAt) { this.deletedAt = deletedAt; }
}
