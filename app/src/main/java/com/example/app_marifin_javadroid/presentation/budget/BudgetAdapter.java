package com.example.app_marifin_javadroid.presentation.budget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.model.BudgetWithProgress;
import com.example.app_marifin_javadroid.databinding.ItemBudgetBinding;

import java.math.BigDecimal;

/**
 * RecyclerView Adapter for displaying Budgets with 4-tier status indicators and progress bars.
 */
public class BudgetAdapter extends ListAdapter<BudgetWithProgress, BudgetAdapter.BudgetViewHolder> {

    public interface OnBudgetActionListener {
        void onDeleteBudget(BudgetWithProgress budget);
    }

    private final OnBudgetActionListener actionListener;

    public BudgetAdapter(OnBudgetActionListener actionListener) {
        super(DIFF_CALLBACK);
        this.actionListener = actionListener;
    }

    private static final DiffUtil.ItemCallback<BudgetWithProgress> DIFF_CALLBACK = new DiffUtil.ItemCallback<BudgetWithProgress>() {
        @Override
        public boolean areItemsTheSame(@NonNull BudgetWithProgress oldItem, @NonNull BudgetWithProgress newItem) {
            return oldItem.getBudget().getId().equals(newItem.getBudget().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BudgetWithProgress oldItem, @NonNull BudgetWithProgress newItem) {
            return oldItem.getSpentAmount().compareTo(newItem.getSpentAmount()) == 0 &&
                    oldItem.getPercentage() == newItem.getPercentage();
        }
    };

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBudgetBinding binding = ItemBudgetBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BudgetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder {
        private final ItemBudgetBinding binding;

        public BudgetViewHolder(@NonNull ItemBudgetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BudgetWithProgress item) {
            binding.tvBudgetName.setText(item.getBudget().getName());

            String period = item.getBudget().getPeriodType();
            String periodLabel = "Bulanan";
            if ("weekly".equalsIgnoreCase(period)) periodLabel = "Mingguan";
            else if ("custom".equalsIgnoreCase(period)) periodLabel = "Kustom";
            binding.tvBudgetPeriod.setText(String.format("Periode: %s", periodLabel));

            int percentage = item.getPercentage();
            binding.progressBudgetUtilization.setProgress(Math.min(percentage, 100));

            binding.tvBudgetSpentVsLimit.setText(String.format("Terpakai: %s / %s",
                    CurrencyHelper.formatRupiah(item.getSpentAmount()),
                    CurrencyHelper.formatRupiah(item.getBudget().getAmount())));

            if (item.getRemainingAmount().compareTo(BigDecimal.ZERO) >= 0) {
                binding.tvBudgetRemaining.setText(String.format("Sisa: %s", CurrencyHelper.formatRupiah(item.getRemainingAmount())));
                binding.tvBudgetRemaining.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            } else {
                binding.tvBudgetRemaining.setText(String.format("Lebih: %s", CurrencyHelper.formatRupiah(item.getRemainingAmount().abs())));
                binding.tvBudgetRemaining.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.expense_red));
            }

            int color;
            String badgeText;

            switch (item.getStatusZone()) {
                case OVER_BUDGET:
                    color = Color.parseColor("#EF4444");
                    badgeText = String.format("Over Budget (%d%%)", percentage);
                    break;
                case DANGER:
                    color = Color.parseColor("#F97316");
                    badgeText = String.format("Kritis (%d%%)", percentage);
                    break;
                case WARNING:
                    color = Color.parseColor("#F59E0B");
                    badgeText = String.format("Waspada (%d%%)", percentage);
                    break;
                case SAFE:
                default:
                    color = Color.parseColor("#10B981");
                    badgeText = String.format("Aman (%d%%)", percentage);
                    break;
            }

            binding.tvBudgetStatusBadge.setText(badgeText);
            binding.tvBudgetStatusBadge.setTextColor(color);
            binding.progressBudgetUtilization.setIndicatorColor(color);

            binding.btnBudgetDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteBudget(item);
                }
            });
        }
    }
}
