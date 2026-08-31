package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * Room Entity for Transaction Documents / Receipts.
 */
@Entity(
        tableName = "documents",
        foreignKeys = {
                @ForeignKey(
                        entity = TransactionEntity.class,
                        parentColumns = "id",
                        childColumns = "transaction_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("user_id"), @Index("transaction_id")}
)
public class DocumentEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @Nullable
    @ColumnInfo(name = "transaction_id")
    private String transactionId;

    @NonNull
    @ColumnInfo(name = "storage_path")
    private String storagePath;

    @NonNull
    @ColumnInfo(name = "original_name")
    private String originalName;

    @NonNull
    @ColumnInfo(name = "mime_type")
    private String mimeType;

    @ColumnInfo(name = "file_size")
    private long fileSize;

    @NonNull
    @ColumnInfo(name = "document_type")
    private String documentType = "receipt"; // 'receipt', 'invoice', 'bank_statement', 'other'

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    public DocumentEntity() {
        this.id = UUID.randomUUID().toString();
    }

    @androidx.room.Ignore
    public DocumentEntity(@NonNull String id, @NonNull String userId, @Nullable String transactionId,
                          @NonNull String storagePath, @NonNull String originalName,
                          @NonNull String mimeType, long fileSize, @NonNull String documentType) {
        this.id = id;
        this.userId = userId;
        this.transactionId = transactionId;
        this.storagePath = storagePath;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.documentType = documentType;
        this.createdAt = new Date();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @Nullable
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(@Nullable String transactionId) { this.transactionId = transactionId; }

    @NonNull
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(@NonNull String storagePath) { this.storagePath = storagePath; }

    @NonNull
    public String getOriginalName() { return originalName; }
    public void setOriginalName(@NonNull String originalName) { this.originalName = originalName; }

    @NonNull
    public String getMimeType() { return mimeType; }
    public void setMimeType(@NonNull String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    @NonNull
    public String getDocumentType() { return documentType; }
    public void setDocumentType(@NonNull String documentType) { this.documentType = documentType; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
}
