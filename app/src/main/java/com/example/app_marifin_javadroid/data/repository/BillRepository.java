package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.AccountDao;
import com.example.app_marifin_javadroid.data.local.dao.BillDao;
import com.example.app_marifin_javadroid.data.local.dao.TransactionDao;
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.BillDto;
import com.example.app_marifin_javadroid.data.remote.mapper.BillMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Repository coordinating Bill local cache and Supabase cloud persistence.
 */
public class BillRepository {

    private static volatile BillRepository INSTANCE;
    private final BillDao billDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> resource);
    }

    private BillRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.billDao = db.billDao();
        this.accountDao = db.accountDao();
        this.transactionDao = db.transactionDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
    }

    public static BillRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BillRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BillRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<BillEntity>> getBillsLiveData() {
        String userId = sessionManager.getUserId();
        return billDao.getBillsLiveData(userId != null ? userId : "");
    }

    public LiveData<List<BillEntity>> getUpcomingBillsLiveData() {
        String userId = sessionManager.getUserId();
        return billDao.getUpcomingBillsLiveData(userId != null ? userId : "");
    }

    public void refreshBills(RepositoryCallback<List<BillEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<BillDto>> response = dataApi.getBills("eq." + userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<BillEntity> entities = BillMapper.toEntityList(response.body());
                        billDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {}
            }
            List<BillEntity> local = billDao.getBillsSync(userId);
            if (callback != null) callback.onResult(Resource.success(local));
        });
    }

    public void createBill(@NonNull BillEntity bill, RepositoryCallback<BillEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) bill.setUserId(userId);

        executor.execute(() -> {
            billDao.insert(bill);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    BillDto dto = BillMapper.toDto(bill);
                    if (dto != null) dataApi.insertBill(dto).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(bill));
        });
    }

    public void payBill(@NonNull BillEntity bill, @NonNull String sourceAccountId, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            billDao.payBillAndRecordTransaction(accountDao, transactionDao, bill, sourceAccountId);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    BillDto dto = BillMapper.toDto(bill);
                    if (dto != null) dataApi.updateBill("eq." + bill.getId(), dto).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }

    public void deleteBill(@NonNull BillEntity bill, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            billDao.delete(bill);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteBill("eq." + bill.getId()).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
