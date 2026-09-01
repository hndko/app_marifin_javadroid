package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.data.remote.dto.GoalDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for GoalMapper DTO <-> Entity.
 */
public class GoalMapperTest {

    @Test
    public void testGoalDtoToEntity() {
        GoalDto dto = new GoalDto();
        dto.setId("goal-1");
        dto.setUserId("user-1");
        dto.setName("Dana Darurat");
        dto.setTargetAmount(new BigDecimal("20000000"));
        dto.setCurrentAmount(new BigDecimal("5000000"));
        dto.setStatus("in_progress");

        GoalEntity entity = GoalMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("goal-1", entity.getId());
        assertEquals("user-1", entity.getUserId());
        assertEquals("Dana Darurat", entity.getName());
        assertEquals(new BigDecimal("20000000"), entity.getTargetAmount());
        assertEquals(new BigDecimal("5000000"), entity.getCurrentAmount());
    }

    @Test
    public void testGoalEntityToDto() {
        GoalEntity entity = new GoalEntity();
        entity.setId("goal-2");
        entity.setUserId("user-2");
        entity.setName("Beli Laptop");
        entity.setTargetAmount(new BigDecimal("15000000"));
        entity.setCurrentAmount(new BigDecimal("15000000"));
        entity.setStatus("achieved");

        GoalDto dto = GoalMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("goal-2", dto.getId());
        assertEquals("user-2", dto.getUserId());
        assertEquals("Beli Laptop", dto.getName());
        assertEquals(new BigDecimal("15000000"), dto.getTargetAmount());
        assertEquals("achieved", dto.getStatus());
    }
}
