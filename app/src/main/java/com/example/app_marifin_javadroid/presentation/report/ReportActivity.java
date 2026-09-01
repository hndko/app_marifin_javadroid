package com.example.app_marifin_javadroid.presentation.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.databinding.ActivityReportBinding;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Activity displaying aggregated Financial Reports and CSV export actions.
 */
public class ReportActivity extends BaseActivity<ActivityReportBinding> {

    private ReportViewModel reportViewModel;
    private final List<TransactionEntity> transactionList = new ArrayList<>();
    private FinancialReportData currentReport;

    @NonNull
    @Override
    protected ActivityReportBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityReportBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.tabReportPeriod.addTab(binding.tabReportPeriod.newTab().setText("Mingguan"));
        binding.tabReportPeriod.addTab(binding.tabReportPeriod.newTab().setText("Bulanan"), true);
        binding.tabReportPeriod.addTab(binding.tabReportPeriod.newTab().setText("Tahunan"));

        binding.tabReportPeriod.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                calculateAndRenderReport();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnExportCsv.setOnClickListener(v -> exportReportAsCsv());
    }

    @Override
    protected void setupObservers() {
        reportViewModel.getTransactions().observe(this, list -> {
            transactionList.clear();
            if (list != null) {
                transactionList.addAll(list);
            }
            calculateAndRenderReport();
        });
    }

    private void calculateAndRenderReport() {
        int selectedTab = binding.tabReportPeriod.getSelectedTabPosition();
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        Date startDate;
        String periodLabel;

        if (selectedTab == 0) {
            // Weekly: last 7 days
            cal.add(Calendar.DAY_OF_YEAR, -7);
            startDate = cal.getTime();
            periodLabel = "Mingguan";
        } else if (selectedTab == 2) {
            // Yearly: current year
            cal.set(Calendar.DAY_OF_YEAR, 1);
            startDate = cal.getTime();
            periodLabel = "Tahunan";
        } else {
            // Monthly: current month
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = cal.getTime();
            periodLabel = "Bulanan";
        }

        currentReport = reportViewModel.generateReport(periodLabel, startDate, endDate, transactionList);

        binding.tvReportDateRange.setText(String.format("Periode %s: %s", periodLabel, currentReport.getDateRangeLabel()));
        binding.tvReportTotalIncome.setText(String.format("+%s", CurrencyHelper.formatRupiah(currentReport.getTotalIncome())));
        binding.tvReportTotalExpense.setText(String.format("-%s", CurrencyHelper.formatRupiah(currentReport.getTotalExpense())));
        binding.tvReportNetCashFlow.setText(CurrencyHelper.formatRupiah(currentReport.getNetCashFlow()));
        binding.tvReportSavingsRate.setText(String.format("%d%%", currentReport.getSavingsRate()));
    }

    private void exportReportAsCsv() {
        if (currentReport == null) {
            Toast.makeText(this, "Data laporan belum siap.", Toast.LENGTH_SHORT).show();
            return;
        }

        String csvData = reportViewModel.generateCsv(currentReport, transactionList);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/csv");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan MariFin - " + currentReport.getPeriodLabel());
        sendIntent.putExtra(Intent.EXTRA_TEXT, csvData);

        startActivity(Intent.createChooser(sendIntent, "Bagikan Laporan CSV"));
    }
}
