package com.example.app_marifin_javadroid.presentation.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.local.model.BudgetWithProgress;
import com.example.app_marifin_javadroid.data.repository.BudgetRepository;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;
import com.example.app_marifin_javadroid.domain.usecase.CalculateBudgetUtilizationUseCase;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel managing Budget list state, progress calculations, and CRUD operations.
 */
public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CalculateBudgetUtilizationUseCase calculateBudgetUtilizationUseCase = new CalculateBudgetUtilizationUseCase();

    private final MediatorLiveData<List<BudgetWithProgress>> budgetsWithProgress = new MediatorLiveData<>();
    private List<BudgetEntity> currentBudgets = new ArrayList<>();
    private List<TransactionEntity> currentTransactions = new ArrayList<>();

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        this.budgetRepository = BudgetRepository.getInstance(application);
        this.transactionRepository = TransactionRepository.getInstance(application);

        LiveData<List<BudgetEntity>> budgetsSource = budgetRepository.getBudgetsLiveData();
        LiveData<List<TransactionEntity>> txsSource = transactionRepository.getTransactionsLiveData();

        budgetsWithProgress.addSource(budgetsSource, budgets -> {
            this.currentBudgets = budgets != null ? budgets : new ArrayList<>();
            recalculateProgress();
        });

        budgetsWithProgress.addSource(txsSource, txs -> {
            this.currentTransactions = txs != null ? txs : new ArrayList<>();
            recalculateProgress();
        });
    }

    public LiveData<List<BudgetWithProgress>> getBudgetsWithProgress() {
        return budgetsWithProgress;
    }

    private void recalculateProgress() {
        List<BudgetWithProgress> result = new ArrayList<>();
        for (BudgetEntity b : currentBudgets) {
            List<CategoryEntity> categories = budgetRepository.getCategoriesForBudget(b.getId());
            BudgetWithProgress progress = calculateBudgetUtilizationUseCase.execute(b, currentTransactions, categories);
            result.add(progress);
        }
        budgetsWithProgress.setValue(result);
    }

    public void createBudget(BudgetEntity budget, List<String> categoryIds, BudgetRepository.RepositoryCallback<BudgetEntity> callback) {
        budgetRepository.createBudget(budget, categoryIds, callback);
    }

    public void deleteBudget(BudgetEntity budget, BudgetRepository.RepositoryCallback<Void> callback) {
        budgetRepository.deleteBudget(budget, callback);
    }

    public void refreshBudgets() {
        budgetRepository.refreshBudgets(null);
        transactionRepository.refreshTransactions(null);
    }
}
