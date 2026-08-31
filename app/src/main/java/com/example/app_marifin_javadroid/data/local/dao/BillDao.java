package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app_marifin_javadroid.data.local.entity.BillEntity;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Bills / Tagihan.
 */
@Dao
public interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BillEntity bill);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BillEntity> bills);

    @Update
    void update(BillEntity bill);

    @Delete
    void delete(BillEntity bill);

    @Query("SELECT * FROM bills WHERE user_id = :userId ORDER BY due_date ASC")
    LiveData<List<BillEntity>> getBillsLiveData(String userId);

    @Query("SELECT * FROM bills WHERE user_id = :userId ORDER BY due_date ASC")
    List<BillEntity> getBillsSync(String userId);

    @Query("SELECT * FROM bills WHERE user_id = :userId AND status != 'paid' ORDER BY due_date ASC")
    LiveData<List<BillEntity>> getUpcomingBillsLiveData(String userId);

    @Query("SELECT * FROM bills WHERE id = :id LIMIT 1")
    BillEntity getBillByIdSync(String id);
}
