package com.example.app_marifin_javadroid.presentation.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.databinding.FragmentLoginBinding;

/**
 * Login Fragment for user authentication.
 */
public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    private AuthViewModel authViewModel;

    @NonNull
    @Override
    protected FragmentLoginBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            authViewModel.login(email, password);
        });

        binding.btnToRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToRegister();
            }
        });

        binding.btnForgotPassword.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToForgotPassword();
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
                    binding.btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    showToast("Berhasil masuk!");
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).navigateToOnboardingOrMain();
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    showSnackbar(resource.getMessage() != null ? resource.getMessage() : "Gagal masuk");
                    break;
                case EMPTY:
                    break;
            }
        });
    }
}
