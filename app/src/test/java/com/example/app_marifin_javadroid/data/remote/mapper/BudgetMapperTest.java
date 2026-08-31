package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.remote.dto.BudgetDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for BudgetMapper DTO <-> Entity.
 */
public class BudgetMapperTest {

    @Test
    public void testDtoToEntity() {
        BudgetDto dto = new BudgetDto();
        dto.setId("b-123");
        dto.setUserId("u-456");
        dto.setName("Budget Hiburan");
        dto.setAmountLimit(new BigDecimal("750000"));
        dto.setPeriod("monthly");
        dto.setStartDate(new Date());
        dto.setEndDate(new Date());
        dto.setAlertThreshold(85);
        dto.setActive(true);

        BudgetEntity entity = BudgetMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("b-123", entity.getId());
        assertEquals("u-456", entity.getUserId());
        assertEquals("Budget Hiburan", entity.getName());
        assertEquals(new BigDecimal("750000"), entity.getAmount());
        assertEquals("monthly", entity.getPeriodType());
        assertEquals(85, entity.getAlertThreshold());
    }

    @Test
    public void testEntityToDto() {
        BudgetEntity entity = new BudgetEntity();
        entity.setId("b-999");
        entity.setUserId("u-999");
        entity.setName("Budget Kuliner");
        entity.setAmount(new BigDecimal("1500000"));
        entity.setPeriodType("monthly");
        entity.setStartDate(new Date());
        entity.setEndDate(new Date());
        entity.setAlertThreshold(80);
        entity.setActive(true);

        BudgetDto dto = BudgetMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("b-999", dto.getId());
        assertEquals("u-999", dto.getUserId());
        assertEquals("Budget Kuliner", dto.getName());
        assertEquals(new BigDecimal("1500000"), dto.getAmountLimit());
    }
}
