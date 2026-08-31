package com.example.app_marifin_javadroid.presentation.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.databinding.FragmentHomeBinding;
import com.example.app_marifin_javadroid.domain.usecase.CalculateCashFlowUseCase;
import com.example.app_marifin_javadroid.presentation.account.AccountListActivity;
import com.example.app_marifin_javadroid.presentation.transaction.AddEditTransactionActivity;
import com.example.app_marifin_javadroid.presentation.transaction.TransactionAdapter;
import com.example.app_marifin_javadroid.presentation.transaction.TransactionDetailBottomSheet;
import com.example.app_marifin_javadroid.presentation.transaction.TransactionListActivity;

import java.math.BigDecimal;

/**
 * Home Dashboard Fragment with Carousel, Cash Flow Summary, Quick Actions, and Recent Transactions.
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    private HomeViewModel homeViewModel;
    private AccountCarouselAdapter carouselAdapter;
    private TransactionAdapter recentTxAdapter;
    private boolean isBalanceVisible = true;
    private BigDecimal currentTotalBalance = BigDecimal.ZERO;

    @NonNull
    @Override
    protected FragmentHomeBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.tvGreeting.setText(homeViewModel.getUserGreeting());

        // Account Carousel setup
        carouselAdapter = new AccountCarouselAdapter(account -> {
            startActivity(new Intent(requireContext(), AccountListActivity.class));
        });
        binding.rvAccountsCarousel.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAccountsCarousel.setAdapter(carouselAdapter);

        // Recent Transactions setup
        recentTxAdapter = new TransactionAdapter(transaction -> {
            TransactionDetailBottomSheet sheet = TransactionDetailBottomSheet.newInstance(transaction);
            sheet.show(getChildFragmentManager(), "home_tx_detail");
        });
        binding.rvHomeRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHomeRecentTransactions.setAdapter(recentTxAdapter);

        // Balance Eye Toggle
        binding.btnToggleBalance.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            updateBalanceDisplay();
            carouselAdapter.setBalanceVisible(isBalanceVisible);
            binding.btnToggleBalance.setImageResource(isBalanceVisible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        });

        // Quick Actions
        binding.btnQuickExpense.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditTransactionActivity.class));
        });

        binding.btnQuickIncome.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditTransactionActivity.class));
        });

        binding.btnQuickTransfer.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddEditTransactionActivity.class));
        });

        binding.btnQuickFingpt.setOnClickListener(v -> {
            showToast("FinGPT siap membantumu di Phase berikutnya!");
        });

        binding.btnManageAccounts.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AccountListActivity.class));
        });

        binding.btnSeeAllTransactions.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TransactionListActivity.class));
        });

        binding.swipeRefreshHome.setOnRefreshListener(() -> {
            homeViewModel.refreshData();
            binding.swipeRefreshHome.setRefreshing(false);
        });

        homeViewModel.refreshData();
    }

    @Override
    protected void setupObservers() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                currentTotalBalance = BigDecimal.valueOf(total);
            } else {
                currentTotalBalance = BigDecimal.ZERO;
            }
            updateBalanceDisplay();
        });

        homeViewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                carouselAdapter.submitList(accounts);
            }
        });

        homeViewModel.getRecentTransactions().observe(getViewLifecycleOwner(), recentTxs -> {
            if (recentTxs != null && !recentTxs.isEmpty()) {
                recentTxAdapter.submitList(recentTxs);
                binding.rvHomeRecentTransactions.setVisibility(View.VISIBLE);
                binding.tvHomeEmptyTx.setVisibility(View.GONE);
            } else {
                recentTxAdapter.submitList(null);
                binding.rvHomeRecentTransactions.setVisibility(View.GONE);
                binding.tvHomeEmptyTx.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getTransactions().observe(getViewLifecycleOwner(), allTxs -> {
            if (allTxs != null) {
                CalculateCashFlowUseCase.CashFlowResult result = homeViewModel.calculateCashFlow(allTxs);
                binding.tvHomeIncome.setText("+" + CurrencyHelper.formatRupiah(result.getTotalIncome()));
                binding.tvHomeExpense.setText("-" + CurrencyHelper.formatRupiah(result.getTotalExpense()));
                binding.tvHomeNetFlow.setText(CurrencyHelper.formatRupiah(result.getNetCashFlow()));
            }
        });
    }

    private void updateBalanceDisplay() {
        if (isBalanceVisible) {
            binding.tvHomeTotalBalance.setText(CurrencyHelper.formatRupiah(currentTotalBalance));
        } else {
            binding.tvHomeTotalBalance.setText("••••••••");
        }
    }
}
