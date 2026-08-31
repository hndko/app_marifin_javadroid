package com.example.app_marifin_javadroid.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app_marifin_javadroid.data.local.entity.SyncQueueEntity;

import java.util.List;

/**
 * Data Access Object for Background Offline Sync Queue.
 */
@Dao
public interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long enqueue(SyncQueueEntity item);

    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC LIMIT :limit")
    List<SyncQueueEntity> getNextPendingItems(int limit);

    @Query("DELETE FROM sync_queue WHERE id = :id")
    void deleteItem(long id);

    @Query("DELETE FROM sync_queue")
    void clearQueue();

    @Query("SELECT COUNT(*) FROM sync_queue")
    int getQueueCount();
}
