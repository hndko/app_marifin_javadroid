package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.GoalContributionEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;

import java.util.List;

/**
 * Data Access Object for Financial Goals & Contributions.
 */
@Dao
public interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GoalEntity> goals);

    @Update
    void update(GoalEntity goal);

    @Delete
    void delete(GoalEntity goal);

    @Query("SELECT * FROM financial_goals WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<GoalEntity>> getGoalsLiveData(String userId);

    @Query("SELECT * FROM financial_goals WHERE user_id = :userId ORDER BY created_at DESC")
    List<GoalEntity> getGoalsSync(String userId);

    @Query("SELECT * FROM financial_goals WHERE id = :id LIMIT 1")
    GoalEntity getGoalByIdSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContribution(GoalContributionEntity contribution);

    @Query("SELECT * FROM goal_contributions WHERE goal_id = :goalId ORDER BY contribution_date DESC")
    List<GoalContributionEntity> getContributionsForGoalSync(String goalId);
}
