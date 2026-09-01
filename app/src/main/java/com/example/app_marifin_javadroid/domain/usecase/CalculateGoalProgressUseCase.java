package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;

import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Domain Use Case for calculating goal progress percentage, remaining balance, and days left.
 */
public class CalculateGoalProgressUseCase {

    public static class GoalProgressResult {
        private final BigDecimal targetAmount;
        private final BigDecimal currentAmount;
        private final BigDecimal remainingAmount;
        private final int percentage;
        private final boolean isAchieved;
        private final long daysLeft;

        public GoalProgressResult(BigDecimal targetAmount, BigDecimal currentAmount,
                                  BigDecimal remainingAmount, int percentage,
                                  boolean isAchieved, long daysLeft) {
            this.targetAmount = targetAmount;
            this.currentAmount = currentAmount;
            this.remainingAmount = remainingAmount;
            this.percentage = percentage;
            this.isAchieved = isAchieved;
            this.daysLeft = daysLeft;
        }

        public BigDecimal getTargetAmount() { return targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public int getPercentage() { return percentage; }
        public boolean isAchieved() { return isAchieved; }
        public long getDaysLeft() { return daysLeft; }
    }

    @NonNull
    public GoalProgressResult execute(@NonNull GoalEntity goal) {
        BigDecimal target = goal.getTargetAmount() != null ? goal.getTargetAmount() : BigDecimal.ZERO;
        BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;

        BigDecimal remaining = target.subtract(current);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        int percentage = 0;
        if (target.compareTo(BigDecimal.ZERO) > 0) {
            percentage = current.multiply(BigDecimal.valueOf(100))
                    .divide(target, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        boolean achieved = current.compareTo(target) >= 0 && target.compareTo(BigDecimal.ZERO) > 0;

        long daysLeft = -1;
        if (goal.getDeadline() != null) {
            long diffMillis = goal.getDeadline().getTime() - new Date().getTime();
            daysLeft = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
            if (daysLeft < 0) daysLeft = 0;
        }

        return new GoalProgressResult(target, current, remaining, percentage, achieved, daysLeft);
    }
}
