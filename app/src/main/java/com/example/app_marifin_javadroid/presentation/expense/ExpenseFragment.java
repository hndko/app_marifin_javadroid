package com.example.app_marifin_javadroid.presentation.expense;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseFragment;
import com.example.app_marifin_javadroid.core.utils.CurrencyHelper;
import com.example.app_marifin_javadroid.core.utils.DateHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.local.model.CategoryExpenseAggregate;
import com.example.app_marifin_javadroid.databinding.FragmentExpenseBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Expense Analytics Dashboard Fragment featuring 6-Month Bar Chart and Category Allocation Donut Chart.
 */
public class ExpenseFragment extends BaseFragment<FragmentExpenseBinding> {

    private ExpenseViewModel expenseViewModel;
    private CategoryBreakdownAdapter breakdownAdapter;
    private int selectedPeriodIndex = 0; // 0: Bulan Ini, 1: 3 Bulan, 2: 6 Bulan, 3: Tahun Ini

    @NonNull
    @Override
    protected FragmentExpenseBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentExpenseBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Setup Period Tabs
        binding.tabExpensePeriod.addTab(binding.tabExpensePeriod.newTab().setText("Bulan Ini"));
        binding.tabExpensePeriod.addTab(binding.tabExpensePeriod.newTab().setText("3 Bulan"));
        binding.tabExpensePeriod.addTab(binding.tabExpensePeriod.newTab().setText("6 Bulan"));
        binding.tabExpensePeriod.addTab(binding.tabExpensePeriod.newTab().setText("Tahun Ini"));

        binding.tabExpensePeriod.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedPeriodIndex = tab.getPosition();
                loadExpenseData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup Breakdown RecyclerView
        breakdownAdapter = new CategoryBreakdownAdapter();
        binding.rvCategoryBreakdown.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategoryBreakdown.setAdapter(breakdownAdapter);

        setupBarChart();
        setupPieChart();

        binding.swipeRefreshExpense.setOnRefreshListener(() -> {
            expenseViewModel.refreshData();
            binding.swipeRefreshExpense.setRefreshing(false);
        });

        loadExpenseData();
    }

    @Override
    protected void setupObservers() {
        expenseViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                updateBarChart(transactions);
            }
        });
    }

    private void loadExpenseData() {
        Date startDate = getStartDateForPeriod(selectedPeriodIndex);
        Date endDate = new Date();

        expenseViewModel.getCategoryExpenseBreakdown(startDate, endDate).observe(getViewLifecycleOwner(), aggregates -> {
            if (aggregates != null && !aggregates.isEmpty()) {
                BigDecimal totalExpense = BigDecimal.ZERO;
                for (CategoryExpenseAggregate a : aggregates) {
                    totalExpense = totalExpense.add(a.getTotalAmount());
                }

                binding.tvExpenseTotal.setText(CurrencyHelper.formatRupiah(totalExpense));
                breakdownAdapter.setTotalPeriodExpense(totalExpense);
                breakdownAdapter.submitList(aggregates);

                updatePieChart(aggregates, totalExpense);

                binding.rvCategoryBreakdown.setVisibility(View.VISIBLE);
                binding.tvExpenseEmpty.setVisibility(View.GONE);
            } else {
                binding.tvExpenseTotal.setText("Rp0");
                breakdownAdapter.submitList(null);
                binding.pieChartCategories.clear();
                binding.pieChartCategories.setCenterText("Tidak Ada Data");
                binding.rvCategoryBreakdown.setVisibility(View.GONE);
                binding.tvExpenseEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private Date getStartDateForPeriod(int periodIndex) {
        Calendar cal = Calendar.getInstance();
        if (periodIndex == 0) {
            // Bulan Ini
            return DateHelper.getStartOfMonth(new Date());
        } else if (periodIndex == 1) {
            // 3 Bulan
            cal.add(Calendar.MONTH, -3);
            return cal.getTime();
        } else if (periodIndex == 2) {
            // 6 Bulan
            cal.add(Calendar.MONTH, -6);
            return cal.getTime();
        } else {
            // Tahun Ini
            cal.set(Calendar.DAY_OF_YEAR, 1);
            return cal.getTime();
        }
    }

    private void setupBarChart() {
        BarChart barChart = binding.barChartExpense;
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_600));

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_600));
        barChart.getAxisRight().setEnabled(false);
    }

    private void updateBarChart(List<TransactionEntity> transactions) {
        BarChart barChart = binding.barChartExpense;
        List<BarEntry> entries = new ArrayList<>();
        List<String> months = new ArrayList<>();

        // Generate past 6 months data buckets
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            Calendar mCal = Calendar.getInstance();
            mCal.add(Calendar.MONTH, -i);
            String monthName = new java.text.SimpleDateFormat("MMM", DateHelper.LOCALE_ID).format(mCal.getTime());
            months.add(monthName);

            BigDecimal sum = BigDecimal.ZERO;
            for (TransactionEntity tx : transactions) {
                if ("expense".equalsIgnoreCase(tx.getType())) {
                    Calendar txCal = Calendar.getInstance();
                    txCal.setTime(tx.getTransactionDate());
                    if (txCal.get(Calendar.YEAR) == mCal.get(Calendar.YEAR) &&
                            txCal.get(Calendar.MONTH) == mCal.get(Calendar.MONTH)) {
                        sum = sum.add(tx.getAmount());
                    }
                }
            }
            entries.add(new BarEntry(5 - i, sum.floatValue()));
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));

        BarDataSet dataSet = new BarDataSet(entries, "Pengeluaran");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
        dataSet.setValueTextSize(9f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        barChart.setData(data);
        barChart.animateY(800);
        barChart.invalidate();
    }

    private void setupPieChart() {
        PieChart pieChart = binding.pieChartCategories;
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(55f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setCenterText("Alokasi");
        pieChart.setCenterTextSize(12f);
        pieChart.getLegend().setEnabled(false);
    }

    private void updatePieChart(List<CategoryExpenseAggregate> aggregates, BigDecimal total) {
        PieChart pieChart = binding.pieChartCategories;
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (CategoryExpenseAggregate a : aggregates) {
            entries.add(new PieEntry(a.getTotalAmount().floatValue(), a.getCategoryName()));
            try {
                colors.add(Color.parseColor(a.getCategoryColor()));
            } catch (Exception e) {
                colors.add(Color.parseColor("#1E56A0"));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setCenterText("Total\n" + CurrencyHelper.formatRupiah(total));
        pieChart.animateY(800);
        pieChart.invalidate();
    }
}
