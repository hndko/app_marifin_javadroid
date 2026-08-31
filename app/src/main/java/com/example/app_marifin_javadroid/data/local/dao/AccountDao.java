package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Access Object for Financial Accounts.
 */
@Dao
public interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AccountEntity account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountEntity> accounts);

    @Update
    void update(AccountEntity account);

    @Delete
    void delete(AccountEntity account);

    @Query("SELECT * FROM financial_accounts WHERE user_id = :userId AND is_active = 1 ORDER BY name ASC")
    LiveData<List<AccountEntity>> getAccountsLiveData(String userId);

    @Query("SELECT * FROM financial_accounts WHERE user_id = :userId AND is_active = 1 ORDER BY name ASC")
    List<AccountEntity> getAccountsSync(String userId);

    @Query("SELECT * FROM financial_accounts WHERE id = :id LIMIT 1")
    LiveData<AccountEntity> getAccountByIdLiveData(String id);

    @Query("SELECT * FROM financial_accounts WHERE id = :id LIMIT 1")
    AccountEntity getAccountByIdSync(String id);

    @Query("UPDATE financial_accounts SET current_balance = :newBalance, updated_at = :updatedAt WHERE id = :accountId")
    void updateBalance(String accountId, BigDecimal newBalance, long updatedAt);

    @Query("SELECT SUM(CAST(current_balance AS REAL)) FROM financial_accounts WHERE user_id = :userId AND is_active = 1")
    LiveData<Double> getTotalBalanceLiveData(String userId);

    @Query("DELETE FROM financial_accounts WHERE user_id = :userId")
    void deleteAllByUserId(String userId);
}
