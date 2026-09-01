package com.example.app_marifin_javadroid.domain.usecase;

import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for CalculateGoalProgressUseCase.
 */
public class CalculateGoalProgressUseCaseTest {

    private CalculateGoalProgressUseCase useCase;

    @Before
    public void setUp() {
        useCase = new CalculateGoalProgressUseCase();
    }

    @Test
    public void testInProgressGoalProgress() {
        GoalEntity goal = new GoalEntity();
        goal.setTargetAmount(new BigDecimal("10000000")); // 10jt
        goal.setCurrentAmount(new BigDecimal("4000000")); // 4jt

        CalculateGoalProgressUseCase.GoalProgressResult result = useCase.execute(goal);

        assertEquals(40, result.getPercentage());
        assertEquals(new BigDecimal("6000000"), result.getRemainingAmount());
        assertFalse(result.isAchieved());
    }

    @Test
    public void testAchievedGoalProgress() {
        GoalEntity goal = new GoalEntity();
        goal.setTargetAmount(new BigDecimal("5000000"));
        goal.setCurrentAmount(new BigDecimal("5000000"));

        CalculateGoalProgressUseCase.GoalProgressResult result = useCase.execute(goal);

        assertEquals(100, result.getPercentage());
        assertEquals(BigDecimal.ZERO, result.getRemainingAmount());
        assertTrue(result.isAchieved());
    }

    @Test
    public void testOverAchievedGoalProgress() {
        GoalEntity goal = new GoalEntity();
        goal.setTargetAmount(new BigDecimal("2000000"));
        goal.setCurrentAmount(new BigDecimal("2500000"));

        CalculateGoalProgressUseCase.GoalProgressResult result = useCase.execute(goal);

        assertEquals(125, result.getPercentage());
        assertEquals(BigDecimal.ZERO, result.getRemainingAmount());
        assertTrue(result.isAchieved());
    }

    @Test
    public void testZeroTargetSafeFallback() {
        GoalEntity goal = new GoalEntity();
        goal.setTargetAmount(BigDecimal.ZERO);
        goal.setCurrentAmount(BigDecimal.ZERO);

        CalculateGoalProgressUseCase.GoalProgressResult result = useCase.execute(goal);

        assertEquals(0, result.getPercentage());
        assertEquals(BigDecimal.ZERO, result.getRemainingAmount());
        assertFalse(result.isAchieved());
    }
}
