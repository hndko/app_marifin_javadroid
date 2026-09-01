package com.example.app_marifin_javadroid.presentation.bill;

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
import com.example.app_marifin_javadroid.data.local.entity.BillEntity;
import com.example.app_marifin_javadroid.databinding.ItemBillBinding;

import java.util.Date;

/**
 * RecyclerView Adapter for Bill items.
 */
public class BillAdapter extends ListAdapter<BillEntity, BillAdapter.BillViewHolder> {

    public interface OnBillActionListener {
        void onPayBill(BillEntity bill);
        void onDeleteBill(BillEntity bill);
    }

    private final OnBillActionListener listener;

    public BillAdapter(OnBillActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<BillEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<BillEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull BillEntity oldItem, @NonNull BillEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BillEntity oldItem, @NonNull BillEntity newItem) {
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getAmount().compareTo(newItem.getAmount()) == 0;
        }
    };

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBillBinding binding = ItemBillBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BillViewHolder extends RecyclerView.ViewHolder {
        private final ItemBillBinding binding;

        public BillViewHolder(@NonNull ItemBillBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BillEntity item) {
            binding.tvBillName.setText(item.getName());
            binding.tvBillAmount.setText(CurrencyHelper.formatRupiah(item.getAmount()));
            binding.tvBillDueDate.setText(String.format("Jatuh tempo: %s", DateHelper.formatDisplayShort(item.getDueDate())));

            boolean isPaid = "paid".equalsIgnoreCase(item.getStatus());
            boolean isOverdue = !isPaid && item.getDueDate() != null && item.getDueDate().before(new Date());

            if (isPaid) {
                binding.tvBillStatusBadge.setText("Sudah Bayar");
                binding.tvBillStatusBadge.setTextColor(Color.parseColor("#10B981"));
                binding.btnPayBill.setVisibility(View.GONE);
            } else if (isOverdue) {
                binding.tvBillStatusBadge.setText("Terlambat");
                binding.tvBillStatusBadge.setTextColor(Color.parseColor("#EF4444"));
                binding.btnPayBill.setVisibility(View.VISIBLE);
            } else {
                binding.tvBillStatusBadge.setText("Belum Bayar");
                binding.tvBillStatusBadge.setTextColor(Color.parseColor("#F59E0B"));
                binding.btnPayBill.setVisibility(View.VISIBLE);
            }

            binding.btnPayBill.setOnClickListener(v -> {
                if (listener != null) listener.onPayBill(item);
            });

            binding.btnBillDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteBill(item);
            });
        }
    }
}
