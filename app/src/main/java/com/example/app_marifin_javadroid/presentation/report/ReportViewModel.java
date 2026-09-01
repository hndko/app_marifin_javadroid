package com.example.app_marifin_javadroid.presentation.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.utils.CsvExportHelper;
import com.example.app_marifin_javadroid.data.local.entity.TransactionEntity;
import com.example.app_marifin_javadroid.data.repository.TransactionRepository;
import com.example.app_marifin_javadroid.domain.model.FinancialReportData;
import com.example.app_marifin_javadroid.domain.usecase.GenerateFinancialReportUseCase;

import java.util.Date;
import java.util.List;

/**
 * ViewModel managing Financial Report calculations and CSV exports.
 */
public class ReportViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final GenerateFinancialReportUseCase generateFinancialReportUseCase = new GenerateFinancialReportUseCase();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        this.transactionRepository = TransactionRepository.getInstance(application);
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactionsLiveData();
    }

    public FinancialReportData generateReport(String periodLabel, Date startDate, Date endDate, List<TransactionEntity> transactions) {
        return generateFinancialReportUseCase.execute(periodLabel, startDate, endDate, transactions);
    }

    public String generateCsv(FinancialReportData report, List<TransactionEntity> transactions) {
        return CsvExportHelper.generateReportCsv(report, transactions);
    }
}
