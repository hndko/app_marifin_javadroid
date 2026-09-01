package com.example.app_marifin_javadroid.presentation.bill;

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
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.databinding.DialogAddEditBillBinding;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialog Fragment for adding a new bill.
 */
public class AddEditBillDialogFragment extends DialogFragment {

    private DialogAddEditBillBinding binding;
    private BillViewModel billViewModel;
    private final Calendar selectedDueDate = Calendar.getInstance();

    public static AddEditBillDialogFragment newInstance() {
        return new AddEditBillDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);

        binding.etBillDueDate.setText(DateHelper.formatDisplayShort(selectedDueDate.getTime()));
        binding.etBillDueDate.setOnClickListener(v -> showDatePicker());

        binding.btnCancelBill.setOnClickListener(v -> dismiss());

        binding.btnSaveBill.setOnClickListener(v -> saveBill());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDueDate.set(Calendar.YEAR, year);
                    selectedDueDate.set(Calendar.MONTH, month);
                    selectedDueDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.etBillDueDate.setText(DateHelper.formatDisplayShort(selectedDueDate.getTime()));
                },
                selectedDueDate.get(Calendar.YEAR),
                selectedDueDate.get(Calendar.MONTH),
                selectedDueDate.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void saveBill() {
        String name = binding.etBillName.getText() != null ? binding.etBillName.getText().toString().trim() : "";
        String amountStr = binding.etBillAmount.getText() != null ? binding.etBillAmount.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilBillName.setError("Nama tagihan wajib diisi.");
            return;
        }
        binding.tilBillName.setError(null);

        if (TextUtils.isEmpty(amountStr)) {
            binding.tilBillAmount.setError("Nominal tagihan wajib diisi.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.replaceAll("[^0-9]", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                binding.tilBillAmount.setError("Nominal harus lebih dari 0.");
                return;
            }
        } catch (Exception e) {
            binding.tilBillAmount.setError("Format nominal tidak valid.");
            return;
        }
        binding.tilBillAmount.setError(null);

        BillEntity bill = new BillEntity();
        bill.setName(name);
        bill.setAmount(amount);
        bill.setDueDate(selectedDueDate.getTime());
        bill.setStatus("upcoming");

        billViewModel.createBill(bill, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Tagihan berhasil disimpan!", Toast.LENGTH_SHORT).show();
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
