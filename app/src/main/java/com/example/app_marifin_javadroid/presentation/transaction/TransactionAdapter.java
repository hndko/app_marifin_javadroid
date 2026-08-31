package com.example.app_marifin_javadroid.presentation.transaction;

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
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.databinding.ItemTransactionBinding;

/**
 * RecyclerView Adapter for displaying Transaction items.
 */
public class TransactionAdapter extends ListAdapter<TransactionEntity, TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(TransactionEntity transaction);
    }

    private final OnTransactionClickListener clickListener;

    public TransactionAdapter(OnTransactionClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<TransactionEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<TransactionEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull TransactionEntity oldItem, @NonNull TransactionEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TransactionEntity oldItem, @NonNull TransactionEntity newItem) {
            return oldItem.getAmount().compareTo(newItem.getAmount()) == 0 &&
                    oldItem.getType().equals(newItem.getType()) &&
                    oldItem.getTransactionDate().equals(newItem.getTransactionDate());
        }
    };

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(getItem(position), position + 1);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        public TransactionViewHolder(@NonNull ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TransactionEntity tx, int sequenceNumber) {
            binding.tvTxNumberSeq.setText(String.format("%d.", sequenceNumber));

            String title = tx.getDescription() != null && !tx.getDescription().isEmpty()
                    ? tx.getDescription()
                    : ("expense".equalsIgnoreCase(tx.getType()) ? "Pengeluaran" : "income".equalsIgnoreCase(tx.getType()) ? "Pemasukan" : "Transfer Antar Rekening");
            binding.tvTxTitle.setText(title);

            String formattedDate = DateHelper.formatDisplayDateTime(tx.getTransactionDate());
            binding.tvTxSubtitle.setText(formattedDate);

            if ("income".equalsIgnoreCase(tx.getType())) {
                binding.tvTxAmount.setText("+" + CurrencyHelper.formatRupiah(tx.getAmount()));
                binding.tvTxAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.income_green));
                binding.ivTxIcon.setImageResource(R.drawable.ic_arrow_upward);
                binding.ivTxIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.income_green));
                binding.cardTxIconContainer.setCardBackgroundColor(Color.parseColor("#E6F4EA"));
            } else if ("transfer".equalsIgnoreCase(tx.getType())) {
                binding.tvTxAmount.setText("⇄ " + CurrencyHelper.formatRupiah(tx.getAmount()));
                binding.tvTxAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                binding.ivTxIcon.setImageResource(R.drawable.ic_transfer);
                binding.ivTxIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.primary));
                binding.cardTxIconContainer.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_container));
            } else {
                // Expense
                binding.tvTxAmount.setText("-" + CurrencyHelper.formatRupiah(tx.getAmount()));
                binding.tvTxAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.expense_red));
                binding.ivTxIcon.setImageResource(R.drawable.ic_arrow_downward);
                binding.ivTxIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.expense_red));
                binding.cardTxIconContainer.setCardBackgroundColor(Color.parseColor("#FCE8E6"));
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTransactionClick(tx);
                }
            });
        }
    }
}
