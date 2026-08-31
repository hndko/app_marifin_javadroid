package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;

import java.util.List;

/**
 * Data Access Object for Categories (System defaults and user custom categories).
 */
@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categories);

    @Update
    void update(CategoryEntity category);

    @Delete
    void delete(CategoryEntity category);

    @Query("SELECT * FROM categories WHERE (user_id = :userId OR is_default = 1) AND is_active = 1 ORDER BY is_favorite DESC, name ASC")
    LiveData<List<CategoryEntity>> getCategoriesLiveData(String userId);

    @Query("SELECT * FROM categories WHERE (user_id = :userId OR is_default = 1) AND is_active = 1 ORDER BY is_favorite DESC, name ASC")
    List<CategoryEntity> getCategoriesSync(String userId);

    @Query("SELECT * FROM categories WHERE (user_id = :userId OR is_default = 1) AND (type = :type OR type = 'both') AND is_active = 1 ORDER BY is_favorite DESC, name ASC")
    LiveData<List<CategoryEntity>> getCategoriesByTypeLiveData(String userId, String type);

    @Query("SELECT * FROM categories WHERE (user_id = :userId OR is_default = 1) AND (type = :type OR type = 'both') AND is_active = 1 ORDER BY is_favorite DESC, name ASC")
    List<CategoryEntity> getCategoriesByTypeSync(String userId, String type);

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    CategoryEntity getCategoryByIdSync(String id);

    @Query("SELECT COUNT(*) FROM categories WHERE is_default = 1")
    int getDefaultCategoriesCount();
}
