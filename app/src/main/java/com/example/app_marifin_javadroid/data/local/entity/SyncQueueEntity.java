package com.example.app_marifin_javadroid.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Room Entity for Offline Mutation Sync Queue (used by WorkManager).
 */
@Entity(
        tableName = "sync_queue",
        indices = {@Index("created_at")}
)
public class SyncQueueEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "entity_type")
    private String entityType; // 'transaction', 'account', 'budget', 'bill', 'goal', 'document'

    @NonNull
    @ColumnInfo(name = "entity_id")
    private String entityId;

    @NonNull
    @ColumnInfo(name = "operation")
    private String operation; // 'INSERT', 'UPDATE', 'DELETE'

    @NonNull
    @ColumnInfo(name = "payload_json")
    private String payloadJson;

    @NonNull
    @ColumnInfo(name = "created_at")
    private Date createdAt = new Date();

    @ColumnInfo(name = "retry_count")
    private int retryCount = 0;

    public SyncQueueEntity() {}

    @androidx.room.Ignore
    public SyncQueueEntity(@NonNull String entityType, @NonNull String entityId,
                           @NonNull String operation, @NonNull String payloadJson) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.operation = operation;
        this.payloadJson = payloadJson;
        this.createdAt = new Date();
        this.retryCount = 0;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getEntityType() { return entityType; }
    public void setEntityType(@NonNull String entityType) { this.entityType = entityType; }

    @NonNull
    public String getEntityId() { return entityId; }
    public void setEntityId(@NonNull String entityId) { this.entityId = entityId; }

    @NonNull
    public String getOperation() { return operation; }
    public void setOperation(@NonNull String operation) { this.operation = operation; }

    @NonNull
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(@NonNull String payloadJson) { this.payloadJson = payloadJson; }

    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
