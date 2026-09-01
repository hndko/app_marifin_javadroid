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
                "Aplikasi pencatatan keuangan pintar untuk membantumu mengelola arus kas, anggaran, dan rekening finansial secara cerdas.",
                R.drawable.ic_marifin_logo
        ));
        slides.add(new OnboardingSlide(
                "Catat Transaksi Cepat & Presisi",
                "Catat pemasukan, pengeluaran, dan transfer antar rekening dalam hitungan detik tanpa floating-point bug.",
                R.drawable.ic_onboarding_transactions
        ));
        slides.add(new OnboardingSlide(
                "Sistem 4-Zona Peringatan Anggaran",
                "Tetapkan batas anggaran per kategori dan dapatkan peringatan dini sebelum pengeluaran over budget.",
                R.drawable.ic_onboarding_budget
        ));
        slides.add(new OnboardingSlide(
                "Asisten Finansial Cerdas FinGPT",
                "Asisten AI cerdas untuk mencatat transaksi dengan bahasa alami dan memberikan strategi keuangan terkontrol.",
                R.drawable.ic_onboarding_fingpt
        ));
        slides.add(new OnboardingSlide(
                "Multi-Rekening & Gudang Dokumen",
                "Kelola seluruh rekening bank, e-wallet, serta simpan bukti struk transaksi dalam brankas digital terenkripsi.",
                R.drawable.ic_onboarding_vault
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
            binding.btnNext.setText("Mulai Sekarang ✨");
            binding.btnNext.setIconResource(R.drawable.ic_check);
        } else {
            binding.btnNext.setText("Lanjut");
            binding.btnNext.setIconResource(R.drawable.ic_arrow_forward);
        }

        android.view.View[] dots = new android.view.View[]{binding.dot1, binding.dot2, binding.dot3, binding.dot4, binding.dot5};
        float density = getResources().getDisplayMetrics().density;
        for (int i = 0; i < dots.length; i++) {
            if (dots[i] != null) {
                boolean isActive = (i == position);
                dots[i].setBackgroundResource(isActive ? R.drawable.bg_indicator_active : R.drawable.bg_indicator_inactive);
                android.view.ViewGroup.LayoutParams params = dots[i].getLayoutParams();
                params.width = (int) (isActive ? (24 * density) : (8 * density));
                dots[i].setLayoutParams(params);
            }
        }
    }

    private void finishOnboarding() {
        sessionManager.setOnboardingCompleted(true);
        finish();
    }
}
