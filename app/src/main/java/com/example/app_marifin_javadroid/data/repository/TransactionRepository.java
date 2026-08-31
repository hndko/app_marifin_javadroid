package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.AccountDao;
import com.example.app_marifin_javadroid.data.local.dao.SyncQueueDao;
import com.example.app_marifin_javadroid.data.local.dao.TransactionDao;
import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.TransactionDto;
import com.example.app_marifin_javadroid.data.remote.mapper.TransactionMapper;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import retrofit2.Response;

/**
 * Repository for Transactions coordinating Room atomic mutations and Supabase cloud sync.
 */
public class TransactionRepository {

    private static volatile TransactionRepository INSTANCE;
    private final Context context;
    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final SyncQueueDao syncQueueDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final ExecutorService executor;
    private final Gson gson = new Gson();

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> result);
    }

    public TransactionRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.transactionDao = db.transactionDao();
        this.accountDao = db.accountDao();
        this.syncQueueDao = db.syncQueueDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
        this.executor = AppDatabase.getDatabaseWriteExecutor();
    }

    public static TransactionRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (TransactionRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransactionRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<TransactionEntity>> getTransactionsLiveData() {
        String userId = sessionManager.getUserId();
        return transactionDao.getTransactionsLiveData(userId != null ? userId : "");
    }

    public LiveData<List<TransactionEntity>> getTransactionsByTypeLiveData(String type) {
        String userId = sessionManager.getUserId();
        return transactionDao.getTransactionsByTypeLiveData(userId != null ? userId : "", type);
    }

    public LiveData<List<TransactionEntity>> getTransactionsByAccountLiveData(String accountId) {
        String userId = sessionManager.getUserId();
        return transactionDao.getTransactionsByAccountLiveData(userId != null ? userId : "", accountId);
    }

    public LiveData<Double> getTotalIncomeLiveData(Date startDate, Date endDate) {
        String userId = sessionManager.getUserId();
        return transactionDao.getTotalIncomeLiveData(userId != null ? userId : "", startDate, endDate);
    }

    public LiveData<Double> getTotalExpenseLiveData(Date startDate, Date endDate) {
        String userId = sessionManager.getUserId();
        return transactionDao.getTotalExpenseLiveData(userId != null ? userId : "", startDate, endDate);
    }

    /**
     * Refreshes transaction list from Supabase.
     */
    public void refreshTransactions(RepositoryCallback<List<TransactionEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<TransactionDto>> response = dataApi.getTransactions("eq." + userId, 100).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<TransactionEntity> entities = TransactionMapper.toEntityList(response.body());
                        transactionDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {
                    // Fall back to Room
                }
            }

            List<TransactionEntity> cached = transactionDao.getTransactionsSync(userId);
            if (callback != null) callback.onResult(Resource.success(cached));
        });
    }

    /**
     * Atomically saves Income or Expense transaction and adjusts Account balance.
     */
    public void saveIncomeOrExpense(@NonNull TransactionEntity transaction, boolean isNew,
                                    RepositoryCallback<TransactionEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            transaction.setUserId(userId);
        }

        executor.execute(() -> {
            try {
                // 1. Atomic local mutation in Room
                transactionDao.insertTransactionAndUpdateAccount(accountDao, transaction);

                // 2. Sync to Supabase
                if (NetworkHelper.isNetworkAvailable(context)) {
                    try {
                        TransactionDto dto = TransactionMapper.toDto(transaction);
                        dataApi.insertTransaction(dto).execute();
                    } catch (Exception e) {
                        syncQueueDao.enqueue(new SyncQueueEntity(
                                "transaction",
                                transaction.getId(),
                                "INSERT",
                                gson.toJson(TransactionMapper.toDto(transaction))
                        ));
                    }
                } else {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "transaction",
                            transaction.getId(),
                            "INSERT",
                            gson.toJson(TransactionMapper.toDto(transaction))
                    ));
                }

                if (callback != null) callback.onResult(Resource.success(transaction));
            } catch (Exception e) {
                if (callback != null) callback.onResult(Resource.error(e.getMessage() != null ? e.getMessage() : "Gagal menyimpan transaksi."));
            }
        });
    }

    /**
     * Atomically executes inter-account transfer (reduces source, increases destination).
     */
    public void executeTransfer(@NonNull String sourceAccountId, @NonNull String destinationAccountId,
                                @NonNull BigDecimal amount, @NonNull String description,
                                @NonNull Date transactionDate, RepositoryCallback<TransactionEntity> callback) {
        String userId = sessionManager.getUserId();

        executor.execute(() -> {
            try {
                String transferGroupId = UUID.randomUUID().toString();

                TransactionEntity transferTx = new TransactionEntity();
                transferTx.setId(UUID.randomUUID().toString());
                transferTx.setUserId(userId != null ? userId : "");
                transferTx.setAccountId(sourceAccountId);
                transferTx.setDestinationAccountId(destinationAccountId);
                transferTx.setTransferGroupId(transferGroupId);
                transferTx.setType("transfer");
                transferTx.setAmount(amount);
                transferTx.setDescription(description);
                transferTx.setTransactionDate(transactionDate);

                // Atomic Room transfer
                transactionDao.executeTransfer(accountDao, sourceAccountId, destinationAccountId, amount, description, transactionDate);

                // Sync to Supabase
                if (NetworkHelper.isNetworkAvailable(context)) {
                    try {
                        dataApi.insertTransaction(TransactionMapper.toDto(transferTx)).execute();
                    } catch (Exception e) {
                        syncQueueDao.enqueue(new SyncQueueEntity(
                                "transaction",
                                transferTx.getId(),
                                "INSERT",
                                gson.toJson(TransactionMapper.toDto(transferTx))
                        ));
                    }
                } else {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "transaction",
                            transferTx.getId(),
                            "INSERT",
                            gson.toJson(TransactionMapper.toDto(transferTx))
                    ));
                }

                if (callback != null) callback.onResult(Resource.success(transferTx));
            } catch (Exception e) {
                if (callback != null) callback.onResult(Resource.error(e.getMessage() != null ? e.getMessage() : "Transfer gagal dilakukan."));
            }
        });
    }

    /**
     * Deletes transaction and atomically reverts account balance.
     */
    public void deleteTransaction(@NonNull TransactionEntity transaction, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                transactionDao.deleteTransactionAndRevertAccount(accountDao, transaction);

                if (NetworkHelper.isNetworkAvailable(context)) {
                    try {
                        dataApi.deleteTransaction("eq." + transaction.getId()).execute();
                    } catch (Exception e) {
                        syncQueueDao.enqueue(new SyncQueueEntity(
                                "transaction",
                                transaction.getId(),
                                "DELETE",
                                ""
                        ));
                    }
                } else {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "transaction",
                            transaction.getId(),
                            "DELETE",
                            ""
                    ));
                }

                if (callback != null) callback.onResult(Resource.success(null));
            } catch (Exception e) {
                if (callback != null) callback.onResult(Resource.error("Gagal menghapus transaksi."));
            }
        });
    }
}
