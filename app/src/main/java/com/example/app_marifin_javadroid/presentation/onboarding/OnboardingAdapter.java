package com.example.app_marifin_javadroid.presentation.onboarding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.databinding.ItemOnboardingSlideBinding;

import java.util.List;

/**
 * ViewPager2 Adapter for 5-step Onboarding slides.
 */
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private final List<OnboardingSlide> slides;

    public OnboardingAdapter(List<OnboardingSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOnboardingSlideBinding binding = ItemOnboardingSlideBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new SlideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.bind(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides != null ? slides.size() : 0;
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        private final ItemOnboardingSlideBinding binding;

        public SlideViewHolder(@NonNull ItemOnboardingSlideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OnboardingSlide slide) {
            binding.tvSlideTitle.setText(slide.getTitle());
            binding.tvSlideDescription.setText(slide.getDescription());
            binding.ivSlideIcon.setImageResource(slide.getIconRes());
        }
    }
}
