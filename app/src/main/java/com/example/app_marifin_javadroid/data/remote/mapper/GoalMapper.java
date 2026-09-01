package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.data.remote.dto.GoalDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bidirectional mapper for Goal DTO <-> Entity.
 */
public final class GoalMapper {

    private GoalMapper() {}

    @Nullable
    public static GoalEntity toEntity(@Nullable GoalDto dto) {
        if (dto == null) return null;

        GoalEntity entity = new GoalEntity();
        if (dto.getId() != null) entity.setId(dto.getId());
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : "");
        entity.setName(dto.getName() != null ? dto.getName() : "Target Tabungan");
        entity.setTargetAmount(dto.getTargetAmount());
        entity.setCurrentAmount(dto.getCurrentAmount());
        entity.setDeadline(dto.getDeadline());
        entity.setAccountId(dto.getAccountId());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "in_progress");
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static GoalDto toDto(@Nullable GoalEntity entity) {
        if (entity == null) return null;

        GoalDto dto = new GoalDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setTargetAmount(entity.getTargetAmount());
        dto.setCurrentAmount(entity.getCurrentAmount());
        dto.setDeadline(entity.getDeadline());
        dto.setAccountId(entity.getAccountId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<GoalEntity> toEntityList(@Nullable List<GoalDto> dtoList) {
        List<GoalEntity> result = new ArrayList<>();
        if (dtoList != null) {
            for (GoalDto dto : dtoList) {
                GoalEntity entity = toEntity(dto);
                if (entity != null) result.add(entity);
            }
        }
        return result;
    }
}
