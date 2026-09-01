package com.example.app_marifin_javadroid.presentation.bill;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;
import com.example.app_marifin_javadroid.data.repository.BillRepository;

import java.util.List;

/**
 * ViewModel managing bills state, actions, and accounts integration.
 */
public class BillViewModel extends AndroidViewModel {

    private final BillRepository billRepository;
    private final AccountRepository accountRepository;

    public BillViewModel(@NonNull Application application) {
        super(application);
        this.billRepository = BillRepository.getInstance(application);
        this.accountRepository = AccountRepository.getInstance(application);
    }

    public LiveData<List<BillEntity>> getBills() {
        return billRepository.getBillsLiveData();
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accountRepository.getAccountsLiveData();
    }

    public void createBill(BillEntity bill, BillRepository.RepositoryCallback<BillEntity> callback) {
        billRepository.createBill(bill, callback);
    }

    public void payBill(BillEntity bill, String sourceAccountId, BillRepository.RepositoryCallback<Void> callback) {
        billRepository.payBill(bill, sourceAccountId, callback);
    }

    public void deleteBill(BillEntity bill, BillRepository.RepositoryCallback<Void> callback) {
        billRepository.deleteBill(bill, callback);
    }

    public void refreshBills() {
        billRepository.refreshBills(null);
        accountRepository.refreshAccounts(null);
    }
}
