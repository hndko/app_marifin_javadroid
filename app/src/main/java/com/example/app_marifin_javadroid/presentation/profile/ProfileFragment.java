package com.example.app_marifin_javadroid.presentation.profile;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.databinding.FragmentProfileBinding;
import com.example.app_marifin_javadroid.presentation.auth.AuthActivity;

/**
 * Profile and Preferences Fragment.
 */
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> {

    private ProfileViewModel profileViewModel;
    private SecureSessionManager sessionManager;

    @NonNull
    @Override
    protected FragmentProfileBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = SecureSessionManager.getInstance(requireContext());

        binding.tvFullName.setText(sessionManager.getFullName().isEmpty() ? "Pengguna MariFin" : sessionManager.getFullName());
        binding.tvEmail.setText(sessionManager.getEmail() != null ? sessionManager.getEmail() : "user@email.com");
        binding.tvAppVersion.setText("MariFin v2.0.0\nProduct by Mari Partner\nKelola Keuangan, Lebih Cerdas.");

        // Feature Navigation
        binding.btnNavReports.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.app_marifin_javadroid.presentation.report.ReportActivity.class));
        });

        binding.btnNavBills.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.app_marifin_javadroid.presentation.bill.BillListActivity.class));
        });

        binding.btnNavGoals.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.app_marifin_javadroid.presentation.goal.GoalListActivity.class));
        });

        binding.btnNavVault.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.app_marifin_javadroid.presentation.document.DocumentVaultActivity.class));
        });

        binding.btnNavCategories.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), com.example.app_marifin_javadroid.presentation.category.CategoryListActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            profileViewModel.logout();
        });

        profileViewModel.loadProfile();
    }

    @Override
    protected void setupObservers() {
        profileViewModel.profileResult.observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getData() != null) {
                if (resource.getData().getFullName() != null && !resource.getData().getFullName().isEmpty()) {
                    binding.tvFullName.setText(resource.getData().getFullName());
                }
                if (resource.getData().getCurrency() != null) {
                    binding.tvCurrency.setText(resource.getData().getCurrency() + " (Rp)");
                }
                if (resource.getData().getTimezone() != null) {
                    binding.tvTimezone.setText(resource.getData().getTimezone());
                }
            }
        });

        profileViewModel.logoutResult.observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                showToast("Berhasil keluar.");
                Intent intent = new Intent(requireActivity(), AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
}
