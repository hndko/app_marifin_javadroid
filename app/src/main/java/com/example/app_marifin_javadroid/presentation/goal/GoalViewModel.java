package com.example.app_marifin_javadroid.presentation.goal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;
import com.example.app_marifin_javadroid.data.repository.GoalRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * ViewModel managing Financial Goals state, contributions, and accounts.
 */
public class GoalViewModel extends AndroidViewModel {

    private final GoalRepository goalRepository;
    private final AccountRepository accountRepository;

    public GoalViewModel(@NonNull Application application) {
        super(application);
        this.goalRepository = GoalRepository.getInstance(application);
        this.accountRepository = AccountRepository.getInstance(application);
    }

    public LiveData<List<GoalEntity>> getGoals() {
        return goalRepository.getGoalsLiveData();
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accountRepository.getAccountsLiveData();
    }

    public void createGoal(GoalEntity goal, GoalRepository.RepositoryCallback<GoalEntity> callback) {
        goalRepository.createGoal(goal, callback);
    }

    public void contributeToGoal(GoalEntity goal, String sourceAccountId, BigDecimal amount,
                                 GoalRepository.RepositoryCallback<Void> callback) {
        goalRepository.contributeToGoal(goal, sourceAccountId, amount, callback);
    }

    public void deleteGoal(GoalEntity goal, GoalRepository.RepositoryCallback<Void> callback) {
        goalRepository.deleteGoal(goal, callback);
    }

    public void refreshGoals() {
        goalRepository.refreshGoals(null);
        accountRepository.refreshAccounts(null);
    }
}
