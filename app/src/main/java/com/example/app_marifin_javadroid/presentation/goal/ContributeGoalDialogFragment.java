package com.example.app_marifin_javadroid.presentation.goal;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.databinding.DialogContributeGoalBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog Fragment for depositing/contributing money to a Financial Goal.
 */
public class ContributeGoalDialogFragment extends DialogFragment {

    private DialogContributeGoalBinding binding;
    private GoalViewModel goalViewModel;
    private GoalEntity currentGoal;
    private final List<AccountEntity> accountList = new ArrayList<>();
    private AccountEntity selectedAccount;

    public static ContributeGoalDialogFragment newInstance(GoalEntity goal) {
        ContributeGoalDialogFragment dialog = new ContributeGoalDialogFragment();
        dialog.currentGoal = goal;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogContributeGoalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalViewModel = new ViewModelProvider(requireActivity()).get(GoalViewModel.class);

        if (currentGoal != null) {
            binding.tvContribGoalName.setText(currentGoal.getName());
            binding.tvContribGoalProgress.setText(String.format("Terkumpul: %s / %s",
                    CurrencyHelper.formatRupiah(currentGoal.getCurrentAmount()),
                    CurrencyHelper.formatRupiah(currentGoal.getTargetAmount())));
        }

        binding.btnCancelContrib.setOnClickListener(v -> dismiss());

        goalViewModel.getAccounts().observe(getViewLifecycleOwner(), accounts -> {
            accountList.clear();
            List<String> accountNames = new ArrayList<>();
            if (accounts != null) {
                accountList.addAll(accounts);
                for (AccountEntity a : accounts) {
                    accountNames.add(a.getName() + " (" + CurrencyHelper.formatRupiah(a.getCurrentBalance()) + ")");
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, accountNames);
            binding.actvContribSourceAccount.setAdapter(adapter);

            if (!accountList.isEmpty()) {
                selectedAccount = accountList.get(0);
                binding.actvContribSourceAccount.setText(accountNames.get(0), false);
            }
        });

        binding.actvContribSourceAccount.setOnItemClickListener((parent, view1, position, id) -> {
            if (position >= 0 && position < accountList.size()) {
                selectedAccount = accountList.get(position);
            }
        });

        binding.btnConfirmContrib.setOnClickListener(v -> {
            if (selectedAccount == null) {
                binding.tilContribSourceAccount.setError("Pilih rekening sumber.");
                return;
            }
            binding.tilContribSourceAccount.setError(null);

            String amountStr = binding.etContribAmount.getText() != null ? binding.etContribAmount.getText().toString().trim() : "";
            if (TextUtils.isEmpty(amountStr)) {
                binding.tilContribAmount.setError("Nominal setoran wajib diisi.");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr.replaceAll("[^0-9]", ""));
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    binding.tilContribAmount.setError("Nominal harus lebih dari 0.");
                    return;
                }
            } catch (Exception e) {
                binding.tilContribAmount.setError("Format nominal tidak valid.");
                return;
            }
            binding.tilContribAmount.setError(null);

            if (currentGoal != null) {
                goalViewModel.contributeToGoal(currentGoal, selectedAccount.getId(), amount, resource -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Setoran tabungan berhasil!", Toast.LENGTH_SHORT).show();
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
