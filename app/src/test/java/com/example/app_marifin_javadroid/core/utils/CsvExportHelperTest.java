package com.example.app_marifin_javadroid.core.utils;

import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for CsvExportHelper.
 */
public class CsvExportHelperTest {

    @Test
    public void testGenerateReportCsv() {
        FinancialReportData report = new FinancialReportData(
                "Bulanan",
                "01 Agu 2026 - 31 Agu 2026",
                new Date(),
                new Date(),
                new BigDecimal("10000000"),
                new BigDecimal("4000000"),
                new BigDecimal("6000000"),
                60,
                new BigDecimal("129032"),
                2
        );

        List<TransactionEntity> transactions = new ArrayList<>();
        TransactionEntity tx1 = new TransactionEntity();
        tx1.setId("tx-1");
        tx1.setType("income");
        tx1.setAmount(new BigDecimal("10000000"));
        tx1.setDescription("Gaji Bulanan");
        tx1.setTransactionDate(new Date());
        transactions.add(tx1);

        TransactionEntity tx2 = new TransactionEntity();
        tx2.setId("tx-2");
        tx2.setType("expense");
        tx2.setAmount(new BigDecimal("4000000"));
        tx2.setDescription("Sewa Rumah");
        tx2.setTransactionDate(new Date());
        transactions.add(tx2);

        String csv = CsvExportHelper.generateReportCsv(report, transactions);

        assertNotNull(csv);
        assertTrue(csv.contains("LAPORAN KEUANGAN MARIFIN"));
        assertTrue(csv.contains("Total Pemasukan,10000000"));
        assertTrue(csv.contains("Total Pengeluaran,4000000"));
        assertTrue(csv.contains("Arus Kas Bersih,6000000"));
        assertTrue(csv.contains("Gaji Bulanan"));
        assertTrue(csv.contains("Sewa Rumah"));
    }
}
