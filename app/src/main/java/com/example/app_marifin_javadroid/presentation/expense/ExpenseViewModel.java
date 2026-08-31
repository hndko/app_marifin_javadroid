package com.example.app_marifin_javadroid.presentation.expense;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.data.local.model.CategoryExpenseAggregate;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;

import java.util.Date;
import java.util.List;

/**
 * ViewModel for Expense Analytics Dashboard (Charts and Category Breakdown).
 */
public class ExpenseViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        this.transactionRepository = TransactionRepository.getInstance(application);
    }

    public LiveData<List<CategoryExpenseAggregate>> getCategoryExpenseBreakdown(Date startDate, Date endDate) {
        return transactionRepository.getCategoryExpenseBreakdownLiveData(startDate, endDate);
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactionsLiveData();
    }

    public void refreshData() {
        transactionRepository.refreshTransactions(null);
    }
}
