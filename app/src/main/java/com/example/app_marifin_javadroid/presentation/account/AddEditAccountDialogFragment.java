package com.example.app_marifin_javadroid.presentation.account;

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

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.databinding.DialogAddEditAccountBinding;

import java.math.BigDecimal;

/**
 * Modal Dialog Fragment for creating and editing financial accounts.
 */
public class AddEditAccountDialogFragment extends DialogFragment {

    private DialogAddEditAccountBinding binding;
    private AccountViewModel accountViewModel;
    private AccountEntity accountToEdit;

    public static AddEditAccountDialogFragment newInstance(@Nullable AccountEntity account) {
        AddEditAccountDialogFragment fragment = new AddEditAccountDialogFragment();
        fragment.accountToEdit = account;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        String[] accountTypes = new String[]{"Bank", "E-Wallet", "Cash", "Credit Card", "Investment", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, accountTypes);
        binding.actvAccountType.setAdapter(typeAdapter);

        if (accountToEdit != null) {
            binding.tvDialogTitle.setText("Ubah Rekening");
            binding.etAccountName.setText(accountToEdit.getName());
            binding.etInstitutionName.setText(accountToEdit.getInstitutionName());
            binding.actvAccountType.setText(accountToEdit.getAccountType(), false);
            binding.etAccountNumber.setText(accountToEdit.getAccountNumberMasked() != null ? accountToEdit.getAccountNumberMasked() : "");
            binding.etInitialBalance.setText(accountToEdit.getInitialBalance().toPlainString());
            binding.tilInitialBalance.setEnabled(false); // Initial balance is locked during edit
        }

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSaveAccount.setOnClickListener(v -> {
            String name = binding.etAccountName.getText() != null ? binding.etAccountName.getText().toString().trim() : "";
            String institution = binding.etInstitutionName.getText() != null ? binding.etInstitutionName.getText().toString().trim() : "";
            String type = binding.actvAccountType.getText() != null ? binding.actvAccountType.getText().toString() : "Bank";
            String number = binding.etAccountNumber.getText() != null ? binding.etAccountNumber.getText().toString().trim() : "";
            String balanceStr = binding.etInitialBalance.getText() != null ? binding.etInitialBalance.getText().toString() : "0";

            BigDecimal balance = CurrencyHelper.parseRupiah(balanceStr);

            String accountId = accountToEdit != null ? accountToEdit.getId() : null;
            BigDecimal currentBal = accountToEdit != null ? accountToEdit.getCurrentBalance() : balance;

            accountViewModel.saveAccount(accountId, name, institution, type, number, balance, currentBal);
            Toast.makeText(requireContext(), "Rekening berhasil disimpan.", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
