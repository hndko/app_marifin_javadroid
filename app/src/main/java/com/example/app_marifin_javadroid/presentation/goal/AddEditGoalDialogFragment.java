package com.example.app_marifin_javadroid.presentation.goal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.databinding.DialogAddEditGoalBinding;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialog Fragment for creating a new Financial Goal.
 */
public class AddEditGoalDialogFragment extends DialogFragment {

    private DialogAddEditGoalBinding binding;
    private GoalViewModel goalViewModel;
    private Date selectedDeadline;

    public static AddEditGoalDialogFragment newInstance() {
        return new AddEditGoalDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditGoalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalViewModel = new ViewModelProvider(requireActivity()).get(GoalViewModel.class);

        binding.etGoalDeadline.setOnClickListener(v -> showDatePicker());
        binding.btnCancelGoal.setOnClickListener(v -> dismiss());
        binding.btnSaveGoal.setOnClickListener(v -> saveGoal());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (selectedDeadline != null) cal.setTime(selectedDeadline);

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (v, year, month, dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, dayOfMonth, 23, 59, 59);
                    selectedDeadline = chosen.getTime();
                    binding.etGoalDeadline.setText(DateHelper.formatDisplayShort(selectedDeadline));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void saveGoal() {
        String name = binding.etGoalName.getText() != null ? binding.etGoalName.getText().toString().trim() : "";
        String amountStr = binding.etGoalTargetAmount.getText() != null ? binding.etGoalTargetAmount.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilGoalName.setError("Nama target wajib diisi.");
            return;
        }
        binding.tilGoalName.setError(null);

        if (TextUtils.isEmpty(amountStr)) {
            binding.tilGoalTargetAmount.setError("Nominal target wajib diisi.");
            return;
        }

        BigDecimal targetAmount;
        try {
            targetAmount = new BigDecimal(amountStr.replaceAll("[^0-9]", ""));
            if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                binding.tilGoalTargetAmount.setError("Nominal target harus lebih dari 0.");
                return;
            }
        } catch (Exception e) {
            binding.tilGoalTargetAmount.setError("Format nominal tidak valid.");
            return;
        }
        binding.tilGoalTargetAmount.setError(null);

        GoalEntity goal = new GoalEntity();
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setDeadline(selectedDeadline);
        goal.setStatus("in_progress");

        goalViewModel.createGoal(goal, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Target tabungan berhasil dibuat!", Toast.LENGTH_SHORT).show();
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
