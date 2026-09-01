package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.GoalContributionEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Financial Goals & Contributions.
 */
@Dao
public interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GoalEntity> goals);

    @Update
    void update(GoalEntity goal);

    @Delete
    void delete(GoalEntity goal);

    @Query("SELECT * FROM financial_goals WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<GoalEntity>> getGoalsLiveData(String userId);

    @Query("SELECT * FROM financial_goals WHERE user_id = :userId ORDER BY created_at DESC")
    List<GoalEntity> getGoalsSync(String userId);

    @Query("SELECT * FROM financial_goals WHERE id = :id LIMIT 1")
    GoalEntity getGoalByIdSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContribution(GoalContributionEntity contribution);

    @Query("SELECT * FROM goal_contributions WHERE goal_id = :goalId ORDER BY contribution_date DESC")
    List<GoalContributionEntity> getContributionsForGoalSync(String goalId);

    @Query("DELETE FROM financial_goals WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE FROM financial_goals WHERE user_id = :userId")
    void deleteAllByUserId(String userId);

    @androidx.room.Transaction
    default void contributeToGoal(AccountDao accountDao, TransactionDao transactionDao,
                                  GoalEntity goal, String sourceAccountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("achieved");
        }
        goal.setUpdatedAt(new Date());
        update(goal);

        com.example.app_marifin_javadroid.data.local.entity.AccountEntity account = accountDao.getAccountByIdSync(sourceAccountId);
        if (account != null) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
            accountDao.update(account);
        }

        com.example.app_marifin_javadroid.data.local.entity.TransactionEntity tx = new com.example.app_marifin_javadroid.data.local.entity.TransactionEntity();
        tx.setUserId(goal.getUserId());
        tx.setAccountId(sourceAccountId);
        tx.setType("transfer");
        tx.setAmount(amount);
        tx.setDescription("Setoran Target Finansial: " + goal.getName());
        tx.setTransactionDate(new Date());
        transactionDao.insert(tx);

        GoalContributionEntity contribution = new GoalContributionEntity();
        contribution.setGoalId(goal.getId());
        contribution.setTransactionId(tx.getId());
        contribution.setAmount(amount);
        contribution.setContributionDate(new Date());
        insertContribution(contribution);
    }
}
