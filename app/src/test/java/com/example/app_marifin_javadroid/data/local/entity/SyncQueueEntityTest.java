package com.example.app_marifin_javadroid.data.local.entity;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for SyncQueueEntity.
 */
public class SyncQueueEntityTest {

    @Test
    public void testSyncQueueEntityCreation() {
        SyncQueueEntity item = new SyncQueueEntity("transaction", "tx-123", "INSERT", "{\"amount\":50000}");

        assertNotNull(item);
        assertEquals("transaction", item.getEntityType());
        assertEquals("tx-123", item.getEntityId());
        assertEquals("INSERT", item.getOperation());
        assertEquals("{\"amount\":50000}", item.getPayloadJson());
        assertEquals(0, item.getRetryCount());
        assertNotNull(item.getCreatedAt());
    }

    @Test
    public void testSyncQueueEntityRetryIncrement() {
        SyncQueueEntity item = new SyncQueueEntity();
        item.setEntityType("budget");
        item.setEntityId("b-1");
        item.setOperation("DELETE");
        item.setPayloadJson("{}");
        item.setRetryCount(3);

        assertEquals("budget", item.getEntityType());
        assertEquals("b-1", item.getEntityId());
        assertEquals("DELETE", item.getOperation());
        assertEquals(3, item.getRetryCount());
    }
}
