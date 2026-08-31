package com.example.app_marifin_javadroid.presentation.home;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.domain.usecase.CalculateCashFlowUseCase;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests for HomeViewModel logic and pure functions.
 */
public class HomeViewModelTest {

    @Test
    public void testFormatGreetingWithName() {
        assertEquals("Halo, Ahmad", HomeViewModel.formatGreeting("Ahmad Dani"));
        assertEquals("Halo, Siti", HomeViewModel.formatGreeting("Siti"));
    }

    @Test
    public void testFormatGreetingEmptyFallback() {
        assertEquals("Halo, Teman!", HomeViewModel.formatGreeting(""));
        assertEquals("Halo, Teman!", HomeViewModel.formatGreeting("   "));
        assertEquals("Halo, Teman!", HomeViewModel.formatGreeting(null));
    }

    @Test
    public void testCalculateCashFlow() {
        CalculateCashFlowUseCase useCase = new CalculateCashFlowUseCase();

        List<TransactionEntity> list = new ArrayList<>();
        TransactionEntity in = new TransactionEntity();
        in.setType("income");
        in.setAmount(new BigDecimal("500000"));
        list.add(in);

        TransactionEntity ex = new TransactionEntity();
        ex.setType("expense");
        ex.setAmount(new BigDecimal("200000"));
        list.add(ex);

        CalculateCashFlowUseCase.CashFlowResult result = useCase.execute(list);
        assertEquals(new BigDecimal("500000"), result.getTotalIncome());
        assertEquals(new BigDecimal("200000"), result.getTotalExpense());
        assertEquals(new BigDecimal("300000"), result.getNetCashFlow());
    }
}
