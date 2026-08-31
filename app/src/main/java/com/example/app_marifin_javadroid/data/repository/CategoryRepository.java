package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.CategoryDao;
import com.example.app_marifin_javadroid.data.local.dao.SyncQueueDao;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.CategoryDto;
import com.example.app_marifin_javadroid.data.remote.mapper.CategoryMapper;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Response;

/**
 * Repository for Categories coordinating Room local cache and Supabase cloud sync.
 */
public class CategoryRepository {

    private static volatile CategoryRepository INSTANCE;
    private final Context context;
    private final CategoryDao categoryDao;
    private final SyncQueueDao syncQueueDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final ExecutorService executor;
    private final Gson gson = new Gson();

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> result);
    }

    public CategoryRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.categoryDao = db.categoryDao();
        this.syncQueueDao = db.syncQueueDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
        this.executor = AppDatabase.getDatabaseWriteExecutor();
    }

    public static CategoryRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (CategoryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CategoryRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<CategoryEntity>> getCategoriesLiveData() {
        String userId = sessionManager.getUserId();
        return categoryDao.getCategoriesLiveData(userId != null ? userId : "");
    }

    public LiveData<List<CategoryEntity>> getCategoriesByTypeLiveData(String type) {
        String userId = sessionManager.getUserId();
        return categoryDao.getCategoriesByTypeLiveData(userId != null ? userId : "", type);
    }

    /**
     * Refreshes categories from Supabase (system defaults + user categories).
     */
    public void refreshCategories(RepositoryCallback<List<CategoryEntity>> callback) {
        String userId = sessionManager.getUserId();

        executor.execute(() -> {
            // Seed defaults locally if empty
            AppDatabase.populateDefaultCategories(categoryDao);

            if (NetworkHelper.isNetworkAvailable(context) && userId != null) {
                try {
                    String filter = "(is_default.eq.true,user_id.eq." + userId + ")";
                    Response<List<CategoryDto>> response = dataApi.getCategories(filter).execute();
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        List<CategoryEntity> entities = CategoryMapper.toEntityList(response.body());
                        categoryDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {
                    // Fall back to Room
                }
            }

            List<CategoryEntity> cached = categoryDao.getCategoriesSync(userId != null ? userId : "");
            if (callback != null) callback.onResult(Resource.success(cached));
        });
    }

    /**
     * Saves user custom category.
     */
    public void saveCategory(@NonNull CategoryEntity category, boolean isNew, RepositoryCallback<CategoryEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            category.setUserId(userId);
        }
        category.setDefault(false);

        executor.execute(() -> {
            if (isNew) {
                categoryDao.insert(category);
            } else {
                categoryDao.update(category);
            }

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    CategoryDto dto = CategoryMapper.toDto(category);
                    Response<List<CategoryDto>> response;
                    if (isNew) {
                        response = dataApi.insertCategory(dto).execute();
                    } else {
                        response = dataApi.updateCategory("eq." + category.getId(), dto).execute();
                    }

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        CategoryEntity synced = CategoryMapper.toEntity(response.body().get(0));
                        if (synced != null) {
                            categoryDao.insert(synced);
                        }
                    }
                } catch (Exception e) {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "category",
                            category.getId(),
                            isNew ? "INSERT" : "UPDATE",
                            gson.toJson(CategoryMapper.toDto(category))
                    ));
                }
            } else {
                syncQueueDao.enqueue(new SyncQueueEntity(
                        "category",
                        category.getId(),
                        isNew ? "INSERT" : "UPDATE",
                        gson.toJson(CategoryMapper.toDto(category))
                ));
            }

            if (callback != null) callback.onResult(Resource.success(category));
        });
    }

    /**
     * Deletes user custom category. Default system categories cannot be deleted.
     */
    public void deleteCategory(@NonNull CategoryEntity category, RepositoryCallback<Void> callback) {
        if (category.isDefault()) {
            if (callback != null) callback.onResult(Resource.error("Kategori sistem tidak dapat dihapus."));
            return;
        }

        executor.execute(() -> {
            categoryDao.delete(category);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteCategory("eq." + category.getId()).execute();
                } catch (Exception e) {
                    syncQueueDao.enqueue(new SyncQueueEntity(
                            "category",
                            category.getId(),
                            "DELETE",
                            ""
                    ));
                }
            } else {
                syncQueueDao.enqueue(new SyncQueueEntity(
                        "category",
                        category.getId(),
                        "DELETE",
                        ""
                ));
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
