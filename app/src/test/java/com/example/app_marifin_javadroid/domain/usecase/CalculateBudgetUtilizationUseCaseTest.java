package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.local.model.BudgetWithProgress;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests for CalculateBudgetUtilizationUseCase testing all 4 alert status thresholds and precision.
 */
public class CalculateBudgetUtilizationUseCaseTest {

    private CalculateBudgetUtilizationUseCase useCase;
    private BudgetEntity budget;
    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        useCase = new CalculateBudgetUtilizationUseCase();

        Calendar startCal = Calendar.getInstance();
        startCal.set(2026, Calendar.AUGUST, 1, 0, 0, 0);
        startDate = startCal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.set(2026, Calendar.AUGUST, 31, 23, 59, 59);
        endDate = endCal.getTime();

        budget = new BudgetEntity();
        budget.setId("b-1");
        budget.setName("Budget Makanan");
        budget.setAmount(new BigDecimal("1000000")); // 1 Juta
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
    }

    @Test
    public void testSafeZoneUnder70Percent() {
        List<TransactionEntity> txs = new ArrayList<>();
        TransactionEntity tx = new TransactionEntity();
        tx.setType("expense");
        tx.setAmount(new BigDecimal("500000")); // 50%
        tx.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(tx);

        BudgetWithProgress result = useCase.execute(budget, txs, null);

        assertEquals(new BigDecimal("500000"), result.getSpentAmount());
        assertEquals(new BigDecimal("500000"), result.getRemainingAmount());
        assertEquals(50, result.getPercentage());
        assertEquals(BudgetWithProgress.StatusZone.SAFE, result.getStatusZone());
    }

    @Test
    public void testWarningZone70To89Percent() {
        List<TransactionEntity> txs = new ArrayList<>();
        TransactionEntity tx = new TransactionEntity();
        tx.setType("expense");
        tx.setAmount(new BigDecimal("750000")); // 75%
        tx.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(tx);

        BudgetWithProgress result = useCase.execute(budget, txs, null);

        assertEquals(75, result.getPercentage());
        assertEquals(BudgetWithProgress.StatusZone.WARNING, result.getStatusZone());
    }

    @Test
    public void testDangerZone90To99Percent() {
        List<TransactionEntity> txs = new ArrayList<>();
        TransactionEntity tx = new TransactionEntity();
        tx.setType("expense");
        tx.setAmount(new BigDecimal("950000")); // 95%
        tx.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(tx);

        BudgetWithProgress result = useCase.execute(budget, txs, null);

        assertEquals(95, result.getPercentage());
        assertEquals(BudgetWithProgress.StatusZone.DANGER, result.getStatusZone());
    }

    @Test
    public void testOverBudgetZoneAtOrAbove100Percent() {
        List<TransactionEntity> txs = new ArrayList<>();
        TransactionEntity tx = new TransactionEntity();
        tx.setType("expense");
        tx.setAmount(new BigDecimal("1200000")); // 120%
        tx.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(tx);

        BudgetWithProgress result = useCase.execute(budget, txs, null);

        assertEquals(120, result.getPercentage());
        assertEquals(new BigDecimal("-200000"), result.getRemainingAmount());
        assertEquals(BudgetWithProgress.StatusZone.OVER_BUDGET, result.getStatusZone());
    }

    @Test
    public void testCategoryFiltering() {
        CategoryEntity catFood = new CategoryEntity();
        catFood.setId("cat-food");
        catFood.setName("Makanan");

        List<CategoryEntity> budgetCats = new ArrayList<>();
        budgetCats.add(catFood);

        List<TransactionEntity> txs = new ArrayList<>();
        TransactionEntity txFood = new TransactionEntity();
        txFood.setType("expense");
        txFood.setCategoryId("cat-food");
        txFood.setAmount(new BigDecimal("300000"));
        txFood.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(txFood);

        TransactionEntity txTransport = new TransactionEntity();
        txTransport.setType("expense");
        txTransport.setCategoryId("cat-transport");
        txTransport.setAmount(new BigDecimal("200000"));
        txTransport.setTransactionDate(new Date(startDate.getTime() + 100000));
        txs.add(txTransport);

        BudgetWithProgress result = useCase.execute(budget, txs, budgetCats);

        // Only food tx should be counted
        assertEquals(new BigDecimal("300000"), result.getSpentAmount());
        assertEquals(30, result.getPercentage());
    }
}
