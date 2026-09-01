package com.example.app_marifin_javadroid.core.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.SyncQueueDao;
import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.AccountDto;
import com.example.app_marifin_javadroid.data.remote.dto.BillDto;
import com.example.app_marifin_javadroid.data.remote.dto.BudgetDto;
import com.example.app_marifin_javadroid.data.remote.dto.DocumentDto;
import com.example.app_marifin_javadroid.data.remote.dto.GoalDto;
import com.example.app_marifin_javadroid.data.remote.dto.TransactionDto;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Background WorkManager Worker to flush and process offline-queued mutations to Supabase PostgREST.
 */
public class SyncQueueWorker extends Worker {

    private static final String TAG = "SyncQueueWorker";
    private static final String UNIQUE_WORK_NAME = "marifin_offline_sync_work";

    private final SyncQueueDao syncQueueDao;
    private final SupabaseDataApi dataApi;
    private final Gson gson = new Gson();

    public SyncQueueWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase db = AppDatabase.getInstance(context);
        this.syncQueueDao = db.syncQueueDao();
        this.dataApi = RetrofitClient.getInstance(context).getDataApi();
    }

    public static void schedulePeriodicSync(@NonNull Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                SyncQueueWorker.class,
                15, TimeUnit.MINUTES
        ).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!NetworkHelper.isNetworkAvailable(getApplicationContext())) {
            return Result.retry();
        }

        try {
            List<SyncQueueEntity> pendingItems = syncQueueDao.getNextPendingItems(30);
            if (pendingItems == null || pendingItems.isEmpty()) {
                return Result.success();
            }

            for (SyncQueueEntity item : pendingItems) {
                boolean success = processItem(item);
                if (success) {
                    syncQueueDao.deleteItem(item.getId());
                } else {
                    item.setRetryCount(item.getRetryCount() + 1);
                    if (item.getRetryCount() > 5) {
                        // Drop permanently failing corrupted items to unblock queue
                        syncQueueDao.deleteItem(item.getId());
                    }
                }
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error executing sync queue worker", e);
            return Result.retry();
        }
    }

    private boolean processItem(SyncQueueEntity item) {
        try {
            String type = item.getEntityType();
            String op = item.getOperation();
            String json = item.getPayloadJson();
            String entityId = item.getEntityId();

            if ("transaction".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    TransactionDto dto = gson.fromJson(json, TransactionDto.class);
                    return dataApi.insertTransaction(dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteTransaction("eq." + entityId).execute().isSuccessful();
                }
            } else if ("account".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    AccountDto dto = gson.fromJson(json, AccountDto.class);
                    return dataApi.insertAccount(dto).execute().isSuccessful();
                } else if ("UPDATE".equalsIgnoreCase(op)) {
                    AccountDto dto = gson.fromJson(json, AccountDto.class);
                    return dataApi.updateAccount("eq." + entityId, dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteAccount("eq." + entityId).execute().isSuccessful();
                }
            } else if ("budget".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    BudgetDto dto = gson.fromJson(json, BudgetDto.class);
                    return dataApi.insertBudget(dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteBudget("eq." + entityId).execute().isSuccessful();
                }
            } else if ("bill".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    BillDto dto = gson.fromJson(json, BillDto.class);
                    return dataApi.insertBill(dto).execute().isSuccessful();
                } else if ("UPDATE".equalsIgnoreCase(op)) {
                    BillDto dto = gson.fromJson(json, BillDto.class);
                    return dataApi.updateBill("eq." + entityId, dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteBill("eq." + entityId).execute().isSuccessful();
                }
            } else if ("goal".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    GoalDto dto = gson.fromJson(json, GoalDto.class);
                    return dataApi.insertGoal(dto).execute().isSuccessful();
                } else if ("UPDATE".equalsIgnoreCase(op)) {
                    GoalDto dto = gson.fromJson(json, GoalDto.class);
                    return dataApi.updateGoal("eq." + entityId, dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteGoal("eq." + entityId).execute().isSuccessful();
                }
            } else if ("document".equalsIgnoreCase(type)) {
                if ("INSERT".equalsIgnoreCase(op)) {
                    DocumentDto dto = gson.fromJson(json, DocumentDto.class);
                    return dataApi.insertDocument(dto).execute().isSuccessful();
                } else if ("DELETE".equalsIgnoreCase(op)) {
                    return dataApi.deleteDocument("eq." + entityId).execute().isSuccessful();
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to sync item: " + item.getId(), e);
            return false;
        }
    }
}
