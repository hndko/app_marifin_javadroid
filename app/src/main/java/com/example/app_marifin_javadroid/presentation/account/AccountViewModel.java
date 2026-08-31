package com.example.app_marifin_javadroid.presentation.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing Financial Accounts UI state and CRUD operations.
 */
public class AccountViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;

    private final MutableLiveData<Resource<AccountEntity>> _operationResult = new MutableLiveData<>();
    public final LiveData<Resource<AccountEntity>> operationResult = _operationResult;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = AccountRepository.getInstance(application);
    }

    public AccountViewModel(@NonNull Application application, @NonNull AccountRepository repository) {
        super(application);
        this.accountRepository = repository;
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accountRepository.getAccountsLiveData();
    }

    public LiveData<Double> getTotalBalance() {
        return accountRepository.getTotalBalanceLiveData();
    }

    public void refreshAccounts() {
        accountRepository.refreshAccounts(null);
    }

    public void saveAccount(String id, String name, String institution, String type,
                            String maskedNumber, BigDecimal initialBalance, BigDecimal currentBalance) {
        if (name == null || name.trim().isEmpty()) {
            _operationResult.setValue(Resource.error("Nama rekening wajib diisi."));
            return;
        }

        if (institution == null || institution.trim().isEmpty()) {
            _operationResult.setValue(Resource.error("Nama institusi bank/e-wallet wajib diisi."));
            return;
        }

        boolean isNew = (id == null || id.trim().isEmpty());
        String accountId = isNew ? UUID.randomUUID().toString() : id;
        BigDecimal initBal = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        BigDecimal currBal = currentBalance != null ? currentBalance : initBal;

        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setName(name.trim());
        account.setInstitutionName(institution.trim());
        account.setAccountType(type != null ? type : "Bank");
        account.setAccountNumberMasked(maskedNumber);
        account.setInitialBalance(initBal);
        account.setCurrentBalance(currBal);
        account.setActive(true);

        _operationResult.setValue(Resource.loading());
        accountRepository.saveAccount(account, isNew, result -> _operationResult.postValue(result));
    }

    public void deleteAccount(AccountEntity account) {
        if (account == null) return;
        _operationResult.setValue(Resource.loading());
        accountRepository.deleteAccount(account, result -> {
            if (result.isSuccess()) {
                _operationResult.postValue(Resource.success(account));
            } else {
                _operationResult.postValue(Resource.error(result.getMessage() != null ? result.getMessage() : "Gagal menghapus rekening."));
            }
        });
    }
}
