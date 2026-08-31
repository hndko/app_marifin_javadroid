package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.remote.dto.TransactionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper between TransactionDto and TransactionEntity.
 */
public final class TransactionMapper {

    private TransactionMapper() {}

    @Nullable
    public static TransactionEntity toEntity(@Nullable TransactionDto dto) {
        if (dto == null) return null;

        TransactionEntity entity = new TransactionEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setAccountId(dto.getAccountId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setDestinationAccountId(dto.getDestinationAccountId());
        entity.setTransferGroupId(dto.getTransferGroupId());
        entity.setType(dto.getType());
        if (dto.getAmount() != null) entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        if (dto.getTransactionDate() != null) entity.setTransactionDate(dto.getTransactionDate());
        entity.setAttachmentUrl(dto.getAttachmentUrl());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static TransactionDto toDto(@Nullable TransactionEntity entity) {
        if (entity == null) return null;

        TransactionDto dto = new TransactionDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAccountId(entity.getAccountId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setDestinationAccountId(entity.getDestinationAccountId());
        dto.setTransferGroupId(entity.getTransferGroupId());
        dto.setType(entity.getType());
        dto.setAmount(entity.getAmount());
        dto.setDescription(entity.getDescription());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setAttachmentUrl(entity.getAttachmentUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<TransactionEntity> toEntityList(@Nullable List<TransactionDto> dtos) {
        List<TransactionEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (TransactionDto dto : dtos) {
                TransactionEntity entity = toEntity(dto);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
