package com.example.app_marifin_javadroid.data.remote.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.remote.dto.AccountDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper between AccountDto and AccountEntity.
 */
public final class AccountMapper {

    private AccountMapper() {}

    @Nullable
    public static AccountEntity toEntity(@Nullable AccountDto dto) {
        if (dto == null) return null;

        AccountEntity entity = new AccountEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setInstitutionName(dto.getInstitutionName());
        entity.setAccountType(dto.getAccountType());
        entity.setAccountNumberMasked(dto.getAccountNumberMasked());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getInitialBalance() != null) entity.setInitialBalance(dto.getInitialBalance());
        if (dto.getCurrentBalance() != null) entity.setCurrentBalance(dto.getCurrentBalance());
        entity.setIconUrl(dto.getIconUrl());
        entity.setActive(dto.isActive());
        if (dto.getCreatedAt() != null) entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    @Nullable
    public static AccountDto toDto(@Nullable AccountEntity entity) {
        if (entity == null) return null;

        AccountDto dto = new AccountDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setInstitutionName(entity.getInstitutionName());
        dto.setAccountType(entity.getAccountType());
        dto.setAccountNumberMasked(entity.getAccountNumberMasked());
        dto.setCurrency(entity.getCurrency());
        dto.setInitialBalance(entity.getInitialBalance());
        dto.setCurrentBalance(entity.getCurrentBalance());
        dto.setIconUrl(entity.getIconUrl());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @NonNull
    public static List<AccountEntity> toEntityList(@Nullable List<AccountDto> dtos) {
        List<AccountEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (AccountDto dto : dtos) {
                AccountEntity entity = toEntity(dto);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
