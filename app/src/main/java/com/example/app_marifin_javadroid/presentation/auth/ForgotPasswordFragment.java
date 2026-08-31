package com.example.app_marifin_javadroid.presentation.auth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.databinding.FragmentForgotPasswordBinding;

/**
 * Forgot Password Fragment for password reset requests.
 */
public class ForgotPasswordFragment extends BaseFragment<FragmentForgotPasswordBinding> {

    private AuthViewModel authViewModel;

    @NonNull
    @Override
    protected FragmentForgotPasswordBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentForgotPasswordBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnSendReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            authViewModel.forgotPassword(email);
        });

        binding.btnBackToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToLogin();
            }
        });
    }

    @Override
    protected void setupObservers() {
        authViewModel.forgotPasswordResult.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSendReset.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSendReset.setEnabled(true);
                    showToast("Tautan reset berhasil dikirim ke email!");
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).navigateToLogin();
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSendReset.setEnabled(true);
                    showSnackbar(resource.getMessage() != null ? resource.getMessage() : "Gagal mengirim email reset");
                    break;
                case EMPTY:
                    break;
            }
        });
    }
}
