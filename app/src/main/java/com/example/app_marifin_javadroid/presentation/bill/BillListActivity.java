package com.example.app_marifin_javadroid.presentation.bill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.databinding.ActivityBillListBinding;

/**
 * Activity for managing recurring Bills / Tagihan Rutin.
 */
public class BillListActivity extends BaseActivity<ActivityBillListBinding> {

    private BillViewModel billViewModel;
    private BillAdapter billAdapter;

    @NonNull
    @Override
    protected ActivityBillListBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityBillListBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        billViewModel = new ViewModelProvider(this).get(BillViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        billAdapter = new BillAdapter(new BillAdapter.OnBillActionListener() {
            @Override
            public void onPayBill(BillEntity bill) {
                PayBillDialogFragment dialog = PayBillDialogFragment.newInstance(bill);
                dialog.show(getSupportFragmentManager(), "pay_bill_dialog");
            }

            @Override
            public void onDeleteBill(BillEntity bill) {
                new AlertDialog.Builder(BillListActivity.this)
                        .setTitle("Hapus Tagihan")
                        .setMessage(String.format("Yakin ingin menghapus tagihan '%s'?", bill.getName()))
                        .setPositiveButton(R.string.action_delete, (d, w) -> {
                            billViewModel.deleteBill(bill, resource -> {
                                runOnUiThread(() -> Toast.makeText(BillListActivity.this, "Tagihan dihapus.", Toast.LENGTH_SHORT).show());
                            });
                        })
                        .setNegativeButton(R.string.action_cancel, null)
                        .show();
            }
        });

        binding.rvBills.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBills.setAdapter(billAdapter);

        binding.fabAddBill.setOnClickListener(v -> {
            AddEditBillDialogFragment dialog = AddEditBillDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_bill_dialog");
        });

        binding.layoutEmptyState.btnEmptyAction.setOnClickListener(v -> {
            AddEditBillDialogFragment dialog = AddEditBillDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_bill_dialog");
        });

        binding.layoutEmptyState.ivEmptyIcon.setImageResource(R.drawable.ic_note);
        binding.layoutEmptyState.tvEmptyTitle.setText(R.string.empty_bills_title);
        binding.layoutEmptyState.tvEmptyDesc.setText(R.string.empty_bills_desc);
        binding.layoutEmptyState.btnEmptyAction.setText(R.string.action_add_bill);

        binding.swipeRefreshBills.setOnRefreshListener(() -> {
            billViewModel.refreshBills();
            binding.swipeRefreshBills.setRefreshing(false);
        });

        billViewModel.refreshBills();
    }

    @Override
    protected void setupObservers() {
        billViewModel.getBills().observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                billAdapter.submitList(list);
                binding.rvBills.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.getRoot().setVisibility(View.GONE);
            } else {
                billAdapter.submitList(null);
                binding.rvBills.setVisibility(View.GONE);
                binding.layoutEmptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
