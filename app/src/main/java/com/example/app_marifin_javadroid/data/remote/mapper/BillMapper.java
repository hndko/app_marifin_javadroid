package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.data.remote.dto.BillDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bidirectional mapper for Bill DTO <-> Entity.
 */
public final class BillMapper {

    private BillMapper() {}

    @Nullable
    public static BillEntity toEntity(@Nullable BillDto dto) {
        if (dto == null) return null;

        BillEntity entity = new BillEntity();
        if (dto.getId() != null) entity.setId(dto.getId());
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : "");
        entity.setName(dto.getName() != null ? dto.getName() : "Tagihan");
        entity.setAmount(dto.getAmount());
        entity.setCategoryId(dto.getCategoryId());
        entity.setAccountId(dto.getAccountId());
        entity.setDueDate(dto.getDueDate() != null ? dto.getDueDate() : new Date());
        entity.setRecurrence(dto.getRecurrence() != null ? dto.getRecurrence() : "monthly");
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "upcoming");
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static BillDto toDto(@Nullable BillEntity entity) {
        if (entity == null) return null;

        BillDto dto = new BillDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setAmount(entity.getAmount());
        dto.setCategoryId(entity.getCategoryId());
        dto.setAccountId(entity.getAccountId());
        dto.setDueDate(entity.getDueDate());
        dto.setRecurrence(entity.getRecurrence());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<BillEntity> toEntityList(@Nullable List<BillDto> dtoList) {
        List<BillEntity> result = new ArrayList<>();
        if (dtoList != null) {
            for (BillDto dto : dtoList) {
                BillEntity entity = toEntity(dto);
                if (entity != null) result.add(entity);
            }
        }
        return result;
    }
}
