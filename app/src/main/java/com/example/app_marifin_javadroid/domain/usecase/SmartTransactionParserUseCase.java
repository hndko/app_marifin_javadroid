package com.example.app_marifin_javadroid.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.domain.model.DraftTransaction;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Domain Use Case to parse free text / bank mutation messages into a structured DraftTransaction.
 */
public class SmartTransactionParserUseCase {

    @Nullable
    public DraftTransaction execute(@NonNull String text) {
        if (text.trim().isEmpty()) return null;

        String lower = text.toLowerCase().trim();

        // 1. Parse Amount
        BigDecimal amount = extractAmount(lower);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            // Cannot reliably identify a transaction without an amount
            return null;
        }

        // 2. Parse Type
        String type = "expense";
        if (lower.contains("masuk") || lower.contains("gaji") || lower.contains("terima") ||
                lower.contains("dapat") || lower.contains("cashback") || lower.contains("penjualan")) {
            type = "income";
        } else if (lower.contains("transfer ke") || lower.contains("kirim ke") || lower.contains("pindah dana")) {
            type = "transfer";
        }

        // 3. Predict Category & Merchant
        String predictedCategory = predictCategory(lower);
        String merchant = extractMerchant(lower);

        // 4. Parse Date
        Date transactionDate = new Date();
        if (lower.contains("kemarin")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            transactionDate = cal.getTime();
        }

        DraftTransaction draft = new DraftTransaction(
                type,
                amount,
                text,
                merchant,
                predictedCategory,
                transactionDate,
                text
        );

        return draft;
    }

    @Nullable
    private BigDecimal extractAmount(String text) {
        // Pattern for "1.5jt", "2jt", "1,5 jt"
        Pattern jtPattern = Pattern.compile("(\\d+([.,]\\d+)?)\\s*(jt|juta)");
        Matcher jtMatcher = jtPattern.matcher(text);
        if (jtMatcher.find()) {
            try {
                String numStr = jtMatcher.group(1).replace(",", ".");
                double num = Double.parseDouble(numStr);
                return BigDecimal.valueOf((long) (num * 1_000_000));
            } catch (Exception ignored) {}
        }

        // Pattern for "50rb", "50k", "25,5 rb"
        Pattern rbPattern = Pattern.compile("(\\d+([.,]\\d+)?)\\s*(rb|k|ribu)");
        Matcher rbMatcher = rbPattern.matcher(text);
        if (rbMatcher.find()) {
            try {
                String numStr = rbMatcher.group(1).replace(",", ".");
                double num = Double.parseDouble(numStr);
                return BigDecimal.valueOf((long) (num * 1_000));
            } catch (Exception ignored) {}
        }

        // Pattern for "Rp 50.000", "Rp. 50000", "50.000", "50000"
        Pattern numPattern = Pattern.compile("(?:rp\\.?\\s*)?(\\d{1,3}(?:[.]\\d{3})+|\\d{4,})");
        Matcher numMatcher = numPattern.matcher(text);
        if (numMatcher.find()) {
            try {
                String cleanNum = numMatcher.group(1).replace(".", "");
                return new BigDecimal(cleanNum);
            } catch (Exception ignored) {}
        }

        return null;
    }

    private String predictCategory(String text) {
        if (text.contains("kopi") || text.contains("makan") || text.contains("mcd") ||
                text.contains("kfc") || text.contains("warteg") || text.contains("resto") ||
                text.contains("cafe") || text.contains("grabfood") || text.contains("gofood") ||
                text.contains("bakso") || text.contains("mie") || text.contains("nasi")) {
            return "Makanan & Minuman";
        }
        if (text.contains("bensin") || text.contains("spbu") || text.contains("pertamina") ||
                text.contains("shell") || text.contains("gojek") || text.contains("grab") ||
                text.contains("tol") || text.contains("parkir") || text.contains("ojek")) {
            return "Transportasi";
        }
        if (text.contains("alfamart") || text.contains("indomaret") || text.contains("shopee") ||
                text.contains("tokopedia") || text.contains("supermarket") || text.contains("belanja") ||
                text.contains("mall")) {
            return "Belanja Bulanan";
        }
        if (text.contains("pln") || text.contains("listrik") || text.contains("pdam") ||
                text.contains("air") || text.contains("wifi") || text.contains("indihome") ||
                text.contains("netflix") || text.contains("bpjs") || text.contains("pulsa")) {
            return "Tagihan & Utilitas";
        }
        if (text.contains("bioskop") || text.contains("xxi") || text.contains("game") ||
                text.contains("spotify") || text.contains("steam") || text.contains("liburan")) {
            return "Hiburan & Rekreasi";
        }
        if (text.contains("gaji") || text.contains("bonus") || text.contains("thr") || text.contains("dividen")) {
            return "Gaji & Pendapatan";
        }
        return "Lainnya";
    }

    private String extractMerchant(String text) {
        String[] merchants = {
                "Alfamart", "Indomaret", "Pertamina", "Shell", "Kopi Kenangan", "Starbucks",
                "Janji Jiwa", "Shopee", "Tokopedia", "GrabFood", "GoFood", "McD", "KFC",
                "Indihome", "PLN", "Steam", "Spotify", "Netflix"
        };
        for (String m : merchants) {
            if (text.contains(m.toLowerCase())) {
                return m;
            }
        }
        return "Umum";
    }
}
