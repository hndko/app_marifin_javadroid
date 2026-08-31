package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.BudgetDao;
import com.example.app_marifin_javadroid.data.local.dao.CategoryDao;
import com.example.app_marifin_javadroid.data.local.entity.BudgetCategoryCrossRefEntity;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.BudgetDto;
import com.example.app_marifin_javadroid.data.remote.mapper.BudgetMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Repository coordinating Budget local database and Supabase cloud sync.
 */
public class BudgetRepository {

    private static volatile BudgetRepository INSTANCE;
    private final BudgetDao budgetDao;
    private final CategoryDao categoryDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> resource);
    }

    private BudgetRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.budgetDao = db.budgetDao();
        this.categoryDao = db.categoryDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
    }

    public static BudgetRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BudgetRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BudgetRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<BudgetEntity>> getBudgetsLiveData() {
        String userId = sessionManager.getUserId();
        return budgetDao.getBudgetsLiveData(userId != null ? userId : "");
    }

    public List<CategoryEntity> getCategoriesForBudget(String budgetId) {
        return budgetDao.getCategoriesForBudget(budgetId);
    }

    public void refreshBudgets(RepositoryCallback<List<BudgetEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<BudgetDto>> response = dataApi.getBudgets("eq." + userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<BudgetEntity> entities = BudgetMapper.toEntityList(response.body());
                        budgetDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {}
            }
            List<BudgetEntity> local = budgetDao.getBudgetsSync(userId);
            if (callback != null) callback.onResult(Resource.success(local));
        });
    }

    public void createBudget(@NonNull BudgetEntity budget, @NonNull List<String> categoryIds,
                             RepositoryCallback<BudgetEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) budget.setUserId(userId);

        executor.execute(() -> {
            budgetDao.insert(budget);
            budgetDao.deleteCategoriesForBudget(budget.getId());
            for (String catId : categoryIds) {
                budgetDao.insertBudgetCategoryCrossRef(new BudgetCategoryCrossRefEntity(budget.getId(), catId));
            }

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    BudgetDto dto = BudgetMapper.toDto(budget);
                    if (dto != null) {
                        dataApi.insertBudget(dto).execute();
                    }
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(budget));
        });
    }

    public void deleteBudget(@NonNull BudgetEntity budget, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            budgetDao.delete(budget);
            budgetDao.deleteCategoriesForBudget(budget.getId());

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteBudget("eq." + budget.getId()).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
