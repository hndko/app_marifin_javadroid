package com.example.app_marifin_javadroid.presentation.budget;

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

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.BudgetEntity;
import com.example.app_marifin_javadroid.databinding.DialogAddEditBudgetBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialog Fragment for creating and configuring new Budgets.
 */
public class AddEditBudgetDialogFragment extends DialogFragment {

    private DialogAddEditBudgetBinding binding;
    private BudgetViewModel budgetViewModel;
    private static final String[] PERIODS = new String[]{"Bulanan", "Mingguan", "Kustom"};

    public static AddEditBudgetDialogFragment newInstance() {
        return new AddEditBudgetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, PERIODS);
        binding.actvBudgetPeriod.setAdapter(adapter);
        binding.actvBudgetPeriod.setText(PERIODS[0], false);

        binding.btnCancelBudget.setOnClickListener(v -> dismiss());

        binding.btnSaveBudget.setOnClickListener(v -> {
            saveBudget();
        });
    }

    private void saveBudget() {
        String name = binding.etBudgetName.getText() != null ? binding.etBudgetName.getText().toString().trim() : "";
        String limitStr = binding.etBudgetLimit.getText() != null ? binding.etBudgetLimit.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilBudgetName.setError("Nama anggaran wajib diisi.");
            return;
        }
        binding.tilBudgetName.setError(null);

        if (TextUtils.isEmpty(limitStr)) {
            binding.tilBudgetLimit.setError("Batas nominal wajib diisi.");
            return;
        }

        BigDecimal limitAmount;
        try {
            limitAmount = new BigDecimal(limitStr.replaceAll("[^0-9]", ""));
            if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
                binding.tilBudgetLimit.setError("Nominal harus lebih dari 0.");
                return;
            }
        } catch (Exception e) {
            binding.tilBudgetLimit.setError("Format nominal tidak valid.");
            return;
        }
        binding.tilBudgetLimit.setError(null);

        String period = "monthly";
        String selected = binding.actvBudgetPeriod.getText().toString();
        if ("Mingguan".equalsIgnoreCase(selected)) period = "weekly";
        else if ("Kustom".equalsIgnoreCase(selected)) period = "custom";

        Date startDate = DateHelper.getStartOfMonth(new Date());
        Date endDate = DateHelper.getEndOfMonth(new Date());

        if ("weekly".equalsIgnoreCase(period)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            startDate = cal.getTime();
            cal.add(Calendar.DAY_OF_WEEK, 6);
            endDate = cal.getTime();
        }

        BudgetEntity budget = new BudgetEntity();
        budget.setName(name);
        budget.setAmount(limitAmount);
        budget.setPeriodType(period);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        budget.setAlertThreshold(80);
        budget.setActive(true);

        budgetViewModel.createBudget(budget, new ArrayList<>(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Anggaran berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }
            } else {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
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
