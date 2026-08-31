package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.remote.dto.BudgetDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bidirectional mapper for Budget DTO <-> Entity.
 */
public final class BudgetMapper {

    private BudgetMapper() {}

    @Nullable
    public static BudgetEntity toEntity(@Nullable BudgetDto dto) {
        if (dto == null) return null;

        BudgetEntity entity = new BudgetEntity();
        if (dto.getId() != null) entity.setId(dto.getId());
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : "");
        entity.setName(dto.getName() != null ? dto.getName() : "Budget");
        entity.setAmount(dto.getAmountLimit());
        entity.setPeriodType(dto.getPeriod() != null ? dto.getPeriod() : "monthly");
        entity.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : new Date());
        entity.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : new Date());
        entity.setAlertThreshold(dto.getAlertThreshold() > 0 ? dto.getAlertThreshold() : 80);
        entity.setActive(dto.isActive());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static BudgetDto toDto(@Nullable BudgetEntity entity) {
        if (entity == null) return null;

        BudgetDto dto = new BudgetDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setAmountLimit(entity.getAmount());
        dto.setPeriod(entity.getPeriodType());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setAlertThreshold(entity.getAlertThreshold());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<BudgetEntity> toEntityList(@Nullable List<BudgetDto> dtoList) {
        List<BudgetEntity> result = new ArrayList<>();
        if (dtoList != null) {
            for (BudgetDto dto : dtoList) {
                BudgetEntity entity = toEntity(dto);
                if (entity != null) result.add(entity);
            }
        }
        return result;
    }
}
