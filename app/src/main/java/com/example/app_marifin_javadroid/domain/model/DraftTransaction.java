package com.example.app_marifin_javadroid.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Domain model representing an AI-parsed draft transaction awaiting user confirmation.
 */
public class DraftTransaction {

    private String type = "expense";
    private BigDecimal amount = BigDecimal.ZERO;
    private String description;
    private String merchant;
    private String predictedCategoryName;
    private String suggestedAccountId;
    private Date transactionDate = new Date();
    private String rawText;
    private float confidenceScore = 1.0f;

    public DraftTransaction() {}

    public DraftTransaction(String type, BigDecimal amount, String description,
                            String merchant, String predictedCategoryName,
                            Date transactionDate, String rawText) {
        this.type = type;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.description = description;
        this.merchant = merchant;
        this.predictedCategoryName = predictedCategoryName;
        this.transactionDate = transactionDate != null ? transactionDate : new Date();
        this.rawText = rawText;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }

    public String getPredictedCategoryName() { return predictedCategoryName; }
    public void setPredictedCategoryName(String predictedCategoryName) { this.predictedCategoryName = predictedCategoryName; }

    public String getSuggestedAccountId() { return suggestedAccountId; }
    public void setSuggestedAccountId(String suggestedAccountId) { this.suggestedAccountId = suggestedAccountId; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(float confidenceScore) { this.confidenceScore = confidenceScore; }
}
