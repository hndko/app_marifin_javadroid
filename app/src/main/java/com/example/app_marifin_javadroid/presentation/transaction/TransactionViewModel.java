package com.example.app_marifin_javadroid.presentation.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.Validator;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;
import com.example.app_marifin_javadroid.data.repository.CategoryRepository;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;
import com.example.app_marifin_javadroid.domain.usecase.CalculateCashFlowUseCase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing Transactions UI state, filtering, creation, and cash flow calculations.
 */
public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final CalculateCashFlowUseCase calculateCashFlowUseCase = new CalculateCashFlowUseCase();

    private final MutableLiveData<Resource<TransactionEntity>> _operationResult = new MutableLiveData<>();
    public final LiveData<Resource<TransactionEntity>> operationResult = _operationResult;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        this.transactionRepository = TransactionRepository.getInstance(application);
        this.accountRepository = AccountRepository.getInstance(application);
        this.categoryRepository = CategoryRepository.getInstance(application);
    }

    public TransactionViewModel(@NonNull Application application,
                                @NonNull TransactionRepository txRepo,
                                @NonNull AccountRepository accRepo,
                                @NonNull CategoryRepository catRepo) {
        super(application);
        this.transactionRepository = txRepo;
        this.accountRepository = accRepo;
        this.categoryRepository = catRepo;
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactionsLiveData();
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accountRepository.getAccountsLiveData();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categoryRepository.getCategoriesLiveData();
    }

    public void refreshTransactions() {
        transactionRepository.refreshTransactions(null);
    }

    public CalculateCashFlowUseCase.CashFlowResult calculateCashFlow(List<TransactionEntity> transactions) {
        return calculateCashFlowUseCase.execute(transactions);
    }

    public void saveTransaction(String id, String accountId, String categoryId,
                                String destinationAccountId, String type,
                                BigDecimal amount, String description,
                                Date transactionDate, String attachmentUrl) {

        if (accountId == null || accountId.trim().isEmpty()) {
            _operationResult.setValue(Resource.error("Silakan pilih rekening."));
            return;
        }

        if (!Validator.isValidAmount(amount)) {
            _operationResult.setValue(Resource.error("Nominal transaksi harus lebih besar dari 0."));
            return;
        }

        if (transactionDate == null) {
            transactionDate = new Date();
        }

        _operationResult.setValue(Resource.loading());

        if ("transfer".equalsIgnoreCase(type)) {
            if (destinationAccountId == null || destinationAccountId.trim().isEmpty()) {
                _operationResult.setValue(Resource.error("Silakan pilih rekening tujuan transfer."));
                return;
            }

            if (accountId.equals(destinationAccountId)) {
                _operationResult.setValue(Resource.error("Rekening asal dan rekening tujuan tidak boleh sama."));
                return;
            }

            transactionRepository.executeTransfer(
                    accountId,
                    destinationAccountId,
                    amount,
                    description != null ? description : "Transfer Antar Rekening",
                    transactionDate,
                    result -> _operationResult.postValue(result)
            );
        } else {
            // Income or Expense
            boolean isNew = (id == null || id.trim().isEmpty());
            String txId = isNew ? UUID.randomUUID().toString() : id;

            TransactionEntity tx = new TransactionEntity();
            tx.setId(txId);
            tx.setAccountId(accountId);
            tx.setCategoryId(categoryId);
            tx.setType(type != null ? type : "expense");
            tx.setAmount(amount);
            tx.setDescription(description != null ? description.trim() : "");
            tx.setTransactionDate(transactionDate);
            tx.setAttachmentUrl(attachmentUrl);

            transactionRepository.saveIncomeOrExpense(tx, isNew, result -> _operationResult.postValue(result));
        }
    }

    public void deleteTransaction(TransactionEntity transaction) {
        if (transaction == null) return;
        _operationResult.setValue(Resource.loading());
        transactionRepository.deleteTransaction(transaction, result -> {
            if (result.isSuccess()) {
                _operationResult.postValue(Resource.success(transaction));
            } else {
                _operationResult.postValue(Resource.error(result.getMessage() != null ? result.getMessage() : "Gagal menghapus transaksi."));
            }
        });
    }
}
