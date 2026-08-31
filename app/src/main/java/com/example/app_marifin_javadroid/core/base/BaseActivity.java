package com.example.app_marifin_javadroid.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

/**
 * Base Activity providing standard ViewBinding lifecycle, Toast, and Snackbar helpers.
 *
 * @param <VB> ViewBinding Type
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected VB binding;

    @NonNull
    protected abstract VB inflateBinding(@NonNull LayoutInflater layoutInflater);

    protected abstract void setupViews();

    protected abstract void setupObservers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
        setupObservers();
    }

    protected void showToast(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showSnackbar(String message) {
        if (binding != null && message != null && !message.trim().isEmpty()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
