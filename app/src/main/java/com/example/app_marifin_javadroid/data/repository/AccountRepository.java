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
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.AccountDto;
import com.example.app_marifin_javadroid.data.remote.mapper.AccountMapper;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Response;

/**
 * Repository for Financial Accounts coordinating Room local cache and Supabase cloud sync.
 */
public class AccountRepository {

    private static volatile AccountRepository INSTANCE;
    private final Context context;
    private final AccountDao accountDao;
    private final SyncQueueDao syncQueueDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final ExecutorService executor;
    private final Gson gson = new Gson();

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> result);
    }

    public AccountRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.accountDao = db.accountDao();
        this.syncQueueDao = db.syncQueueDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
        this.executor = AppDatabase.getDatabaseWriteExecutor();
    }

    public static AccountRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (AccountRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccountRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<AccountEntity>> getAccountsLiveData() {
        String userId = sessionManager.getUserId();
        return accountDao.getAccountsLiveData(userId != null ? userId : "");
    }

    public LiveData<AccountEntity> getAccountByIdLiveData(String id) {
        return accountDao.getAccountByIdLiveData(id);
    }

    public LiveData<Double> getTotalBalanceLiveData() {
        String userId = sessionManager.getUserId();
        return accountDao.getTotalBalanceLiveData(userId != null ? userId : "");
    }

    /**
     * Refreshes account list from Supabase and updates Room database cache.
     */
    public void refreshAccounts(RepositoryCallback<List<AccountEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<AccountDto>> response = dataApi.getAccounts("eq." + userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<AccountEntity> entities = AccountMapper.toEntityList(response.body());
                        accountDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {
                    // Fall back to Room
                }
            }

            // Return cached data
            List<AccountEntity> cached = accountDao.getAccountsSync(userId);
            if (callback != null) callback.onResult(Resource.success(cached));
        });
    }

    /**
     * Saves or updates account both locally and syncs to Supabase.
     */
    public void saveAccount(@NonNull AccountEntity account, boolean isNew, RepositoryCallback<AccountEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            account.setUserId(userId);
        }

        executor.execute(() -> {
            // 1. Save locally in Room immediately (Optimistic UI)
            if (isNew) {
                accountDao.insert(account);
            } else {
                accountDao.update(account);
            }

            // 2. Sync to Supabase
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    AccountDto dto = AccountMapper.toDto(account);
                    Response<List<AccountDto>> response;
                    if (isNew) {
                        response = dataApi.insertAccount(dto).execute();
                    } else {
                        response = dataApi.updateAccount("eq." + account.getId(), dto).execute();
                    }

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        AccountEntity synced = AccountMapper.toEntity(response.body().get(0));
                        if (synced != null) {
                            accountDao.insert(synced);
                        }
                    }
                } catch (Exception e) {
                    // Enqueue for background sync
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "account",
                            account.getId(),
                            isNew ? "INSERT" : "UPDATE",
                            gson.toJson(AccountMapper.toDto(account))
                    ));
                }
            } else {
                // Offline: Enqueue for background sync
                syncQueueDao.enqueue(new SyncQueueEntity(
                        "account",
                        account.getId(),
                        isNew ? "INSERT" : "UPDATE",
                        gson.toJson(AccountMapper.toDto(account))
                ));
            }

            if (callback != null) callback.onResult(Resource.success(account));
        });
    }

    /**
     * Deletes account locally and syncs deletion to Supabase.
     */
    public void deleteAccount(@NonNull AccountEntity account, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            accountDao.delete(account);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteAccount("eq." + account.getId()).execute();
                } catch (Exception e) {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "account",
                            account.getId(),
                            "DELETE",
                            ""
                    ));
                }
            } else {
                syncQueueDao.enqueue(new SyncQueueEntity(
                        "account",
                        account.getId(),
                        "DELETE",
                        ""
                ));
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
