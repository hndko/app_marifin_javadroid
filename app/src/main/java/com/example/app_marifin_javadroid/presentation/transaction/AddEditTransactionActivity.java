package com.example.app_marifin_javadroid.presentation.transaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.databinding.ActivityAddEditTransactionBinding;
import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Activity for recording new Income, Expense, or Transfer transactions.
 */
public class AddEditTransactionActivity extends BaseActivity<ActivityAddEditTransactionBinding> {

    private TransactionViewModel transactionViewModel;
    private List<AccountEntity> accountList = new ArrayList<>();
    private List<CategoryEntity> categoryList = new ArrayList<>();
    private Date selectedDate = new Date();
    private String selectedType = "expense"; // "expense", "income", "transfer"

    @NonNull
    @Override
    protected ActivityAddEditTransactionBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityAddEditTransactionBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Setup Tabs
        binding.tabType.addTab(binding.tabType.newTab().setText("Pengeluaran"));
        binding.tabType.addTab(binding.tabType.newTab().setText("Pemasukan"));
        binding.tabType.addTab(binding.tabType.newTab().setText("Transfer"));

        binding.tabType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    selectedType = "expense";
                    binding.tvAmountLabel.setText("Nominal Pengeluaran");
                    binding.tilDestAccount.setVisibility(View.GONE);
                    binding.tilCategory.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    selectedType = "income";
                    binding.tvAmountLabel.setText("Nominal Pemasukan");
                    binding.tilDestAccount.setVisibility(View.GONE);
                    binding.tilCategory.setVisibility(View.VISIBLE);
                } else {
                    selectedType = "transfer";
                    binding.tvAmountLabel.setText("Nominal Transfer");
                    binding.tilDestAccount.setVisibility(View.VISIBLE);
                    binding.tilCategory.setVisibility(View.GONE);
                }
                updateCategoryDropdown();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Date selection
        binding.etDate.setText(DateHelper.formatDisplayDateTime(selectedDate));
        binding.etDate.setOnClickListener(v -> showDateTimePicker());

        binding.btnSaveTransaction.setOnClickListener(v -> handleSaveTransaction());
    }

    @Override
    protected void setupObservers() {
        transactionViewModel.getAccounts().observe(this, accounts -> {
            if (accounts != null) {
                accountList = accounts;
                List<String> accountNames = new ArrayList<>();
                for (AccountEntity a : accounts) {
                    accountNames.add(a.getName() + " (" + CurrencyHelper.formatRupiah(a.getCurrentBalance()) + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accountNames);
                binding.actvSourceAccount.setAdapter(adapter);
                binding.actvDestAccount.setAdapter(adapter);

                if (!accounts.isEmpty() && binding.actvSourceAccount.getText().toString().isEmpty()) {
                    binding.actvSourceAccount.setText(accountNames.get(0), false);
                }
            }
        });

        transactionViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                categoryList = categories;
                updateCategoryDropdown();
            }
        });

        transactionViewModel.operationResult.observe(this, resource -> {
            if (resource == null) return;

            if (resource.isSuccess()) {
                showToast("Transaksi berhasil disimpan!");
                finish();
            } else if (resource.isError()) {
                showSnackbar(resource.getMessage() != null ? resource.getMessage() : "Gagal menyimpan transaksi.");
            }
        });
    }

    private void updateCategoryDropdown() {
        List<String> filteredCategoryNames = new ArrayList<>();
        for (CategoryEntity c : categoryList) {
            if ("both".equalsIgnoreCase(c.getType()) || selectedType.equalsIgnoreCase(c.getType())) {
                filteredCategoryNames.add(c.getName());
            }
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, filteredCategoryNames);
        binding.actvCategory.setAdapter(catAdapter);

        if (!filteredCategoryNames.isEmpty()) {
            binding.actvCategory.setText(filteredCategoryNames.get(0), false);
        }
    }

    private void showDateTimePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                selectedDate = cal.getTime();
                binding.etDate.setText(DateHelper.formatDisplayDateTime(selectedDate));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void handleSaveTransaction() {
        String amountStr = binding.etAmount.getText() != null ? binding.etAmount.getText().toString().trim() : "0";
        BigDecimal amount = CurrencyHelper.parseRupiah(amountStr);

        String sourceAccText = binding.actvSourceAccount.getText().toString();
        String destAccText = binding.actvDestAccount.getText().toString();
        String catText = binding.actvCategory.getText().toString();
        String notes = binding.etNotes.getText() != null ? binding.etNotes.getText().toString().trim() : "";

        String sourceAccountId = null;
        for (int i = 0; i < accountList.size(); i++) {
            if (sourceAccText.startsWith(accountList.get(i).getName())) {
                sourceAccountId = accountList.get(i).getId();
                break;
            }
        }

        String destAccountId = null;
        if ("transfer".equalsIgnoreCase(selectedType)) {
            for (int i = 0; i < accountList.size(); i++) {
                if (destAccText.startsWith(accountList.get(i).getName())) {
                    destAccountId = accountList.get(i).getId();
                    break;
                }
            }
        }

        String categoryId = null;
        for (CategoryEntity c : categoryList) {
            if (c.getName().equalsIgnoreCase(catText)) {
                categoryId = c.getId();
                break;
            }
        }

        transactionViewModel.saveTransaction(
                null,
                sourceAccountId,
                categoryId,
                destAccountId,
                selectedType,
                amount,
                notes,
                selectedDate,
                null
        );
    }
}
