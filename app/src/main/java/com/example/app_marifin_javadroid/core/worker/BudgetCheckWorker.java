package com.example.app_marifin_javadroid.core.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.local.model.BudgetWithProgress;
import com.example.app_marifin_javadroid.domain.usecase.CalculateBudgetUtilizationUseCase;

import java.util.List;

/**
 * Background WorkManager Worker that checks budget utilization and triggers alert notifications.
 */
public class BudgetCheckWorker extends Worker {

    public static final String CHANNEL_ID = "marifin_budget_alerts";
    private static final String CHANNEL_NAME = "Peringatan Anggaran MariFin";
    private static final String UNIQUE_WORK_NAME = "marifin_budget_check_work";

    public static void schedulePeriodicCheck(@NonNull Context context) {
        androidx.work.PeriodicWorkRequest checkRequest = new androidx.work.PeriodicWorkRequest.Builder(
                BudgetCheckWorker.class,
                6, java.util.concurrent.TimeUnit.HOURS
        ).build();

        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                checkRequest
        );
    }

    public BudgetCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SecureSessionManager session = SecureSessionManager.getInstance(context);
        String userId = session.getUserId();

        if (userId == null) {
            return Result.success();
        }

        AppDatabase db = AppDatabase.getInstance(context);
        List<BudgetEntity> budgets = db.budgetDao().getBudgetsSync(userId);
        List<TransactionEntity> transactions = db.transactionDao().getTransactionsSync(userId);
        CalculateBudgetUtilizationUseCase useCase = new CalculateBudgetUtilizationUseCase();

        createNotificationChannel(context);

        int notificationId = 1000;
        for (BudgetEntity budget : budgets) {
            List<CategoryEntity> categories = db.budgetDao().getCategoriesForBudget(budget.getId());
            BudgetWithProgress progress = useCase.execute(budget, transactions, categories);

            if (progress.getStatusZone() == BudgetWithProgress.StatusZone.OVER_BUDGET) {
                showNotification(context, notificationId++,
                        "⚠️ Anggaran Terlampaui!",
                        String.format("Budget '%s' telah melebihi batas (Terpakai %s dari %s).",
                                budget.getName(),
                                CurrencyHelper.formatRupiah(progress.getSpentAmount()),
                                CurrencyHelper.formatRupiah(budget.getAmount())));
            } else if (progress.getStatusZone() == BudgetWithProgress.StatusZone.DANGER) {
                showNotification(context, notificationId++,
                        "⚠️ Peringatan Kritis Anggaran",
                        String.format("Pengeluaran untuk '%s' telah mencapai %d%% dari batas.",
                                budget.getName(), progress.getPercentage()));
            }
        }

        return Result.success();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi saat pengeluaran mendekati atau melampaui batas anggaran.");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, int id, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(id, builder.build());
        }
    }
}
