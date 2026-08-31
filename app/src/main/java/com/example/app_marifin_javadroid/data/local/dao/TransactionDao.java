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

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = :type AND deleted_at IS NULL ORDER BY transaction_date DESC")
    public abstract LiveData<List<TransactionEntity>> getTransactionsByTypeLiveData(String userId, String type);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND (account_id = :accountId OR destination_account_id = :accountId) AND deleted_at IS NULL ORDER BY transaction_date DESC")
    public abstract LiveData<List<TransactionEntity>> getTransactionsByAccountLiveData(String userId, String accountId);

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'income' AND deleted_at IS NULL AND (:startDate IS NULL OR transaction_date >= :startDate) AND (:endDate IS NULL OR transaction_date <= :endDate)")
    public abstract LiveData<Double> getTotalIncomeLiveData(String userId, Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'expense' AND deleted_at IS NULL AND (:startDate IS NULL OR transaction_date >= :startDate) AND (:endDate IS NULL OR transaction_date <= :endDate)")
    public abstract LiveData<Double> getTotalExpenseLiveData(String userId, Date startDate, Date endDate);

    @Query("DELETE FROM transactions WHERE user_id = :userId")
    public abstract void deleteAllByUserId(String userId);

    /**
     * Atomically inserts income/expense transaction and mutates account balance.
     */
    @Transaction
    public void insertTransactionAndUpdateAccount(AccountDao accountDao, TransactionEntity transaction) {
        insert(transaction);
        AccountEntity account = accountDao.getAccountByIdSync(transaction.getAccountId());
        if (account != null && transaction.getAmount() != null) {
            if ("income".equalsIgnoreCase(transaction.getType())) {
                account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
            } else if ("expense".equalsIgnoreCase(transaction.getType())) {
                account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
            }
            accountDao.update(account);
        }
    }

    /**
     * Atomically deletes transaction and reverts account balance.
     */
    @Transaction
    public void deleteTransactionAndRevertAccount(AccountDao accountDao, TransactionEntity transaction) {
        delete(transaction);
        AccountEntity account = accountDao.getAccountByIdSync(transaction.getAccountId());
        if (account != null && transaction.getAmount() != null) {
            if ("income".equalsIgnoreCase(transaction.getType())) {
                account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
            } else if ("expense".equalsIgnoreCase(transaction.getType())) {
                account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
            }
            accountDao.update(account);
        }
    }

    /**
     * Executes atomic local transfer between source and destination accounts.
     */
    @Transaction
    public void executeTransfer(AccountDao accountDao, String sourceAccountId, String destAccountId,
                                BigDecimal amount, String description, Date transactionDate) {
        AccountEntity sourceAccount = accountDao.getAccountByIdSync(sourceAccountId);
        AccountEntity destAccount = accountDao.getAccountByIdSync(destAccountId);

        if (sourceAccount != null && destAccount != null && amount != null) {
            sourceAccount.setCurrentBalance(sourceAccount.getCurrentBalance().subtract(amount));
            destAccount.setCurrentBalance(destAccount.getCurrentBalance().add(amount));

            accountDao.update(sourceAccount);
            accountDao.update(destAccount);

            TransactionEntity transferTx = new TransactionEntity();
            transferTx.setAccountId(sourceAccountId);
            transferTx.setDestinationAccountId(destAccountId);
            transferTx.setType("transfer");
            transferTx.setAmount(amount);
            transferTx.setDescription(description);
            transferTx.setTransactionDate(transactionDate);
            transferTx.setUserId(sourceAccount.getUserId());

            insert(transferTx);
        }
    }

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
