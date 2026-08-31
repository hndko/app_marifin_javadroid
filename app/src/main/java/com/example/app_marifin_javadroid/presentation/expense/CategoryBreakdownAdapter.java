package com.example.app_marifin_javadroid.presentation.expense;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.model.CategoryExpenseAggregate;
import com.example.app_marifin_javadroid.databinding.ItemCategoryBreakdownBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * RecyclerView Adapter for category expense ranking and progress bars.
 */
public class CategoryBreakdownAdapter extends ListAdapter<CategoryExpenseAggregate, CategoryBreakdownAdapter.BreakdownViewHolder> {

    private BigDecimal totalPeriodExpense = BigDecimal.ZERO;

    public CategoryBreakdownAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setTotalPeriodExpense(BigDecimal total) {
        this.totalPeriodExpense = (total != null && total.compareTo(BigDecimal.ZERO) > 0) ? total : BigDecimal.ONE;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<CategoryExpenseAggregate> DIFF_CALLBACK = new DiffUtil.ItemCallback<CategoryExpenseAggregate>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryExpenseAggregate oldItem, @NonNull CategoryExpenseAggregate newItem) {
            return oldItem.getCategoryId() != null && oldItem.getCategoryId().equals(newItem.getCategoryId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryExpenseAggregate oldItem, @NonNull CategoryExpenseAggregate newItem) {
            return oldItem.getTotalAmount().compareTo(newItem.getTotalAmount()) == 0 &&
                    oldItem.getTransactionCount() == newItem.getTransactionCount();
        }
    };

    @NonNull
    @Override
    public BreakdownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBreakdownBinding binding = ItemCategoryBreakdownBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BreakdownViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BreakdownViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BreakdownViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBreakdownBinding binding;

        public BreakdownViewHolder(@NonNull ItemCategoryBreakdownBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoryExpenseAggregate item) {
            binding.tvBreakdownCatName.setText(item.getCategoryName());
            binding.tvBreakdownAmount.setText(CurrencyHelper.formatRupiah(item.getTotalAmount()));
            binding.tvBreakdownTxCount.setText(String.format("%d Transaksi", item.getTransactionCount()));

            int percentage = 0;
            if (totalPeriodExpense.compareTo(BigDecimal.ZERO) > 0) {
                percentage = item.getTotalAmount().multiply(BigDecimal.valueOf(100))
                        .divide(totalPeriodExpense, 0, RoundingMode.HALF_UP).intValue();
            }
            binding.tvBreakdownPercentage.setText(String.format("%d%%", percentage));
            binding.progressBreakdown.setProgress(percentage);

            try {
                int color = Color.parseColor(item.getCategoryColor());
                binding.cardBreakdownColor.setCardBackgroundColor(color);
                binding.progressBreakdown.setIndicatorColor(color);
            } catch (Exception e) {
                binding.cardBreakdownColor.setCardBackgroundColor(Color.parseColor("#1E56A0"));
            }
        }
    }
}
