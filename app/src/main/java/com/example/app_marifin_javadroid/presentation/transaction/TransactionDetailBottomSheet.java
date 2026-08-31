package com.example.app_marifin_javadroid.presentation.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.databinding.BottomSheetTransactionDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Bottom Sheet Dialog displaying detailed transaction breakdown with delete option.
 */
public class TransactionDetailBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetTransactionDetailBinding binding;
    private TransactionViewModel transactionViewModel;
    private TransactionEntity transaction;

    public static TransactionDetailBottomSheet newInstance(TransactionEntity transaction) {
        TransactionDetailBottomSheet sheet = new TransactionDetailBottomSheet();
        sheet.transaction = transaction;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTransactionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        if (transaction == null) {
            dismiss();
            return;
        }

        binding.tvDetailDate.setText(DateHelper.formatDisplayDateTime(transaction.getTransactionDate()));
        binding.tvDetailNotes.setText(transaction.getDescription() != null && !transaction.getDescription().isEmpty()
                ? transaction.getDescription() : "Tidak ada catatan");

        if ("income".equalsIgnoreCase(transaction.getType())) {
            binding.tvDetailAmount.setText("+" + CurrencyHelper.formatRupiah(transaction.getAmount()));
            binding.tvDetailAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green));
            binding.tvDetailTypeBadge.setText("Pemasukan");
            binding.tvDetailAccount.setText("Rekening ID: " + transaction.getAccountId());
        } else if ("transfer".equalsIgnoreCase(transaction.getType())) {
            binding.tvDetailAmount.setText("⇄ " + CurrencyHelper.formatRupiah(transaction.getAmount()));
            binding.tvDetailAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            binding.tvDetailTypeBadge.setText("Transfer Antar Rekening");
            binding.tvDetailAccount.setText(String.format("Dari: %s\nKe: %s",
                    transaction.getAccountId(),
                    transaction.getDestinationAccountId() != null ? transaction.getDestinationAccountId() : "-"));
        } else {
            // Expense
            binding.tvDetailAmount.setText("-" + CurrencyHelper.formatRupiah(transaction.getAmount()));
            binding.tvDetailAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_red));
            binding.tvDetailTypeBadge.setText("Pengeluaran");
            binding.tvDetailAccount.setText("Rekening ID: " + transaction.getAccountId());
        }

        binding.btnDeleteTransaction.setOnClickListener(v -> {
            transactionViewModel.deleteTransaction(transaction);
            Toast.makeText(requireContext(), "Transaksi berhasil dihapus.", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
