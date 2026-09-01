package com.example.app_marifin_javadroid.presentation.ai;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.databinding.ActivityFingptBinding;

/**
 * Activity for interacting with the FinGPT AI Financial Assistant.
 */
public class FinGptActivity extends BaseActivity<ActivityFingptBinding> {

    private AiChatViewModel chatViewModel;
    private ChatMessageAdapter chatAdapter;

    @NonNull
    @Override
    protected ActivityFingptBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityFingptBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        chatViewModel = new ViewModelProvider(this).get(AiChatViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        chatAdapter = new ChatMessageAdapter(draft -> {
            DraftTransactionPreviewDialog dialog = DraftTransactionPreviewDialog.newInstance(draft);
            dialog.show(getSupportFragmentManager(), "draft_tx_preview_dialog");
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(chatAdapter);

        binding.btnSendChat.setOnClickListener(v -> submitMessage());

        binding.chipPromptCashflow.setOnClickListener(v -> {
            binding.etChatInput.setText("Bagaimana kondisi arus kas saya?");
            submitMessage();
        });

        binding.chipPromptBudget.setOnClickListener(v -> {
            binding.etChatInput.setText("Apakah ada anggaran yang over budget?");
            submitMessage();
        });

        binding.chipPromptTips.setOnClickListener(v -> {
            binding.etChatInput.setText("Beri tips hemat pengeluaran");
            submitMessage();
        });
    }

    @Override
    protected void setupObservers() {
        chatViewModel.getMessages().observe(this, list -> {
            if (list != null) {
                chatAdapter.submitList(list, () -> {
                    if (!list.isEmpty()) {
                        binding.rvChatMessages.scrollToPosition(list.size() - 1);
                    }
                });
            }
        });

        chatViewModel.getIsTyping().observe(this, isTyping -> {
            binding.tvTypingIndicator.setVisibility(isTyping != null && isTyping ? View.VISIBLE : View.GONE);
        });
    }

    private void submitMessage() {
        String text = binding.etChatInput.getText() != null ? binding.etChatInput.getText().toString().trim() : "";
        if (TextUtils.isEmpty(text)) return;

        chatViewModel.sendMessage(text);
        binding.etChatInput.setText("");
    }
}
