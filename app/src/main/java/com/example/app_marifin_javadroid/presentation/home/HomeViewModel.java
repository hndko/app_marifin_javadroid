package com.example.app_marifin_javadroid.presentation.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;
import com.example.app_marifin_javadroid.domain.usecase.CalculateCashFlowUseCase;

import java.util.Date;
import java.util.List;

/**
 * ViewModel for Home Dashboard data aggregation, balances, carousel, and recent transactions.
 */
public class HomeViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SecureSessionManager sessionManager;
    private final CalculateCashFlowUseCase calculateCashFlowUseCase = new CalculateCashFlowUseCase();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = AccountRepository.getInstance(application);
        this.transactionRepository = TransactionRepository.getInstance(application);
        this.sessionManager = SecureSessionManager.getInstance(application);
    }

    public HomeViewModel(@NonNull Application application,
                         @NonNull AccountRepository accountRepo,
                         @NonNull TransactionRepository txRepo,
                         @NonNull SecureSessionManager sessionManager) {
        super(application);
        this.accountRepository = accountRepo;
        this.transactionRepository = txRepo;
        this.sessionManager = sessionManager;
    }

    public static String formatGreeting(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return "Halo, " + fullName.trim().split(" ")[0];
        }
        return "Halo, Teman!";
    }

    public String getUserGreeting() {
        return formatGreeting(sessionManager.getFullName());
    }

    public LiveData<Double> getTotalBalance() {
        return accountRepository.getTotalBalanceLiveData();
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accountRepository.getAccountsLiveData();
    }

    public LiveData<List<TransactionEntity>> getRecentTransactions() {
        return transactionRepository.getRecentTransactionsLiveData(5);
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactionsLiveData();
    }

    public CalculateCashFlowUseCase.CashFlowResult calculateCashFlow(List<TransactionEntity> transactions) {
        return calculateCashFlowUseCase.execute(transactions);
    }

    public void refreshData() {
        accountRepository.refreshAccounts(null);
        transactionRepository.refreshTransactions(null);
    }
}
