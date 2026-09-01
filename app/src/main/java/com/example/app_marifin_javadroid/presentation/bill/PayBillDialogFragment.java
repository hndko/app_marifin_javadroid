package com.example.app_marifin_javadroid.presentation.bill;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.databinding.DialogPayBillBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog Fragment for selecting paying account and executing atomic bill payment.
 */
public class PayBillDialogFragment extends DialogFragment {

    private DialogPayBillBinding binding;
    private BillViewModel billViewModel;
    private BillEntity currentBill;
    private final List<AccountEntity> accountList = new ArrayList<>();
    private AccountEntity selectedAccount;

    public static PayBillDialogFragment newInstance(BillEntity bill) {
        PayBillDialogFragment dialog = new PayBillDialogFragment();
        dialog.currentBill = bill;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogPayBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);

        if (currentBill != null) {
            binding.tvPayBillName.setText(currentBill.getName());
            binding.tvPayBillAmount.setText(CurrencyHelper.formatRupiah(currentBill.getAmount()));
        }

        binding.btnCancelPay.setOnClickListener(v -> dismiss());

        billViewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            accountList.clear();
            List<String> accountNames = new ArrayList<>();
            if (accounts != null) {
                accountList.addAll(accounts);
                for (AccountEntity a : accounts) {
                    accountNames.add(a.getName() + " (" + CurrencyHelper.formatRupiah(a.getCurrentBalance()) + ")");
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, accountNames);
            binding.actvPaySourceAccount.setAdapter(adapter);

            if (!accountList.isEmpty()) {
                selectedAccount = accountList.get(0);
                binding.actvPaySourceAccount.setText(accountNames.get(0), false);
            }
        });

        binding.actvPaySourceAccount.setOnItemClickListener((parent, view1, position, id) -> {
            if (position >= 0 && position < accountList.size()) {
                selectedAccount = accountList.get(position);
            }
        });

        binding.btnConfirmPay.setOnClickListener(v -> {
            if (selectedAccount == null) {
                binding.tilPaySourceAccount.setError("Pilih rekening pembayar.");
                return;
            }
            binding.tilPaySourceAccount.setError(null);

            if (currentBill != null) {
                billViewModel.payBill(currentBill, selectedAccount.getId(), resource -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Tagihan berhasil dibayar!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
