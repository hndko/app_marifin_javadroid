package com.example.app_marifin_javadroid.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.databinding.ActivityAuthBinding;
import com.example.app_marifin_javadroid.presentation.onboarding.OnboardingActivity;

/**
 * Authentication Container Activity hosting Login, Register, and Forgot Password fragments.
 */
public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private AuthViewModel authViewModel;

    @NonNull
    @Override
    protected ActivityAuthBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityAuthBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (authViewModel.isLoggedIn()) {
            if (!authViewModel.isOnboardingCompleted()) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else {
                startActivity(new Intent(this, com.example.app_marifin_javadroid.presentation.main.MainActivity.class));
            }
            finish();
            return;
        }

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            replaceFragment(new LoginFragment(), false);
        }
    }

    @Override
    protected void setupObservers() {
        // No global activity observers needed
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToLogin() {
        replaceFragment(new LoginFragment(), false);
    }

    public void navigateToRegister() {
        replaceFragment(new RegisterFragment(), true);
    }

    public void navigateToForgotPassword() {
        replaceFragment(new ForgotPasswordFragment(), true);
    }

    public void navigateToOnboardingOrMain() {
        if (!authViewModel.isOnboardingCompleted()) {
            startActivity(new Intent(this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(this, com.example.app_marifin_javadroid.presentation.main.MainActivity.class));
        }
        finish();
    }
}
