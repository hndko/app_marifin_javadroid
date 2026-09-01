package com.example.app_marifin_javadroid.presentation.ai;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.repository.AccountRepository;
import com.example.app_marifin_javadroid.data.repository.CategoryRepository;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;
import com.example.app_marifin_javadroid.databinding.DialogDraftTransactionPreviewBinding;
import com.example.app_marifin_javadroid.domain.model.DraftTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mandatory User Confirmation Modal for AI-Parsed Transaction Drafts.
 */
public class DraftTransactionPreviewDialog extends DialogFragment {

    private DialogDraftTransactionPreviewBinding binding;
    private DraftTransaction draftTransaction;
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private CategoryRepository categoryRepository;

    private final List<AccountEntity> accountList = new ArrayList<>();
    private final List<CategoryEntity> categoryList = new ArrayList<>();
    private AccountEntity selectedAccount;
    private CategoryEntity selectedCategory;

    public static DraftTransactionPreviewDialog newInstance(@NonNull DraftTransaction draft) {
        DraftTransactionPreviewDialog dialog = new DraftTransactionPreviewDialog();
        dialog.draftTransaction = draft;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogDraftTransactionPreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionRepository = TransactionRepository.getInstance(requireContext());
        accountRepository = AccountRepository.getInstance(requireContext());
        categoryRepository = CategoryRepository.getInstance(requireContext());

        if (draftTransaction != null) {
            binding.etDraftAmount.setText(draftTransaction.getAmount().toPlainString());
            binding.etDraftDesc.setText(draftTransaction.getDescription());
        }

        binding.btnCancelDraft.setOnClickListener(v -> dismiss());

        // Setup Accounts
        accountRepository.getAccountsLiveData().observe(getViewLifecycleOwner(), accounts -> {
            accountList.clear();
            List<String> names = new ArrayList<>();
            if (accounts != null) {
                accountList.addAll(accounts);
                for (AccountEntity a : accounts) {
                    names.add(a.getName() + " (" + CurrencyHelper.formatRupiah(a.getCurrentBalance()) + ")");
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
            binding.actvDraftAccount.setAdapter(adapter);

            if (!accountList.isEmpty()) {
                selectedAccount = accountList.get(0);
                binding.actvDraftAccount.setText(names.get(0), false);
            }
        });

        binding.actvDraftAccount.setOnItemClickListener((parent, v, position, id) -> {
            if (position >= 0 && position < accountList.size()) {
                selectedAccount = accountList.get(position);
            }
        });

        // Setup Categories
        categoryRepository.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            categoryList.clear();
            List<String> names = new ArrayList<>();
            int matchIndex = 0;
            if (categories != null) {
                categoryList.addAll(categories);
                for (int i = 0; i < categories.size(); i++) {
                    CategoryEntity c = categories.get(i);
                    names.add(c.getName());
                    if (draftTransaction != null && draftTransaction.getPredictedCategoryName() != null &&
                            c.getName().equalsIgnoreCase(draftTransaction.getPredictedCategoryName())) {
                        matchIndex = i;
                    }
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
            binding.actvDraftCategory.setAdapter(adapter);

            if (!categoryList.isEmpty()) {
                selectedCategory = categoryList.get(matchIndex);
                binding.actvDraftCategory.setText(names.get(matchIndex), false);
            }
        });

        binding.actvDraftCategory.setOnItemClickListener((parent, v, position, id) -> {
            if (position >= 0 && position < categoryList.size()) {
                selectedCategory = categoryList.get(position);
            }
        });

        binding.btnConfirmDraft.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        if (selectedAccount == null) {
            binding.tilDraftAccount.setError("Pilih rekening.");
            return;
        }
        binding.tilDraftAccount.setError(null);

        String amountStr = binding.etDraftAmount.getText() != null ? binding.etDraftAmount.getText().toString().trim() : "";
        if (TextUtils.isEmpty(amountStr)) {
            binding.tilDraftAmount.setError("Nominal wajib diisi.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.replaceAll("[^0-9]", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                binding.tilDraftAmount.setError("Nominal harus lebih dari 0.");
                return;
            }
        } catch (Exception e) {
            binding.tilDraftAmount.setError("Format nominal tidak valid.");
            return;
        }
        binding.tilDraftAmount.setError(null);

        String desc = binding.etDraftDesc.getText() != null ? binding.etDraftDesc.getText().toString().trim() : "";

        TransactionEntity tx = new TransactionEntity();
        tx.setAccountId(selectedAccount.getId());
        tx.setCategoryId(selectedCategory != null ? selectedCategory.getId() : null);
        tx.setType(draftTransaction != null ? draftTransaction.getType() : "expense");
        tx.setAmount(amount);
        tx.setDescription(desc);
        tx.setMerchant(draftTransaction != null ? draftTransaction.getMerchant() : null);
        tx.setTransactionDate(draftTransaction != null && draftTransaction.getTransactionDate() != null ?
                draftTransaction.getTransactionDate() : new Date());

        transactionRepository.saveIncomeOrExpense(tx, true, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Transaksi berhasil dicatat oleh FinGPT!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }
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
