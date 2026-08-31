package com.example.app_marifin_javadroid.presentation.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.databinding.FragmentBudgetListBinding;

/**
 * Fragment managing Budget list, progress display, and empty states.
 */
public class BudgetListFragment extends BaseFragment<FragmentBudgetListBinding> {

    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;

    @NonNull
    @Override
    protected FragmentBudgetListBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentBudgetListBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        budgetAdapter = new BudgetAdapter(item -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Anggaran")
                    .setMessage(String.format("Yakin ingin menghapus anggaran '%s'?", item.getBudget().getName()))
                    .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                        budgetViewModel.deleteBudget(item.getBudget(), resource -> {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), "Anggaran berhasil dihapus.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        });

        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBudgets.setAdapter(budgetAdapter);

        binding.fabAddBudget.setOnClickListener(v -> {
            AddEditBudgetDialogFragment dialog = AddEditBudgetDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "add_budget_dialog");
        });

        binding.layoutEmptyState.btnEmptyAction.setOnClickListener(v -> {
            AddEditBudgetDialogFragment dialog = AddEditBudgetDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "add_budget_dialog");
        });

        binding.layoutEmptyState.ivEmptyIcon.setImageResource(R.drawable.ic_budget);
        binding.layoutEmptyState.tvEmptyTitle.setText(R.string.empty_budgets_title);
        binding.layoutEmptyState.tvEmptyDesc.setText(R.string.empty_budgets_desc);
        binding.layoutEmptyState.btnEmptyAction.setText(R.string.action_create_budget);

        binding.swipeRefreshBudgets.setOnRefreshListener(() -> {
            budgetViewModel.refreshBudgets();
            binding.swipeRefreshBudgets.setRefreshing(false);
        });

        budgetViewModel.refreshBudgets();
    }

    @Override
    protected void setupObservers() {
        budgetViewModel.getBudgetsWithProgress().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                budgetAdapter.submitList(list);
                binding.rvBudgets.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.getRoot().setVisibility(View.GONE);
            } else {
                budgetAdapter.submitList(null);
                binding.rvBudgets.setVisibility(View.GONE);
                binding.layoutEmptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
