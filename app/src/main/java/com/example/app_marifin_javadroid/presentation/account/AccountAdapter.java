package com.example.app_marifin_javadroid.presentation.account;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.AccountEntity;
import com.example.app_marifin_javadroid.databinding.ItemAccountBinding;

/**
 * RecyclerView Adapter for Financial Accounts list.
 */
public class AccountAdapter extends ListAdapter<AccountEntity, AccountAdapter.AccountViewHolder> {

    public interface OnAccountActionListener {
        void onEdit(AccountEntity account);
        void onDelete(AccountEntity account);
    }

    private final OnAccountActionListener actionListener;

    public AccountAdapter(OnAccountActionListener actionListener) {
        super(DIFF_CALLBACK);
        this.actionListener = actionListener;
    }

    private static final DiffUtil.ItemCallback<AccountEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<AccountEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull AccountEntity oldItem, @NonNull AccountEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AccountEntity oldItem, @NonNull AccountEntity newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCurrentBalance().compareTo(newItem.getCurrentBalance()) == 0 &&
                    oldItem.getInstitutionName().equals(newItem.getInstitutionName());
        }
    };

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAccountBinding binding = ItemAccountBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AccountViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.bind(getItem(position), position + 1);
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private final ItemAccountBinding binding;

        public AccountViewHolder(@NonNull ItemAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AccountEntity account, int sequenceNumber) {
            binding.tvAccountNumberSeq.setText(String.format("%d.", sequenceNumber));
            binding.tvAccountName.setText(account.getName());

            String masked = account.getAccountNumberMasked() != null && !account.getAccountNumberMasked().isEmpty()
                    ? " • " + account.getAccountNumberMasked() : "";
            binding.tvAccountTypeNumber.setText(String.format("%s (%s)%s", account.getAccountType(), account.getInstitutionName(), masked));

            binding.tvAccountBalance.setText(CurrencyHelper.formatRupiah(account.getCurrentBalance()));

            // Set icon based on account type
            if ("E-Wallet".equalsIgnoreCase(account.getAccountType())) {
                binding.ivAccountIcon.setImageResource(R.drawable.ic_wallet_card);
            } else if ("Cash".equalsIgnoreCase(account.getAccountType())) {
                binding.ivAccountIcon.setImageResource(R.drawable.ic_cash);
            } else {
                binding.ivAccountIcon.setImageResource(R.drawable.ic_bank);
            }

            binding.btnAccountMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add(0, 1, 0, "Ubah Rekening");
                popup.getMenu().add(0, 2, 1, "Hapus Rekening");

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1 && actionListener != null) {
                        actionListener.onEdit(account);
                        return true;
                    } else if (item.getItemId() == 2 && actionListener != null) {
                        actionListener.onDelete(account);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
