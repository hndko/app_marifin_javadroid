package com.example.app_marifin_javadroid.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Data Transfer Object for Supabase documents table.
 */
public class DocumentDto {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("storage_path")
    private String storagePath;

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("mime_type")
    private String mimeType;

    @SerializedName("file_size")
    private long fileSize;

    @SerializedName("document_type")
    private String documentType = "receipt";

    @SerializedName("created_at")
    private Date createdAt;

    public DocumentDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
