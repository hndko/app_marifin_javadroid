package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Transactions.
 */
@Dao
public abstract class TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(TransactionEntity transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<TransactionEntity> transactions);

    @Update
    public abstract void update(TransactionEntity transaction);

    @Delete
    public abstract void delete(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL ORDER BY transaction_date DESC")
    public abstract LiveData<List<TransactionEntity>> getTransactionsLiveData(String userId);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL ORDER BY transaction_date DESC")
    public abstract List<TransactionEntity> getTransactionsSync(String userId);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL ORDER BY transaction_date DESC LIMIT :limit OFFSET :offset")
    public abstract List<TransactionEntity> getTransactionsPagedSync(String userId, int limit, int offset);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL ORDER BY transaction_date DESC LIMIT :limit")
    public abstract LiveData<List<TransactionEntity>> getRecentTransactionsLiveData(String userId, int limit);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL AND transaction_date BETWEEN :startDate AND :endDate ORDER BY transaction_date DESC")
    public abstract LiveData<List<TransactionEntity>> getTransactionsByDateRangeLiveData(String userId, Date startDate, Date endDate);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL AND transaction_date BETWEEN :startDate AND :endDate ORDER BY transaction_date DESC")
    public abstract List<TransactionEntity> getTransactionsByDateRangeSync(String userId, Date startDate, Date endDate);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL AND category_id = :categoryId AND transaction_date BETWEEN :startDate AND :endDate")
    public abstract List<TransactionEntity> getExpenseTransactionsByCategorySync(String userId, String categoryId, Date startDate, Date endDate);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    public abstract TransactionEntity getTransactionByIdSync(String id);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    public abstract LiveData<TransactionEntity> getTransactionByIdLiveData(String id);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND deleted_at IS NULL AND (merchant LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%') ORDER BY transaction_date DESC")
    public abstract List<TransactionEntity> searchTransactionsSync(String userId, String keyword);

    @Query("DELETE FROM transactions WHERE user_id = :userId")
    public abstract void deleteAllByUserId(String userId);

    /**
     * Executes atomic local transfer between source and destination accounts.
     */
    @Transaction
    public void executeAtomicTransfer(TransactionEntity outTx, TransactionEntity inTx,
                                      AccountEntity sourceAccount, AccountEntity destAccount,
                                      AccountDao accountDao) {
        accountDao.update(sourceAccount);
        accountDao.update(destAccount);
        insert(outTx);
        insert(inTx);
    }
}
