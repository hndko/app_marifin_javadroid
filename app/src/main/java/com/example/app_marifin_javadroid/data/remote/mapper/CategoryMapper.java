package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.remote.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper between CategoryDto and CategoryEntity.
 */
public final class CategoryMapper {

    private CategoryMapper() {}

    @Nullable
    public static CategoryEntity toEntity(@Nullable CategoryDto dto) {
        if (dto == null) return null;

        CategoryEntity entity = new CategoryEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setParentId(dto.getParentId());
        entity.setName(dto.getName());
        if (dto.getIcon() != null) entity.setIcon(dto.getIcon());
        if (dto.getColor() != null) entity.setColor(dto.getColor());
        if (dto.getType() != null) entity.setType(dto.getType());
        entity.setDefault(dto.isDefault());
        entity.setFavorite(dto.isFavorite());
        entity.setActive(dto.isActive());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static CategoryDto toDto(@Nullable CategoryEntity entity) {
        if (entity == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setParentId(entity.getParentId());
        dto.setName(entity.getName());
        dto.setIcon(entity.getIcon());
        dto.setColor(entity.getColor());
        dto.setType(entity.getType());
        dto.setDefault(entity.isDefault());
        dto.setFavorite(entity.isFavorite());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<CategoryEntity> toEntityList(@Nullable List<CategoryDto> dtos) {
        List<CategoryEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (CategoryDto dto : dtos) {
                CategoryEntity entity = toEntity(dto);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
