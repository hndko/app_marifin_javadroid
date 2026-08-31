package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.remote.dto.TransactionDto;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for TransactionMapper.
 */
public class TransactionMapperTest {

    @Test
    public void testTransactionMapper() {
        Date now = new Date();
        TransactionDto dto = new TransactionDto(
                "tx-123",
                "user-1",
                "acc-1",
                "cat-1",
                "expense",
                new BigDecimal("45000"),
                "Makan Siang",
                now
        );

        TransactionEntity entity = TransactionMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("tx-123", entity.getId());
        assertEquals("user-1", entity.getUserId());
        assertEquals("acc-1", entity.getAccountId());
        assertEquals("expense", entity.getType());
        assertEquals(new BigDecimal("45000"), entity.getAmount());
        assertEquals("Makan Siang", entity.getDescription());
        assertEquals(now, entity.getTransactionDate());

        TransactionDto mappedBack = TransactionMapper.toDto(entity);
        assertNotNull(mappedBack);
        assertEquals("Makan Siang", mappedBack.getDescription());

        assertNull(TransactionMapper.toEntity(null));
        assertNull(TransactionMapper.toDto(null));
    }

    @Test
    public void testTransactionListMapper() {
        List<TransactionDto> dtos = new ArrayList<>();
        dtos.add(new TransactionDto("tx-1", "u1", "a1", "c1", "income", new BigDecimal("1000000"), "Gaji", new Date()));
        dtos.add(new TransactionDto("tx-2", "u1", "a1", "c2", "expense", new BigDecimal("50000"), "Bensin", new Date()));

        List<TransactionEntity> entities = TransactionMapper.toEntityList(dtos);
        assertEquals(2, entities.size());
        assertEquals("Gaji", entities.get(0).getDescription());
        assertEquals("Bensin", entities.get(1).getDescription());
    }
}
