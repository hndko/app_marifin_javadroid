package com.example.app_marifin_javadroid.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

/**
 * Base Fragment providing standard ViewBinding lifecycle and feedback helpers.
 *
 * @param <VB> ViewBinding Type
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    private VB _binding;
    protected VB binding;

    @NonNull
    protected abstract VB inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected abstract void setupViews();

    protected abstract void setupObservers();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = inflateBinding(inflater, container);
        binding = _binding;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setupObservers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
        binding = null;
    }

    protected void showToast(String message) {
        if (getContext() != null && message != null && !message.trim().isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showSnackbar(String message) {
        if (binding != null && message != null && !message.trim().isEmpty()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
