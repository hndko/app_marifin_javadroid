package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for GenerateFinancialReportUseCase.
 */
public class GenerateFinancialReportUseCaseTest {

    private GenerateFinancialReportUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GenerateFinancialReportUseCase();
    }

    @Test
    public void testGenerateReportCalculations() {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -10);
        Date startDate = cal.getTime();

        List<TransactionEntity> transactions = new ArrayList<>();

        TransactionEntity tx1 = new TransactionEntity();
        tx1.setType("income");
        tx1.setAmount(new BigDecimal("10000000"));
        tx1.setTransactionDate(now);
        transactions.add(tx1);

        TransactionEntity tx2 = new TransactionEntity();
        tx2.setType("expense");
        tx2.setAmount(new BigDecimal("3000000"));
        tx2.setTransactionDate(now);
        transactions.add(tx2);

        TransactionEntity tx3 = new TransactionEntity();
        tx3.setType("bill");
        tx3.setAmount(new BigDecimal("1000000"));
        tx3.setTransactionDate(now);
        transactions.add(tx3);

        FinancialReportData report = useCase.execute("Bulanan", startDate, now, transactions);

        assertNotNull(report);
        assertEquals(new BigDecimal("10000000"), report.getTotalIncome());
        assertEquals(new BigDecimal("4000000"), report.getTotalExpense());
        assertEquals(new BigDecimal("6000000"), report.getNetCashFlow());
        assertEquals(60, report.getSavingsRate());
        assertEquals(3, report.getTransactionCount());
    }

    @Test
    public void testGenerateReportWithZeroTransactions() {
        Date now = new Date();
        FinancialReportData report = useCase.execute("Bulanan", now, now, new ArrayList<>());

        assertNotNull(report);
        assertEquals(BigDecimal.ZERO, report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getTotalExpense());
        assertEquals(BigDecimal.ZERO, report.getNetCashFlow());
        assertEquals(0, report.getSavingsRate());
    }
}
