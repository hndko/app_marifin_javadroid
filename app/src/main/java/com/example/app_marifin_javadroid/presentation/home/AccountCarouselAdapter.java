package com.example.app_marifin_javadroid.presentation.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.databinding.ItemAccountCarouselCardBinding;

/**
 * RecyclerView Adapter for Horizontal Account Carousel on Home screen.
 */
public class AccountCarouselAdapter extends ListAdapter<AccountEntity, AccountCarouselAdapter.CarouselViewHolder> {

    public interface OnAccountClickListener {
        void onAccountClick(AccountEntity account);
    }

    private final OnAccountClickListener clickListener;
    private boolean isBalanceVisible = true;

    public AccountCarouselAdapter(OnAccountClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    public void setBalanceVisible(boolean visible) {
        this.isBalanceVisible = visible;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<AccountEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<AccountEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull AccountEntity oldItem, @NonNull AccountEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AccountEntity oldItem, @NonNull AccountEntity newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCurrentBalance().compareTo(newItem.getCurrentBalance()) == 0;
        }
    };

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAccountCarouselCardBinding binding = ItemAccountCarouselCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CarouselViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CarouselViewHolder extends RecyclerView.ViewHolder {
        private final ItemAccountCarouselCardBinding binding;

        public CarouselViewHolder(@NonNull ItemAccountCarouselCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AccountEntity account) {
            binding.tvCarouselAccName.setText(account.getName());
            binding.tvCarouselAccInstitution.setText(account.getInstitutionName());

            if (isBalanceVisible) {
                binding.tvCarouselAccBalance.setText(CurrencyHelper.formatRupiah(account.getCurrentBalance()));
            } else {
                binding.tvCarouselAccBalance.setText("••••••••");
            }

            if ("E-Wallet".equalsIgnoreCase(account.getAccountType())) {
                binding.ivCarouselAccIcon.setImageResource(R.drawable.ic_wallet_card);
            } else if ("Cash".equalsIgnoreCase(account.getAccountType())) {
                binding.ivCarouselAccIcon.setImageResource(R.drawable.ic_cash);
            } else {
                binding.ivCarouselAccIcon.setImageResource(R.drawable.ic_bank);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAccountClick(account);
                }
            });
        }
    }
}
