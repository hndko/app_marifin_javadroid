package com.example.app_marifin_javadroid.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;

import java.util.List;

/**
 * Data Access Object for Transaction Documents / Receipts.
 */
@Dao
public interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DocumentEntity document);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DocumentEntity> documents);

    @Delete
    void delete(DocumentEntity document);

    @Query("SELECT * FROM documents WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<DocumentEntity>> getDocumentsLiveData(String userId);

    @Query("SELECT * FROM documents WHERE user_id = :userId ORDER BY created_at DESC")
    List<DocumentEntity> getDocumentsSync(String userId);

    @Query("SELECT * FROM documents WHERE transaction_id = :transactionId")
    List<DocumentEntity> getDocumentsByTransactionIdSync(String transactionId);
}
