package com.example.app_marifin_javadroid.presentation.goal;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.databinding.ItemGoalBinding;
import com.example.app_marifin_javadroid.domain.usecase.CalculateGoalProgressUseCase;

/**
 * RecyclerView Adapter for Financial Goals list.
 */
public class GoalAdapter extends ListAdapter<GoalEntity, GoalAdapter.GoalViewHolder> {

    public interface OnGoalActionListener {
        void onContribute(GoalEntity goal);
        void onDelete(GoalEntity goal);
    }

    private final OnGoalActionListener listener;
    private final CalculateGoalProgressUseCase calculateGoalProgressUseCase = new CalculateGoalProgressUseCase();

    public GoalAdapter(OnGoalActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<GoalEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<GoalEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull GoalEntity oldItem, @NonNull GoalEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GoalEntity oldItem, @NonNull GoalEntity newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getCurrentAmount().compareTo(newItem.getCurrentAmount()) == 0 &&
                    oldItem.getTargetAmount().compareTo(newItem.getTargetAmount()) == 0;
        }
    };

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GoalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private final ItemGoalBinding binding;

        public GoalViewHolder(@NonNull ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GoalEntity item) {
            binding.tvGoalName.setText(item.getName());

            CalculateGoalProgressUseCase.GoalProgressResult result = calculateGoalProgressUseCase.execute(item);

            binding.progressGoalUtilization.setProgress(Math.min(result.getPercentage(), 100));

            binding.tvGoalSavedVsTarget.setText(String.format("Terkumpul: %s / %s",
                    CurrencyHelper.formatRupiah(result.getCurrentAmount()),
                    CurrencyHelper.formatRupiah(result.getTargetAmount())));

            if (result.isAchieved()) {
                binding.tvGoalStatusBadge.setText("Tercapai! 🎉");
                binding.tvGoalStatusBadge.setTextColor(Color.parseColor("#10B981"));
                binding.progressGoalUtilization.setIndicatorColor(Color.parseColor("#10B981"));
                binding.tvGoalRemaining.setText("Target tabungan telah terpenuhi!");
                binding.btnContributeGoal.setVisibility(View.GONE);
            } else {
                binding.tvGoalStatusBadge.setText(String.format("%d%%", result.getPercentage()));
                binding.tvGoalStatusBadge.setTextColor(Color.parseColor("#1E56A0"));
                binding.progressGoalUtilization.setIndicatorColor(Color.parseColor("#1E56A0"));
                binding.tvGoalRemaining.setText(String.format("Kurang: %s", CurrencyHelper.formatRupiah(result.getRemainingAmount())));
                binding.btnContributeGoal.setVisibility(View.VISIBLE);
            }

            if (item.getDeadline() != null) {
                String deadlineStr = DateHelper.formatDisplayShort(item.getDeadline());
                if (result.getDaysLeft() >= 0) {
                    binding.tvGoalDeadline.setText(String.format("Target: %s • Sisa %d hari", deadlineStr, result.getDaysLeft()));
                } else {
                    binding.tvGoalDeadline.setText(String.format("Target: %s", deadlineStr));
                }
            } else {
                binding.tvGoalDeadline.setText("Target: Fleksibel (Tanpa batas waktu)");
            }

            binding.btnContributeGoal.setOnClickListener(v -> {
                if (listener != null) listener.onContribute(item);
            });

            binding.btnGoalDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }
    }
}
