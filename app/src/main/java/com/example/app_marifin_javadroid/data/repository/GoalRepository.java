package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.AccountDao;
import com.example.app_marifin_javadroid.data.local.dao.GoalDao;
import com.example.app_marifin_javadroid.data.local.dao.TransactionDao;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.GoalDto;
import com.example.app_marifin_javadroid.data.remote.mapper.GoalMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Repository coordinating Financial Goals local cache, contributions, and Supabase cloud sync.
 */
public class GoalRepository {

    private static volatile GoalRepository INSTANCE;
    private final GoalDao goalDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> resource);
    }

    private GoalRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.goalDao = db.goalDao();
        this.accountDao = db.accountDao();
        this.transactionDao = db.transactionDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
    }

    public static GoalRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GoalRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GoalRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<GoalEntity>> getGoalsLiveData() {
        String userId = sessionManager.getUserId();
        return goalDao.getGoalsLiveData(userId != null ? userId : "");
    }

    public void refreshGoals(RepositoryCallback<List<GoalEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<GoalDto>> response = dataApi.getGoals("eq." + userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<GoalEntity> entities = GoalMapper.toEntityList(response.body());
                        goalDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {}
            }
            List<GoalEntity> local = goalDao.getGoalsSync(userId);
            if (callback != null) callback.onResult(Resource.success(local));
        });
    }

    public void createGoal(@NonNull GoalEntity goal, RepositoryCallback<GoalEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) goal.setUserId(userId);

        executor.execute(() -> {
            goalDao.insert(goal);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    GoalDto dto = GoalMapper.toDto(goal);
                    if (dto != null) dataApi.insertGoal(dto).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(goal));
        });
    }

    public void contributeToGoal(@NonNull GoalEntity goal, @NonNull String sourceAccountId,
                                 @NonNull BigDecimal amount, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            goalDao.contributeToGoal(accountDao, transactionDao, goal, sourceAccountId, amount);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    GoalDto dto = GoalMapper.toDto(goal);
                    if (dto != null) dataApi.updateGoal("eq." + goal.getId(), dto).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }

    public void deleteGoal(@NonNull GoalEntity goal, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            goalDao.delete(goal);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteGoal("eq." + goal.getId()).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
