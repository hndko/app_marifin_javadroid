package com.example.app_marifin_javadroid.presentation.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.databinding.ActivityTransactionListBinding;
import com.example.app_marifin_javadroid.domain.usecase.CalculateCashFlowUseCase;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing, filtering, and managing transaction history with cash flow metrics.
 */
public class TransactionListActivity extends BaseActivity<ActivityTransactionListBinding> implements TransactionAdapter.OnTransactionClickListener {

    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private List<TransactionEntity> fullTransactionList = new ArrayList<>();
    private int currentTabPosition = 0;

    @NonNull
    @Override
    protected ActivityTransactionListBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityTransactionListBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Setup Filter Tabs
        binding.tabTypeFilter.addTab(binding.tabTypeFilter.newTab().setText("Semua"));
        binding.tabTypeFilter.addTab(binding.tabTypeFilter.newTab().setText("Pengeluaran"));
        binding.tabTypeFilter.addTab(binding.tabTypeFilter.newTab().setText("Pemasukan"));
        binding.tabTypeFilter.addTab(binding.tabTypeFilter.newTab().setText("Transfer"));

        adapter = new TransactionAdapter(this);
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTransactions.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            transactionViewModel.refreshTransactions();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.tabTypeFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterAndDisplayTransactions();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.fabAddTransaction.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditTransactionActivity.class));
        });

        binding.layoutEmpty.btnEmptyAction.setText("+ Catat Transaksi");
        binding.layoutEmpty.btnEmptyAction.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditTransactionActivity.class));
        });

        transactionViewModel.refreshTransactions();
    }

    @Override
    protected void setupObservers() {
        transactionViewModel.getTransactions().observe(this, transactions -> {
            if (transactions != null) {
                fullTransactionList = new ArrayList<>(transactions);

                // Calculate Cash Flow metrics
                CalculateCashFlowUseCase.CashFlowResult result = transactionViewModel.calculateCashFlow(transactions);
                binding.tvTotalIncome.setText("+" + CurrencyHelper.formatRupiah(result.getTotalIncome()));
                binding.tvTotalExpense.setText("-" + CurrencyHelper.formatRupiah(result.getTotalExpense()));
                binding.tvNetFlow.setText(CurrencyHelper.formatRupiah(result.getNetCashFlow()));

                filterAndDisplayTransactions();
            }
        });
    }

    private void filterAndDisplayTransactions() {
        List<TransactionEntity> filtered = new ArrayList<>();
        if (currentTabPosition == 0) {
            filtered.addAll(fullTransactionList);
        } else if (currentTabPosition == 1) {
            for (TransactionEntity tx : fullTransactionList) {
                if ("expense".equalsIgnoreCase(tx.getType())) {
                    filtered.add(tx);
                }
            }
        } else if (currentTabPosition == 2) {
            for (TransactionEntity tx : fullTransactionList) {
                if ("income".equalsIgnoreCase(tx.getType())) {
                    filtered.add(tx);
                }
            }
        } else if (currentTabPosition == 3) {
            for (TransactionEntity tx : fullTransactionList) {
                if ("transfer".equalsIgnoreCase(tx.getType())) {
                    filtered.add(tx);
                }
            }
        }

        if (!filtered.isEmpty()) {
            adapter.submitList(filtered);
            binding.rvTransactions.setVisibility(View.VISIBLE);
            binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.GONE);
        } else {
            adapter.submitList(null);
            binding.rvTransactions.setVisibility(View.GONE);
            binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.VISIBLE);
            binding.layoutEmpty.tvEmptyTitle.setText("Belum Ada Transaksi");
            binding.layoutEmpty.tvEmptyDesc.setText("Catat transaksi pemasukan, pengeluaran, atau transfer pertamamu.");
        }
    }

    @Override
    public void onTransactionClick(TransactionEntity transaction) {
        TransactionDetailBottomSheet sheet = TransactionDetailBottomSheet.newInstance(transaction);
        sheet.show(getSupportFragmentManager(), "transaction_detail_sheet");
    }
}
