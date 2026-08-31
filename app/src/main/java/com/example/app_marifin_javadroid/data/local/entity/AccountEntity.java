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
 * Room Entity for Financial Accounts (Bank, E-Wallet, Cash).
 */
@Entity(
        tableName = "financial_accounts",
        indices = {@Index("user_id")}
)
public class AccountEntity {

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
    @ColumnInfo(name = "institution_name")
    private String institutionName;

    @NonNull
    @ColumnInfo(name = "account_type")
    private String accountType; // 'Bank', 'E-Wallet', 'Cash', 'Credit Card', 'Investment', 'Other'

    @ColumnInfo(name = "account_number_masked")
    private String accountNumberMasked;

    @NonNull
    @ColumnInfo(name = "currency")
    private String currency = "IDR";

    @NonNull
    @ColumnInfo(name = "initial_balance")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @NonNull
    @ColumnInfo(name = "current_balance")
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @ColumnInfo(name = "icon_url")
    private String iconUrl;

    @ColumnInfo(name = "is_active")
    private boolean isActive = true;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @NonNull
    @ColumnInfo(name = "updated_at")
    private Date updatedAt = new Date();

    public AccountEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public AccountEntity(@NonNull String id, @NonNull String userId, @NonNull String name,
                         @NonNull String institutionName, @NonNull String accountType,
                         @NonNull BigDecimal initialBalance, @NonNull BigDecimal currentBalance) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.institutionName = institutionName;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
        this.currency = "IDR";
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
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(@NonNull String institutionName) { this.institutionName = institutionName; }

    @NonNull
    public String getAccountType() { return accountType; }
    public void setAccountType(@NonNull String accountType) { this.accountType = accountType; }

    public String getAccountNumberMasked() { return accountNumberMasked; }
    public void setAccountNumberMasked(String accountNumberMasked) { this.accountNumberMasked = accountNumberMasked; }

    @NonNull
    public String getCurrency() { return currency; }
    public void setCurrency(@NonNull String currency) { this.currency = currency; }

    @NonNull
    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(@NonNull BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    @NonNull
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(@NonNull BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    @NonNull
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(@NonNull Date updatedAt) { this.updatedAt = updatedAt; }
}
