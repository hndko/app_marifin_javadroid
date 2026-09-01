package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.BillEntity;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Bills / Tagihan.
 */
@Dao
public interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BillEntity bill);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BillEntity> bills);

    @Update
    void update(BillEntity bill);

    @Delete
    void delete(BillEntity bill);

    @Query("SELECT * FROM bills WHERE user_id = :userId ORDER BY due_date ASC")
    LiveData<List<BillEntity>> getBillsLiveData(String userId);

    @Query("SELECT * FROM bills WHERE user_id = :userId ORDER BY due_date ASC")
    List<BillEntity> getBillsSync(String userId);

    @Query("SELECT * FROM bills WHERE user_id = :userId AND status != 'paid' ORDER BY due_date ASC")
    LiveData<List<BillEntity>> getUpcomingBillsLiveData(String userId);

    @Query("SELECT * FROM bills WHERE id = :id LIMIT 1")
    BillEntity getBillByIdSync(String id);

    @Query("DELETE FROM bills WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE FROM bills WHERE user_id = :userId")
    void deleteAllByUserId(String userId);

    @androidx.room.Transaction
    default void payBillAndRecordTransaction(AccountDao accountDao, TransactionDao transactionDao,
                                            BillEntity bill, String sourceAccountId) {
        bill.setStatus("paid");
        bill.setAccountId(sourceAccountId);
        bill.setUpdatedAt(new Date());
        update(bill);

        com.example.app_marifin_javadroid.data.local.entity.AccountEntity account = accountDao.getAccountByIdSync(sourceAccountId);
        if (account != null && bill.getAmount() != null) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(bill.getAmount()));
            accountDao.update(account);

            com.example.app_marifin_javadroid.data.local.entity.TransactionEntity tx = new com.example.app_marifin_javadroid.data.local.entity.TransactionEntity();
            tx.setUserId(bill.getUserId());
            tx.setAccountId(sourceAccountId);
            tx.setCategoryId(bill.getCategoryId());
            tx.setType("bill");
            tx.setAmount(bill.getAmount());
            tx.setDescription("Pembayaran Tagihan: " + bill.getName());
            tx.setTransactionDate(new Date());
            transactionDao.insert(tx);
        }
    }
}
