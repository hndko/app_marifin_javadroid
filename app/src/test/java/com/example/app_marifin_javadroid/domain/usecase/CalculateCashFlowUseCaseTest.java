package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for CalculateCashFlowUseCase financial domain rules.
 */
public class CalculateCashFlowUseCaseTest {

    private CalculateCashFlowUseCase useCase;

    @Before
    public void setUp() {
        useCase = new CalculateCashFlowUseCase();
    }

    @Test
    public void testEmptyOrNullTransactions() {
        CalculateCashFlowUseCase.CashFlowResult resultNull = useCase.execute(null);
        assertNotNull(resultNull);
        assertEquals(BigDecimal.ZERO, resultNull.getTotalIncome());
        assertEquals(BigDecimal.ZERO, resultNull.getTotalExpense());
        assertEquals(BigDecimal.ZERO, resultNull.getNetCashFlow());
        assertEquals(BigDecimal.ZERO, resultNull.getSavingsRate());

        CalculateCashFlowUseCase.CashFlowResult resultEmpty = useCase.execute(new ArrayList<>());
        assertEquals(BigDecimal.ZERO, resultEmpty.getTotalIncome());
        assertEquals(BigDecimal.ZERO, resultEmpty.getTotalExpense());
        assertEquals(BigDecimal.ZERO, resultEmpty.getNetCashFlow());
    }

    @Test
    public void testIncomeAndExpenseCalculations() {
        List<TransactionEntity> list = new ArrayList<>();

        // Income 1: 5,000,000
        TransactionEntity in1 = new TransactionEntity();
        in1.setType("income");
        in1.setAmount(new BigDecimal("5000000"));
        list.add(in1);

        // Income 2: 2,000,000
        TransactionEntity in2 = new TransactionEntity();
        in2.setType("income");
        in2.setAmount(new BigDecimal("2000000"));
        list.add(in2);

        // Expense 1: 1,500,000
        TransactionEntity ex1 = new TransactionEntity();
        ex1.setType("expense");
        ex1.setAmount(new BigDecimal("1500000"));
        list.add(ex1);

        // Expense 2: 500,000
        TransactionEntity ex2 = new TransactionEntity();
        ex2.setType("expense");
        ex2.setAmount(new BigDecimal("500000"));
        list.add(ex2);

        CalculateCashFlowUseCase.CashFlowResult result = useCase.execute(list);

        // Total Income: 7,000,000
        assertEquals(new BigDecimal("7000000"), result.getTotalIncome());
        // Total Expense: 2,000,000
        assertEquals(new BigDecimal("2000000"), result.getTotalExpense());
        // Net Cash Flow: 5,000,000
        assertEquals(new BigDecimal("5000000"), result.getNetCashFlow());
        // Savings Rate: (5,000,000 / 7,000,000) * 100 = 71.43%
        assertEquals(new BigDecimal("71.43"), result.getSavingsRate());
    }

    @Test
    public void testTransferIsExcludedFromCashFlow() {
        List<TransactionEntity> list = new ArrayList<>();

        // Income: 10,000,000
        TransactionEntity income = new TransactionEntity();
        income.setType("income");
        income.setAmount(new BigDecimal("10000000"));
        list.add(income);

        // Expense: 3,000,000
        TransactionEntity expense = new TransactionEntity();
        expense.setType("expense");
        expense.setAmount(new BigDecimal("3000000"));
        list.add(expense);

        // Transfer: 4,000,000 (Should be ignored in cash flow)
        TransactionEntity transfer = new TransactionEntity();
        transfer.setType("transfer");
        transfer.setAmount(new BigDecimal("4000000"));
        list.add(transfer);

        CalculateCashFlowUseCase.CashFlowResult result = useCase.execute(list);

        assertEquals(new BigDecimal("10000000"), result.getTotalIncome());
        assertEquals(new BigDecimal("3000000"), result.getTotalExpense());
        assertEquals(new BigDecimal("7000000"), result.getNetCashFlow());
        assertEquals(new BigDecimal("70.00"), result.getSavingsRate());
    }
}
