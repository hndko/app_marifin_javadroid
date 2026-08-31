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
 * Room Entity for Goal Contributions.
 */
@Entity(
        tableName = "goal_contributions",
        foreignKeys = {
                @ForeignKey(
                        entity = GoalEntity.class,
                        parentColumns = "id",
                        childColumns = "goal_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = TransactionEntity.class,
                        parentColumns = "id",
                        childColumns = "transaction_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("goal_id"), @Index("transaction_id")}
)
public class GoalContributionEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "goal_id")
    private String goalId;

    @NonNull
    @ColumnInfo(name = "amount")
    private BigDecimal amount;

    @Nullable
    @ColumnInfo(name = "transaction_id")
    private String transactionId;

    @NonNull
    @ColumnInfo(name = "contribution_date")
    private Date contributionDate = new Date();

    public GoalContributionEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public GoalContributionEntity(@NonNull String id, @NonNull String goalId,
                                  @NonNull BigDecimal amount, @Nullable String transactionId,
                                  @NonNull Date contributionDate) {
        this.id = id;
        this.goalId = goalId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.contributionDate = contributionDate;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getGoalId() { return goalId; }
    public void setGoalId(@NonNull String goalId) { this.goalId = goalId; }

    @NonNull
    public BigDecimal getAmount() { return amount; }
    public void setAmount(@NonNull BigDecimal amount) { this.amount = amount; }

    @Nullable
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(@Nullable String transactionId) { this.transactionId = transactionId; }

    @NonNull
    public Date getContributionDate() { return contributionDate; }
    public void setContributionDate(@NonNull Date contributionDate) { this.contributionDate = contributionDate; }
}
