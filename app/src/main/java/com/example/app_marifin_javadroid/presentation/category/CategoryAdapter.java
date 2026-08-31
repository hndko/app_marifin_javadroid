package com.example.app_marifin_javadroid.presentation.category;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.databinding.ItemCategoryBinding;

/**
 * RecyclerView Adapter for Category list.
 */
public class CategoryAdapter extends ListAdapter<CategoryEntity, CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryActionListener {
        void onDelete(CategoryEntity category);
    }

    private final OnCategoryActionListener actionListener;

    public CategoryAdapter(OnCategoryActionListener actionListener) {
        super(DIFF_CALLBACK);
        this.actionListener = actionListener;
    }

    private static final DiffUtil.ItemCallback<CategoryEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<CategoryEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryEntity oldItem, @NonNull CategoryEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryEntity oldItem, @NonNull CategoryEntity newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getType().equals(newItem.getType()) &&
                    oldItem.getColor().equals(newItem.getColor());
        }
    };

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(getItem(position), position + 1);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoryEntity category, int sequenceNumber) {
            binding.tvCategoryNumberSeq.setText(String.format("%d.", sequenceNumber));
            binding.tvCategoryName.setText(category.getName());

            String typeLabel = "expense".equalsIgnoreCase(category.getType()) ? "Pengeluaran"
                    : "income".equalsIgnoreCase(category.getType()) ? "Pemasukan" : "Umum";
            String defaultLabel = category.isDefault() ? "Bawaan Sistem" : "Kustom";
            binding.tvCategoryTypeBadge.setText(String.format("%s • %s", typeLabel, defaultLabel));

            try {
                int color = Color.parseColor(category.getColor());
                binding.cardCategoryColor.setCardBackgroundColor(color);
            } catch (Exception e) {
                binding.cardCategoryColor.setCardBackgroundColor(Color.parseColor("#1E56A0"));
            }

            if (category.isDefault()) {
                binding.btnCategoryMenu.setVisibility(View.GONE);
            } else {
                binding.btnCategoryMenu.setVisibility(View.VISIBLE);
                binding.btnCategoryMenu.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.getMenu().add(0, 1, 0, "Hapus Kategori");
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == 1 && actionListener != null) {
                            actionListener.onDelete(category);
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                });
            }
        }
    }
}
