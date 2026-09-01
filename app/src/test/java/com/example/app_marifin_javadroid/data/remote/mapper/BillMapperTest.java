package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.data.remote.dto.BillDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for BillMapper DTO <-> Entity.
 */
public class BillMapperTest {

    @Test
    public void testBillDtoToEntity() {
        BillDto dto = new BillDto();
        dto.setId("bill-1");
        dto.setUserId("user-1");
        dto.setName("Listrik PLN");
        dto.setAmount(new BigDecimal("250000"));
        dto.setDueDate(new Date());
        dto.setStatus("upcoming");

        BillEntity entity = BillMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("bill-1", entity.getId());
        assertEquals("user-1", entity.getUserId());
        assertEquals("Listrik PLN", entity.getName());
        assertEquals(new BigDecimal("250000"), entity.getAmount());
        assertEquals("upcoming", entity.getStatus());
    }

    @Test
    public void testBillEntityToDto() {
        BillEntity entity = new BillEntity();
        entity.setId("bill-2");
        entity.setUserId("user-2");
        entity.setName("Indihome");
        entity.setAmount(new BigDecimal("350000"));
        entity.setStatus("paid");

        BillDto dto = BillMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("bill-2", dto.getId());
        assertEquals("user-2", dto.getUserId());
        assertEquals("Indihome", dto.getName());
        assertEquals(new BigDecimal("350000"), dto.getAmount());
        assertEquals("paid", dto.getStatus());
    }
}
