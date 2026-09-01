package com.example.app_marifin_javadroid.presentation.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.databinding.ActivityDocumentVaultBinding;

/**
 * Activity displaying the Receipt & Document Vault with upload and preview capabilities.
 */
public class DocumentVaultActivity extends BaseActivity<ActivityDocumentVaultBinding> {

    private DocumentViewModel documentViewModel;
    private DocumentAdapter documentAdapter;

    @NonNull
    @Override
    protected ActivityDocumentVaultBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityDocumentVaultBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        documentViewModel = new ViewModelProvider(this).get(DocumentViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        documentAdapter = new DocumentAdapter(doc -> {
            new AlertDialog.Builder(DocumentVaultActivity.this)
                    .setTitle("Hapus Dokumen")
                    .setMessage(String.format("Yakin ingin menghapus dokumen '%s'?", doc.getOriginalName()))
                    .setPositiveButton(R.string.action_delete, (d, w) -> {
                        documentViewModel.deleteDocument(doc, resource -> {
                            runOnUiThread(() -> Toast.makeText(DocumentVaultActivity.this, "Dokumen dihapus.", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        });

        binding.rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDocuments.setAdapter(documentAdapter);

        binding.fabUploadDoc.setOnClickListener(v -> showUploadBottomSheet());
        binding.layoutEmptyState.btnEmptyAction.setOnClickListener(v -> showUploadBottomSheet());

        binding.layoutEmptyState.ivEmptyIcon.setImageResource(R.drawable.ic_document);
        binding.layoutEmptyState.tvEmptyTitle.setText("Belum Ada Dokumen");
        binding.layoutEmptyState.tvEmptyDesc.setText("Simpan kuitansi, struk belanja, atau invoice transaksi Anda dengan aman.");
        binding.layoutEmptyState.btnEmptyAction.setText("[+] Upload Dokumen");

        binding.swipeRefreshDocuments.setOnRefreshListener(() -> {
            documentViewModel.refreshDocuments();
            binding.swipeRefreshDocuments.setRefreshing(false);
        });

        documentViewModel.refreshDocuments();
    }

    private void showUploadBottomSheet() {
        UploadDocumentBottomSheet bottomSheet = UploadDocumentBottomSheet.newInstance();
        bottomSheet.show(getSupportFragmentManager(), "upload_doc_sheet");
    }

    @Override
    protected void setupObservers() {
        documentViewModel.getDocuments().observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                documentAdapter.submitList(list);
                binding.rvDocuments.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.getRoot().setVisibility(View.GONE);
            } else {
                documentAdapter.submitList(null);
                binding.rvDocuments.setVisibility(View.GONE);
                binding.layoutEmptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
