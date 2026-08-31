package com.example.app_marifin_javadroid.presentation.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.databinding.ActivityMainBinding;
import com.example.app_marifin_javadroid.presentation.expense.ExpenseFragment;
import com.example.app_marifin_javadroid.presentation.home.HomeFragment;
import com.example.app_marifin_javadroid.presentation.profile.ProfileFragment;
import com.example.app_marifin_javadroid.presentation.transaction.AddEditTransactionActivity;
import com.example.app_marifin_javadroid.presentation.transaction.TransactionListActivity;

/**
 * Main Activity hosting Bottom Navigation and Fragment switching.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @NonNull
    @Override
    protected ActivityMainBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        if (getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment) == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_expense) {
                loadFragment(new ExpenseFragment());
                return true;
            } else if (itemId == R.id.navigation_add) {
                startActivity(new Intent(this, AddEditTransactionActivity.class));
                return false;
            } else if (itemId == R.id.navigation_transaction) {
                startActivity(new Intent(this, TransactionListActivity.class));
                return false;
            } else if (itemId == R.id.navigation_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void setupObservers() {
        // No activity-level observers needed
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
