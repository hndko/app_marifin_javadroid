package com.example.app_marifin_javadroid.data.local;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app_marifin_javadroid.data.local.dao.AccountDao;
import com.example.app_marifin_javadroid.data.local.dao.CategoryDao;
import com.example.app_marifin_javadroid.data.local.dao.TransactionDao;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented In-Memory Room Database Test for DAOs and Atomic Transfers.
 */
@RunWith(AndroidJUnit4.class)
public class RoomDatabaseInstrumentedTest {

    private AppDatabase db;
    private AccountDao accountDao;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;

    private static final String TEST_USER_ID = UUID.randomUUID().toString();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        accountDao = db.accountDao();
        categoryDao = db.categoryDao();
        transactionDao = db.transactionDao();
    }

    @After
    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void testAccountAndCategoryInsertion() {
        AccountEntity account = new AccountEntity(
                UUID.randomUUID().toString(),
                TEST_USER_ID,
                "Blu by BCA Digital",
                "BCA Digital",
                "Bank",
                new BigDecimal("1500000"),
                new BigDecimal("1500000")
        );
        accountDao.insert(account);

        List<AccountEntity> accounts = accountDao.getAccountsSync(TEST_USER_ID);
        assertEquals(1, accounts.size());
        assertEquals("Blu by BCA Digital", accounts.get(0).getName());

        CategoryEntity category = new CategoryEntity(
                UUID.randomUUID().toString(),
                TEST_USER_ID,
                "Makanan & Minuman",
                "ic_food",
                "#EF4444",
                "expense",
                false,
                true
        );
        categoryDao.insert(category);

        List<CategoryEntity> categories = categoryDao.getCategoriesSync(TEST_USER_ID);
        assertEquals(1, categories.size());
        assertEquals("Makanan & Minuman", categories.get(0).getName());
    }

    @Test
    public void testAtomicTransferExecution() {
        String sourceId = UUID.randomUUID().toString();
        String destId = UUID.randomUUID().toString();

        AccountEntity source = new AccountEntity(
                sourceId,
                TEST_USER_ID,
                "Sumber",
                "Bank A",
                "Bank",
                new BigDecimal("1000000"),
                new BigDecimal("1000000")
        );
        AccountEntity dest = new AccountEntity(
                destId,
                TEST_USER_ID,
                "Tujuan",
                "Bank B",
                "Bank",
                new BigDecimal("200000"),
                new BigDecimal("200000")
        );

        accountDao.insert(source);
        accountDao.insert(dest);

        BigDecimal transferAmount = new BigDecimal("300000");

        // Mutate balances
        source.setCurrentBalance(source.getCurrentBalance().subtract(transferAmount));
        dest.setCurrentBalance(dest.getCurrentBalance().add(transferAmount));

        String transferGroupId = UUID.randomUUID().toString();
        Date now = new Date();

        TransactionEntity outTx = new TransactionEntity(
                UUID.randomUUID().toString(),
                TEST_USER_ID,
                sourceId,
                null,
                "transfer_out",
                transferAmount,
                null,
                "Transfer ke Tujuan",
                now
        );
        outTx.setTransferGroupId(transferGroupId);

        TransactionEntity inTx = new TransactionEntity(
                UUID.randomUUID().toString(),
                TEST_USER_ID,
                destId,
                null,
                "transfer_in",
                transferAmount,
                null,
                "Transfer dari Sumber",
                now
        );
        inTx.setTransferGroupId(transferGroupId);

        transactionDao.executeAtomicTransfer(outTx, inTx, source, dest, accountDao);

        // Verify updated account balances
        AccountEntity updatedSource = accountDao.getAccountByIdSync(sourceId);
        AccountEntity updatedDest = accountDao.getAccountByIdSync(destId);

        assertNotNull(updatedSource);
        assertNotNull(updatedDest);
        assertEquals(new BigDecimal("700000"), updatedSource.getCurrentBalance());
        assertEquals(new BigDecimal("500000"), updatedDest.getCurrentBalance());

        // Verify transactions
        List<TransactionEntity> txs = transactionDao.getTransactionsSync(TEST_USER_ID);
        assertEquals(2, txs.size());
    }
}
