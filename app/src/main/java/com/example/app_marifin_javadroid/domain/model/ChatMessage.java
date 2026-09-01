package com.example.app_marifin_javadroid.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.UUID;

/**
 * Domain model representing a chat message between User and FinGPT Assistant.
 */
public class ChatMessage {

    public enum Sender {
        USER,
        FINGPT
    }

    private final String id;
    private final Sender sender;
    private final String content;
    private final Date timestamp;
    private final DraftTransaction draftTransaction;

    public ChatMessage(@NonNull Sender sender, @NonNull String content) {
        this(UUID.randomUUID().toString(), sender, content, new Date(), null);
    }

    public ChatMessage(@NonNull Sender sender, @NonNull String content, @Nullable DraftTransaction draftTransaction) {
        this(UUID.randomUUID().toString(), sender, content, new Date(), draftTransaction);
    }

    public ChatMessage(String id, Sender sender, String content, Date timestamp, DraftTransaction draftTransaction) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.draftTransaction = draftTransaction;
    }

    public String getId() { return id; }
    public Sender getSender() { return sender; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public DraftTransaction getDraftTransaction() { return draftTransaction; }
    public boolean hasDraftTransaction() { return draftTransaction != null; }
}
