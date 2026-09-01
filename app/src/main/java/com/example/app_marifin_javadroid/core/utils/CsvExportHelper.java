package com.example.app_marifin_javadroid.core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;

import java.util.List;

/**
 * Utility to generate standard CSV formatted strings for financial report exports.
 */
public final class CsvExportHelper {

    private CsvExportHelper() {}

    @NonNull
    public static String generateReportCsv(@NonNull FinancialReportData report,
                                           @Nullable List<TransactionEntity> transactions) {
        StringBuilder sb = new StringBuilder();

        // 1. Header Summary
        sb.append("LAPORAN KEUANGAN MARIFIN\n");
        sb.append("Periode,").append(escapeCsv(report.getPeriodLabel())).append("\n");
        sb.append("Rentang Tanggal,").append(escapeCsv(report.getDateRangeLabel())).append("\n");
        sb.append("Total Pemasukan,").append(report.getTotalIncome().toPlainString()).append("\n");
        sb.append("Total Pengeluaran,").append(report.getTotalExpense().toPlainString()).append("\n");
        sb.append("Arus Kas Bersih,").append(report.getNetCashFlow().toPlainString()).append("\n");
        sb.append("Tingkat Tabungan (%),").append(report.getSavingsRate()).append("%\n");
        sb.append("Rata-rata Pengeluaran Harian,").append(report.getAvgDailyExpense().toPlainString()).append("\n\n");

        // 2. Transaction Records Table
        sb.append("ID,Tanggal,Tipe,Nominal (IDR),Merchant/Tempat,Catatan\n");

        if (transactions != null) {
            for (TransactionEntity tx : transactions) {
                String dateStr = tx.getTransactionDate() != null ? DateHelper.formatToIsoUtc(tx.getTransactionDate()) : "";
                String typeStr = tx.getType() != null ? tx.getType() : "unknown";
                String amountStr = tx.getAmount() != null ? tx.getAmount().toPlainString() : "0";
                String merchantStr = tx.getMerchant() != null ? tx.getMerchant() : "";
                String descStr = tx.getDescription() != null ? tx.getDescription() : "";

                sb.append(escapeCsv(tx.getId())).append(",")
                        .append(escapeCsv(dateStr)).append(",")
                        .append(escapeCsv(typeStr)).append(",")
                        .append(amountStr).append(",")
                        .append(escapeCsv(merchantStr)).append(",")
                        .append(escapeCsv(descStr)).append("\n");
            }
        }

        return sb.toString();
    }

    @NonNull
    private static String escapeCsv(@Nullable String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
