package com.example.app_marifin_javadroid.presentation.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.databinding.FragmentRegisterBinding;

/**
 * Register Fragment for new user registration.
 */
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> {

    private AuthViewModel authViewModel;

    @NonNull
    @Override
    protected FragmentRegisterBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            String confirm = binding.etPasswordConfirm.getText() != null ? binding.etPasswordConfirm.getText().toString() : "";

            authViewModel.register(email, password, confirm, fullName);
        });

        binding.btnToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToLogin();
            }
        });
    }

    @Override
    protected void setupObservers() {
        authViewModel.authResult.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    showToast("Akun berhasil dibuat!");
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).navigateToOnboardingOrMain();
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    showSnackbar(resource.getMessage() != null ? resource.getMessage() : "Pendaftaran gagal");
                    break;
                case EMPTY:
                    break;
            }
        });
    }
}
