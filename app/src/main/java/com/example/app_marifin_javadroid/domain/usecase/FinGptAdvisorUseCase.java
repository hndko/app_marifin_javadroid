package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;

import com.example.app_marifin_javadroid.domain.model.ChatMessage;
import com.example.app_marifin_javadroid.domain.model.DraftTransaction;

/**
 * Domain Use Case to generate FinGPT AI responses, insights, and parsed transaction drafts.
 */
public class FinGptAdvisorUseCase {

    private final SmartTransactionParserUseCase parserUseCase = new SmartTransactionParserUseCase();

    @NonNull
    public ChatMessage processUserPrompt(@NonNull String userPrompt) {
        String lower = userPrompt.toLowerCase().trim();

        // 1. Check if the prompt describes a transaction to record
        DraftTransaction draft = parserUseCase.execute(userPrompt);
        if (draft != null) {
            String botReply = "Saya menemukan data transaksi dari pesan Anda:\n\n" +
                    "• **Tipe**: " + ("income".equals(draft.getType()) ? "Pemasukan" : "Pengeluaran") + "\n" +
                    "• **Nominal**: Rp " + draft.getAmount().toPlainString() + "\n" +
                    "• **Kategori**: " + draft.getPredictedCategoryName() + "\n" +
                    "• **Merchant/Tempat**: " + draft.getMerchant() + "\n\n" +
                    "Silakan periksa dan klik tombol **Konfirmasi & Simpan** di bawah untuk memasukkannya ke catatan keuangan Anda.";
            return new ChatMessage(ChatMessage.Sender.FINGPT, botReply, draft);
        }

        // 2. Financial Advisory Prompts
        if (lower.contains("arus kas") || lower.contains("cash flow") || lower.contains("kondisi keuangan")) {
            String advice = "📊 **Analisis Arus Kas Anda**:\n\n" +
                    "Arus kas positif tercipta jika pemasukan Anda lebih besar dari total pengeluaran bulanan.\n\n" +
                    "💡 **Rekomendasi MariFin**:\n" +
                    "1. Sisihkan minimal **20%** dari total pemasukan di awal bulan untuk dana darurat/tabungan impian.\n" +
                    "2. Batasi pengeluaran gaya hidup (*wants*) maksimal **30%**.\n" +
                    "3. Pantau menu **Laporan Keuangan** untuk evaluasi berkala.";
            return new ChatMessage(ChatMessage.Sender.FINGPT, advice);
        }

        if (lower.contains("budget") || lower.contains("anggaran") || lower.contains("over budget") || lower.contains("boros")) {
            String advice = "⚠️ **Manajemen Anggaran & Deteksi Pengeluaran**:\n\n" +
                    "MariFin menerapkan sistem **4-Zona Alert** pada setiap anggaran:\n" +
                    "• 🟢 **Aman**: Terpakai < 70%\n" +
                    "• 🟡 **Waspada**: Terpakai 70% - 89%\n" +
                    "• 🟠 **Kritis**: Terpakai 90% - 99%\n" +
                    "• 🔴 **Over Budget**: Terpakai ≥ 100%\n\n" +
                    "💡 **Tips**: Jika suatu kategori sering over budget, pertimbangkan untuk menyesuaikan limit atau mengurangi frekuensi jajan.";
            return new ChatMessage(ChatMessage.Sender.FINGPT, advice);
        }

        if (lower.contains("hemat") || lower.contains("tips") || lower.contains("cara menabung")) {
            String advice = "🎯 **Tips Cerdas Mengelola Keuangan (FinGPT)**:\n\n" +
                    "1. **Terapkan Rumus 50/30/20**: 50% Kebutuhan Pokok, 30% Keinginan, 20% Tabungan/Investasi.\n" +
                    "2. **Buat Target Tabungan (*Goals*)**: Menabung dengan tujuan spesifik (seperti Dana Darurat) 3x lebih konsisten berhasil.\n" +
                    "3. **Gunakan Fitur Pelacak Tagihan**: Hindari denda keterlambatan dengan membayar tagihan rutin tepat waktu.";
            return new ChatMessage(ChatMessage.Sender.FINGPT, advice);
        }

        // Default greeting / guidance
        String defaultReply = "Halo! Saya **FinGPT**, asisten finansial pribadi Anda di MariFin.\n\n" +
                "Anda bisa bertanya tentang saran anggaran, tips hemat, atau langsung mencatat transaksi dengan mengetik pesan seperti:\n" +
                "_\"Beli bensin 50rb di Pertamina kemarin\"_ atau _\"Makan siang McD 45rb\"_.";
        return new ChatMessage(ChatMessage.Sender.FINGPT, defaultReply);
    }
}
