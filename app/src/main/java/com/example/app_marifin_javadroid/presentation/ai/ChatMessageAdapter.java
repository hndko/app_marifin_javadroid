package com.example.app_marifin_javadroid.presentation.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_marifin_javadroid.databinding.ItemChatMessageBinding;
import com.example.app_marifin_javadroid.domain.model.ChatMessage;
import com.example.app_marifin_javadroid.domain.model.DraftTransaction;

/**
 * RecyclerView Adapter for displaying FinGPT chat messages and draft transaction preview buttons.
 */
public class ChatMessageAdapter extends ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder> {

    public interface OnDraftClickListener {
        void onDraftClick(DraftTransaction draft);
    }

    private final OnDraftClickListener draftClickListener;

    public ChatMessageAdapter(OnDraftClickListener draftClickListener) {
        super(DIFF_CALLBACK);
        this.draftClickListener = draftClickListener;
    }

    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getSender() == newItem.getSender();
        }
    };

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChatMessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        public ChatMessageViewHolder(@NonNull ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatMessage message) {
            if (message.getSender() == ChatMessage.Sender.USER) {
                binding.layoutUserBubble.setVisibility(View.VISIBLE);
                binding.layoutBotBubble.setVisibility(View.GONE);
                binding.tvUserMessage.setText(message.getContent());
            } else {
                binding.layoutUserBubble.setVisibility(View.GONE);
                binding.layoutBotBubble.setVisibility(View.VISIBLE);
                binding.tvBotMessage.setText(message.getContent());

                if (message.hasDraftTransaction()) {
                    binding.btnPreviewDraft.setVisibility(View.VISIBLE);
                    binding.btnPreviewDraft.setOnClickListener(v -> {
                        if (draftClickListener != null) {
                            draftClickListener.onDraftClick(message.getDraftTransaction());
                        }
                    });
                } else {
                    binding.btnPreviewDraft.setVisibility(View.GONE);
                }
            }
        }
    }
}
