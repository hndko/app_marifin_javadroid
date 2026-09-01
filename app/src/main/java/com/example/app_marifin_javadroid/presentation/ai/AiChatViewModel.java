package com.example.app_marifin_javadroid.presentation.ai;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.domain.model.ChatMessage;
import com.example.app_marifin_javadroid.domain.usecase.FinGptAdvisorUseCase;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel managing the FinGPT AI Assistant conversation messages and smart parser triggers.
 */
public class AiChatViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ChatMessage>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isTypingLiveData = new MutableLiveData<>(false);
    private final FinGptAdvisorUseCase finGptAdvisorUseCase = new FinGptAdvisorUseCase();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AiChatViewModel(@NonNull Application application) {
        super(application);
        // Welcome message
        List<ChatMessage> initial = new ArrayList<>();
        initial.add(new ChatMessage(
                ChatMessage.Sender.FINGPT,
                "Halo! Saya **FinGPT**, asisten AI finansial Anda.\n\n" +
                        "Saya bisa membantu menganalisis pengeluaran, memberi saran anggaran, atau mencatat transaksi otomatis cukup dari pesan teks biasa (contoh: _\"Makan siang McD 45rb\"_).\n\n" +
                        "Ada yang bisa saya bantu hari ini?"
        ));
        messagesLiveData.setValue(initial);
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<Boolean> getIsTyping() {
        return isTypingLiveData;
    }

    public void sendMessage(@NonNull String text) {
        if (text.trim().isEmpty()) return;

        List<ChatMessage> current = messagesLiveData.getValue() != null ? new ArrayList<>(messagesLiveData.getValue()) : new ArrayList<>();
        current.add(new ChatMessage(ChatMessage.Sender.USER, text.trim()));
        messagesLiveData.setValue(current);

        isTypingLiveData.setValue(true);

        // Realistic typing response delay
        handler.postDelayed(() -> {
            ChatMessage botReply = finGptAdvisorUseCase.processUserPrompt(text);
            List<ChatMessage> updated = messagesLiveData.getValue() != null ? new ArrayList<>(messagesLiveData.getValue()) : new ArrayList<>();
            updated.add(botReply);
            messagesLiveData.setValue(updated);
            isTypingLiveData.setValue(false);
        }, 600);
    }
}
