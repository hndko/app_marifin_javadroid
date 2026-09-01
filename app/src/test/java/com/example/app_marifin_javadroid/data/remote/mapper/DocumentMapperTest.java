package com.example.app_marifin_javadroid.data.remote.mapper;

import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.remote.dto.DocumentDto;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for DocumentMapper DTO <-> Entity.
 */
public class DocumentMapperTest {

    @Test
    public void testDocumentDtoToEntity() {
        DocumentDto dto = new DocumentDto();
        dto.setId("doc-1");
        dto.setUserId("user-1");
        dto.setOriginalName("struk.pdf");
        dto.setStoragePath("/vault/struk.pdf");
        dto.setMimeType("application/pdf");
        dto.setFileSize(204800);
        dto.setDocumentType("receipt");
        dto.setCreatedAt(new Date());

        DocumentEntity entity = DocumentMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("doc-1", entity.getId());
        assertEquals("user-1", entity.getUserId());
        assertEquals("struk.pdf", entity.getOriginalName());
        assertEquals("/vault/struk.pdf", entity.getStoragePath());
        assertEquals("application/pdf", entity.getMimeType());
        assertEquals(204800, entity.getFileSize());
        assertEquals("receipt", entity.getDocumentType());
    }

    @Test
    public void testDocumentEntityToDto() {
        DocumentEntity entity = new DocumentEntity();
        entity.setId("doc-2");
        entity.setUserId("user-2");
        entity.setOriginalName("invoice.png");
        entity.setStoragePath("/vault/invoice.png");
        entity.setMimeType("image/png");
        entity.setFileSize(512000);
        entity.setDocumentType("invoice");

        DocumentDto dto = DocumentMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("doc-2", dto.getId());
        assertEquals("user-2", dto.getUserId());
        assertEquals("invoice.png", dto.getOriginalName());
        assertEquals("/vault/invoice.png", dto.getStoragePath());
        assertEquals("image/png", dto.getMimeType());
        assertEquals(512000, dto.getFileSize());
        assertEquals("invoice", dto.getDocumentType());
    }
}
