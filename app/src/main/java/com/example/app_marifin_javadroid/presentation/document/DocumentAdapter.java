package com.example.app_marifin_javadroid.presentation.document;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.databinding.ItemDocumentBinding;

import java.util.Locale;

/**
 * RecyclerView Adapter for Document Vault files.
 */
public class DocumentAdapter extends ListAdapter<DocumentEntity, DocumentAdapter.DocumentViewHolder> {

    public interface OnDocumentActionListener {
        void onDeleteDocument(DocumentEntity document);
    }

    private final OnDocumentActionListener listener;

    public DocumentAdapter(OnDocumentActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<DocumentEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<DocumentEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull DocumentEntity oldItem, @NonNull DocumentEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DocumentEntity oldItem, @NonNull DocumentEntity newItem) {
            return oldItem.getOriginalName().equals(newItem.getOriginalName()) &&
                    oldItem.getFileSize() == newItem.getFileSize();
        }
    };

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder {
        private final ItemDocumentBinding binding;

        public DocumentViewHolder(@NonNull ItemDocumentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DocumentEntity item) {
            binding.tvDocName.setText(item.getOriginalName());

            String typeStr = "receipt".equalsIgnoreCase(item.getDocumentType()) ? "Struk" :
                    "invoice".equalsIgnoreCase(item.getDocumentType()) ? "Invoice" :
                    "bank_statement".equalsIgnoreCase(item.getDocumentType()) ? "Rekening Koran" : "Lainnya";
            binding.tvDocTypeBadge.setText(typeStr);

            String sizeStr;
            if (item.getFileSize() >= 1024 * 1024) {
                sizeStr = String.format(Locale.US, "%.1f MB", item.getFileSize() / (1024.0 * 1024.0));
            } else {
                sizeStr = String.format(Locale.US, "%d KB", Math.max(1, item.getFileSize() / 1024));
            }

            String dateStr = DateHelper.formatDisplayShort(item.getCreatedAt());
            binding.tvDocSizeAndDate.setText(String.format("%s • %s", sizeStr, dateStr));

            binding.btnDocDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteDocument(item);
            });
        }
    }
}
