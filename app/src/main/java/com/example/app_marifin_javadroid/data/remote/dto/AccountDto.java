package com.example.app_marifin_javadroid.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object for Supabase financial_accounts table.
 */
public class AccountDto {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("institution_name")
    private String institutionName;

    @SerializedName("account_type")
    private String accountType; // 'Bank', 'E-Wallet', 'Cash', 'Credit Card', 'Investment', 'Other'

    @SerializedName("account_number_masked")
    private String accountNumberMasked;

    @SerializedName("currency")
    private String currency = "IDR";

    @SerializedName("initial_balance")
    private BigDecimal initialBalance;

    @SerializedName("current_balance")
    private BigDecimal currentBalance;

    @SerializedName("icon_url")
    private String iconUrl;

    @SerializedName("is_active")
    private boolean isActive = true;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public AccountDto() {}

    public AccountDto(String id, String userId, String name, String institutionName,
                      String accountType, String accountNumberMasked,
                      BigDecimal initialBalance, BigDecimal currentBalance) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.institutionName = institutionName;
        this.accountType = accountType;
        this.accountNumberMasked = accountNumberMasked;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
        this.currency = "IDR";
        this.isActive = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountNumberMasked() { return accountNumberMasked; }
    public void setAccountNumberMasked(String accountNumberMasked) { this.accountNumberMasked = accountNumberMasked; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
