package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.remote.dto.AccountDto;
import com.example.app_marifin_javadroid.data.remote.dto.CategoryDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for AccountMapper and CategoryMapper.
 */
public class MapperTest {

    @Test
    public void testAccountMapper() {
        AccountDto dto = new AccountDto(
                "acc-123",
                "user-456",
                "Blu by BCA Digital",
                "BCA Digital",
                "Bank",
                "1234",
                new BigDecimal("1500000"),
                new BigDecimal("1500000")
        );

        AccountEntity entity = AccountMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("acc-123", entity.getId());
        assertEquals("user-456", entity.getUserId());
        assertEquals("Blu by BCA Digital", entity.getName());
        assertEquals("BCA Digital", entity.getInstitutionName());
        assertEquals(new BigDecimal("1500000"), entity.getInitialBalance());

        AccountDto mappedBack = AccountMapper.toDto(entity);
        assertNotNull(mappedBack);
        assertEquals("Blu by BCA Digital", mappedBack.getName());

        assertNull(AccountMapper.toEntity(null));
        assertNull(AccountMapper.toDto(null));
    }

    @Test
    public void testAccountListMapper() {
        List<AccountDto> dtos = new ArrayList<>();
        dtos.add(new AccountDto("1", "u1", "A1", "B1", "Bank", null, BigDecimal.ZERO, BigDecimal.ZERO));
        dtos.add(new AccountDto("2", "u1", "A2", "B2", "E-Wallet", null, BigDecimal.ZERO, BigDecimal.ZERO));

        List<AccountEntity> entities = AccountMapper.toEntityList(dtos);
        assertEquals(2, entities.size());
        assertEquals("A1", entities.get(0).getName());
        assertEquals("A2", entities.get(1).getName());
    }

    @Test
    public void testCategoryMapper() {
        CategoryDto dto = new CategoryDto(
                "cat-1",
                "user-1",
                "Makanan & Minuman",
                "ic_food",
                "#EF4444",
                "expense",
                true,
                true
        );

        CategoryEntity entity = CategoryMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("cat-1", entity.getId());
        assertEquals("Makanan & Minuman", entity.getName());
        assertEquals("expense", entity.getType());
        assertTrue(entity.isDefault());
        assertTrue(entity.isFavorite());

        CategoryDto mappedBack = CategoryMapper.toDto(entity);
        assertNotNull(mappedBack);
        assertEquals("Makanan & Minuman", mappedBack.getName());

        assertNull(CategoryMapper.toEntity(null));
        assertNull(CategoryMapper.toDto(null));
    }
}
