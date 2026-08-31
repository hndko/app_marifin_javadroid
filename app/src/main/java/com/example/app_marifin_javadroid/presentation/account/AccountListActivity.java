package com.example.app_marifin_javadroid.presentation.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.databinding.ActivityAccountListBinding;

import java.math.BigDecimal;

/**
 * Activity for displaying and managing all financial accounts.
 */
public class AccountListActivity extends BaseActivity<ActivityAccountListBinding> implements AccountAdapter.OnAccountActionListener {

    private AccountViewModel accountViewModel;
    private AccountAdapter adapter;

    @NonNull
    @Override
    protected ActivityAccountListBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityAccountListBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AccountAdapter(this);
        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAccounts.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            accountViewModel.refreshAccounts();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.fabAddAccount.setOnClickListener(v -> {
            AddEditAccountDialogFragment dialog = AddEditAccountDialogFragment.newInstance(null);
            dialog.show(getSupportFragmentManager(), "add_account_dialog");
        });

        binding.layoutEmpty.btnEmptyAction.setText("+ Tambah Rekening");
        binding.layoutEmpty.btnEmptyAction.setOnClickListener(v -> {
            AddEditAccountDialogFragment dialog = AddEditAccountDialogFragment.newInstance(null);
            dialog.show(getSupportFragmentManager(), "add_account_dialog");
        });

        accountViewModel.refreshAccounts();
    }

    @Override
    protected void setupObservers() {
        accountViewModel.getAccounts().observe(this, accounts -> {
            if (accounts != null && !accounts.isEmpty()) {
                adapter.submitList(accounts);
                binding.rvAccounts.setVisibility(View.VISIBLE);
                binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.rvAccounts.setVisibility(View.GONE);
                binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.VISIBLE);
                binding.layoutEmpty.tvEmptyTitle.setText("Belum Ada Rekening");
                binding.layoutEmpty.tvEmptyDesc.setText("Tambahkan rekening bank atau e-wallet pertamamu untuk memantau saldo.");
            }
        });

        accountViewModel.getTotalBalance().observe(this, totalBalance -> {
            if (totalBalance != null) {
                binding.tvTotalBalance.setText(CurrencyHelper.formatRupiah(BigDecimal.valueOf(totalBalance)));
            } else {
                binding.tvTotalBalance.setText("Rp0");
            }
        });
    }

    @Override
    public void onEdit(AccountEntity account) {
        AddEditAccountDialogFragment dialog = AddEditAccountDialogFragment.newInstance(account);
        dialog.show(getSupportFragmentManager(), "edit_account_dialog");
    }

    @Override
    public void onDelete(AccountEntity account) {
        accountViewModel.deleteAccount(account);
        showToast("Rekening berhasil dihapus.");
    }
}
