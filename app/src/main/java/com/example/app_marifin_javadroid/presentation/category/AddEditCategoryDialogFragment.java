package com.example.app_marifin_javadroid.presentation.category;

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

import com.example.app_marifin_javadroid.databinding.DialogAddEditCategoryBinding;

/**
 * Modal Dialog Fragment for creating custom categories.
 */
public class AddEditCategoryDialogFragment extends DialogFragment {

    private DialogAddEditCategoryBinding binding;
    private CategoryViewModel categoryViewModel;

    public static AddEditCategoryDialogFragment newInstance() {
        return new AddEditCategoryDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditCategoryBinding.inflate(inflater, container, false);
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
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        String[] categoryTypes = new String[]{"Pengeluaran", "Pemasukan"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryTypes);
        binding.actvCategoryType.setAdapter(typeAdapter);

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSaveCategory.setOnClickListener(v -> {
            String name = binding.etCategoryName.getText() != null ? binding.etCategoryName.getText().toString().trim() : "";
            String typeText = binding.actvCategoryType.getText() != null ? binding.actvCategoryType.getText().toString() : "Pengeluaran";
            String type = "Pemasukan".equalsIgnoreCase(typeText) ? "income" : "expense";
            String color = "income".equalsIgnoreCase(type) ? "#10B981" : "#1E56A0";

            categoryViewModel.saveCategory(null, name, type, "ic_category_default", color);
            Toast.makeText(requireContext(), "Kategori berhasil disimpan.", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
