package com.example.app_marifin_javadroid.presentation.onboarding;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.databinding.ActivityOnboardingBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 5-Step Onboarding Activity introducing MariFin core features.
 */
public class OnboardingActivity extends BaseActivity<ActivityOnboardingBinding> {

    private List<OnboardingSlide> slides;
    private OnboardingAdapter adapter;
    private SecureSessionManager sessionManager;

    @NonNull
    @Override
    protected ActivityOnboardingBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityOnboardingBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        sessionManager = SecureSessionManager.getInstance(this);

        slides = new ArrayList<>();
        slides.add(new OnboardingSlide(
                "Selamat Datang di MariFin",
                "Aplikasi pencatatan keuangan pintar untuk membantumu mengelola arus kas, budget, dan rekening secara cerdas.",
                R.drawable.ic_marifin_logo
        ));
        slides.add(new OnboardingSlide(
                "Catat Transaksi dengan Mudah",
                "Catat pemasukan, pengeluaran, dan transfer antar rekening dalam hitungan detik tanpa ribet.",
                R.drawable.ic_wallet_card
        ));
        slides.add(new OnboardingSlide(
                "Pantau Pengeluaran & Budget",
                "Tetapkan batas anggaran per kategori dan pantau persentase penggunaan agar keuangan tetap aman.",
                R.drawable.ic_budget
        ));
        slides.add(new OnboardingSlide(
                "Biarkan FinGPT Membantu",
                "Asisten AI cerdas untuk mencatat transaksi dengan bahasa alami dan memberikan saran finansial terkontrol.",
                R.drawable.ic_robot
        ));
        slides.add(new OnboardingSlide(
                "Setup Rekening Pertamamu",
                "Hubungkan atau catat saldo awal rekening bank dan e-wallet untuk mulai mengontrol masa depanmu.",
                R.drawable.ic_bank
        ));

        adapter = new OnboardingAdapter(slides);
        binding.viewPager.setAdapter(adapter);

        updateStepIndicator(0);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateStepIndicator(position);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < slides.size() - 1) {
                binding.viewPager.setCurrentItem(current + 1, true);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    @Override
    protected void setupObservers() {
        // No ViewModel observers required
    }

    private void updateStepIndicator(int position) {
        binding.tvStepIndicator.setText(String.format("Langkah %d dari %d", position + 1, slides.size()));
        if (position == slides.size() - 1) {
            binding.btnNext.setText("Mulai Sekarang");
        } else {
            binding.btnNext.setText("Lanjut");
        }
    }

    private void finishOnboarding() {
        sessionManager.setOnboardingCompleted(true);
        finish();
    }
}
