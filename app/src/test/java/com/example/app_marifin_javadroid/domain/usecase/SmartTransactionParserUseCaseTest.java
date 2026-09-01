package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.domain.model.DraftTransaction;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for SmartTransactionParserUseCase.
 */
public class SmartTransactionParserUseCaseTest {

    private SmartTransactionParserUseCase parser;

    @Before
    public void setUp() {
        parser = new SmartTransactionParserUseCase();
    }

    @Test
    public void testParseFoodExpenseWithRb() {
        String text = "Beli makan siang di McD 45rb kemarin";
        DraftTransaction draft = parser.execute(text);

        assertNotNull(draft);
        assertEquals("expense", draft.getType());
        assertEquals(new BigDecimal("45000"), draft.getAmount());
        assertEquals("Makanan & Minuman", draft.getPredictedCategoryName());
        assertEquals("McD", draft.getMerchant());
    }

    @Test
    public void testParseFuelExpenseWithRupiah() {
        String text = "Isi bensin di Pertamina Rp 50.000";
        DraftTransaction draft = parser.execute(text);

        assertNotNull(draft);
        assertEquals("expense", draft.getType());
        assertEquals(new BigDecimal("50000"), draft.getAmount());
        assertEquals("Transportasi", draft.getPredictedCategoryName());
        assertEquals("Pertamina", draft.getMerchant());
    }

    @Test
    public void testParseIncomeWithJt() {
        String text = "Terima gaji bulanan 5.5jt";
        DraftTransaction draft = parser.execute(text);

        assertNotNull(draft);
        assertEquals("income", draft.getType());
        assertEquals(BigDecimal.valueOf(5500000), draft.getAmount());
        assertEquals("Gaji & Pendapatan", draft.getPredictedCategoryName());
    }

    @Test
    public void testParseInvalidOrEmptyText() {
        DraftTransaction draft = parser.execute("Halo selamat pagi apa kabar?");
        assertNull(draft);
    }
}
