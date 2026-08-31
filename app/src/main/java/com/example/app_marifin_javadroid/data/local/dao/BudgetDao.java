package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.BudgetCategoryCrossRefEntity;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;

import java.util.List;

/**
 * Data Access Object for Budgets and Budget-Category mappings.
 */
@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BudgetEntity budget);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BudgetEntity> budgets);

    @Update
    void update(BudgetEntity budget);

    @Delete
    void delete(BudgetEntity budget);

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND is_active = 1 ORDER BY start_date DESC")
    LiveData<List<BudgetEntity>> getBudgetsLiveData(String userId);

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND is_active = 1 ORDER BY start_date DESC")
    List<BudgetEntity> getBudgetsSync(String userId);

    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    BudgetEntity getBudgetByIdSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudgetCategoryCrossRef(BudgetCategoryCrossRefEntity crossRef);

    @Query("DELETE FROM budget_categories WHERE budget_id = :budgetId")
    void deleteCategoriesForBudget(String budgetId);

    @Query("SELECT c.* FROM categories c INNER JOIN budget_categories bc ON c.id = bc.category_id WHERE bc.budget_id = :budgetId")
    List<CategoryEntity> getCategoriesForBudget(String budgetId);
}
