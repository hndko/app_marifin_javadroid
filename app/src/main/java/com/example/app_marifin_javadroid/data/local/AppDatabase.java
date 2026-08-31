package com.example.app_marifin_javadroid.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.app_marifin_javadroid.data.local.converters.BigDecimalConverter;
import com.example.app_marifin_javadroid.data.local.converters.DateConverter;
import com.example.app_marifin_javadroid.data.local.dao.AccountDao;
import com.example.app_marifin_javadroid.data.local.dao.BillDao;
import com.example.app_marifin_javadroid.data.local.dao.BudgetDao;
import com.example.app_marifin_javadroid.data.local.dao.CategoryDao;
import com.example.app_marifin_javadroid.data.local.dao.DocumentDao;
import com.example.app_marifin_javadroid.data.local.dao.GoalDao;
import com.example.app_marifin_javadroid.data.local.dao.SyncQueueDao;
import com.example.app_marifin_javadroid.data.local.dao.TransactionDao;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.data.local.entity.BudgetCategoryCrossRefEntity;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalContributionEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Room Database Singleton for MariFin.
 */
@Database(
        entities = {
                AccountEntity.class,
                CategoryEntity.class,
                TransactionEntity.class,
                BudgetEntity.class,
                BudgetCategoryCrossRefEntity.class,
                BillEntity.class,
                GoalEntity.class,
                GoalContributionEntity.class,
                DocumentEntity.class,
                SyncQueueEntity.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({BigDecimalConverter.class, DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "marifin_local.db";
    private static volatile AppDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract AccountDao accountDao();
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
    public abstract BillDao billDao();
    public abstract GoalDao goalDao();
    public abstract DocumentDao documentDao();
    public abstract SyncQueueDao syncQueueDao();

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        populateDefaultCategories(INSTANCE.categoryDao());
                                    });
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Seeds initial default categories if empty.
     */
    public static void populateDefaultCategories(CategoryDao categoryDao) {
        if (categoryDao.getDefaultCategoriesCount() == 0) {
            List<CategoryEntity> defaults = new ArrayList<>();
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Makanan & Minuman", "ic_category_food", "#EF4444", "expense", true, true));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Transportasi", "ic_category_transport", "#3AB4F2", "expense", true, true));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Kebutuhan Rumah", "ic_category_home", "#10B981", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Tagihan & Utilitas", "ic_category_bills", "#F59E0B", "expense", true, true));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Shopping", "ic_category_shopping", "#EC4899", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Kesehatan & Olahraga", "ic_category_health", "#06B6D4", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Hiburan", "ic_category_entertainment", "#8B5CF6", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Edukasi", "ic_category_education", "#3B82F6", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Investasi", "ic_category_investment", "#10B981", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Donasi & Hadiah", "ic_category_gift", "#F43F5E", "expense", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Gaji & Upah", "ic_category_salary", "#10B981", "income", true, true));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Bonus & THR", "ic_category_bonus", "#34D399", "income", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Pendapatan Bisnis", "ic_category_business", "#6366F1", "income", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Dividen & Bunga", "ic_category_dividend", "#14B8A6", "income", true, false));
            defaults.add(new CategoryEntity(UUID.randomUUID().toString(), null, "Lainnya", "ic_category_other", "#64748B", "both", true, false));

            categoryDao.insertAll(defaults);
        }
    }
}
