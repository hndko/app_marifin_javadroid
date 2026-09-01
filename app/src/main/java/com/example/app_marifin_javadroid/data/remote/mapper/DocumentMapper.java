package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.remote.dto.DocumentDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bidirectional mapper for Document DTO <-> Entity.
 */
public final class DocumentMapper {

    private DocumentMapper() {}

    @Nullable
    public static DocumentEntity toEntity(@Nullable DocumentDto dto) {
        if (dto == null) return null;

        DocumentEntity entity = new DocumentEntity();
        if (dto.getId() != null) entity.setId(dto.getId());
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : "");
        entity.setTransactionId(dto.getTransactionId());
        entity.setStoragePath(dto.getStoragePath() != null ? dto.getStoragePath() : "");
        entity.setOriginalName(dto.getOriginalName() != null ? dto.getOriginalName() : "file");
        entity.setMimeType(dto.getMimeType() != null ? dto.getMimeType() : "application/octet-stream");
        entity.setFileSize(dto.getFileSize());
        entity.setDocumentType(dto.getDocumentType() != null ? dto.getDocumentType() : "receipt");
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }

    @Nullable
    public static DocumentDto toDto(@Nullable DocumentEntity entity) {
        if (entity == null) return null;

        DocumentDto dto = new DocumentDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTransactionId(entity.getTransactionId());
        dto.setStoragePath(entity.getStoragePath());
        dto.setOriginalName(entity.getOriginalName());
        dto.setMimeType(entity.getMimeType());
        dto.setFileSize(entity.getFileSize());
        dto.setDocumentType(entity.getDocumentType());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    @NonNull
    public static List<DocumentEntity> toEntityList(@Nullable List<DocumentDto> dtoList) {
        List<DocumentEntity> result = new ArrayList<>();
        if (dtoList != null) {
            for (DocumentDto dto : dtoList) {
                DocumentEntity entity = toEntity(dto);
                if (entity != null) result.add(entity);
            }
        }
        return result;
    }
}
