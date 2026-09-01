package com.example.app_marifin_javadroid.presentation.document;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.repository.DocumentRepository;
import com.example.app_marifin_javadroid.databinding.BottomSheetUploadDocumentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

/**
 * Bottom Sheet Dialog Fragment supporting Drag & Drop and File Picker for document uploads.
 */
public class UploadDocumentBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetUploadDocumentBinding binding;
    private DocumentViewModel documentViewModel;
    private Uri selectedFileUri;
    private String selectedFileName;
    private long selectedFileSize = 0;
    private String selectedMimeType = "application/octet-stream";

    private final String[] docTypeDisplay = {"Struk / Kuitansi", "Invoice", "Rekening Koran", "Lainnya"};
    private final String[] docTypeKeys = {"receipt", "invoice", "bank_statement", "other"};
    private int selectedTypeIndex = 0;

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processSelectedUri(uri);
                }
            }
    );

    public static UploadDocumentBottomSheet newInstance() {
        return new UploadDocumentBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetUploadDocumentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        documentViewModel = new ViewModelProvider(requireActivity()).get(DocumentViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, docTypeDisplay);
        binding.actvDocType.setAdapter(adapter);
        binding.actvDocType.setText(docTypeDisplay[0], false);

        binding.actvDocType.setOnItemClickListener((parent, v, position, id) -> {
            selectedTypeIndex = position;
        });

        binding.layoutDropZone.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        setupDragAndDrop();

        binding.btnRemovePreview.setOnClickListener(v -> clearFileSelection());
        binding.btnCancelUpload.setOnClickListener(v -> dismiss());
        binding.btnConfirmUpload.setOnClickListener(v -> saveDocument());
    }

    private void setupDragAndDrop() {
        binding.layoutDropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    binding.layoutDropZone.setAlpha(0.7f);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    binding.layoutDropZone.setAlpha(1.0f);
                    return true;
                case DragEvent.ACTION_DROP:
                    binding.layoutDropZone.setAlpha(1.0f);
                    ClipData clipData = event.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri uri = clipData.getItemAt(0).getUri();
                        if (uri != null) {
                            processSelectedUri(uri);
                        }
                    }
                    return true;
            }
            return false;
        });
    }

    private void processSelectedUri(@NonNull Uri uri) {
        this.selectedFileUri = uri;
        String mime = requireContext().getContentResolver().getType(uri);
        this.selectedMimeType = mime != null ? mime : "application/octet-stream";

        // Query file name and size
        Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) selectedFileName = cursor.getString(nameIndex);
                if (sizeIndex >= 0) selectedFileSize = cursor.getLong(sizeIndex);
            }
            cursor.close();
        }

        if (selectedFileName == null) {
            selectedFileName = "doc_" + System.currentTimeMillis();
        }

        if (selectedFileSize > DocumentRepository.MAX_FILE_SIZE_BYTES) {
            Toast.makeText(requireContext(), "Ukuran file melebihi batas 5MB.", Toast.LENGTH_LONG).show();
            clearFileSelection();
            return;
        }

        // Show preview below the upload form
        binding.cardFilePreview.setVisibility(View.VISIBLE);
        binding.tvPreviewFileName.setText(selectedFileName);

        String sizeStr;
        if (selectedFileSize >= 1024 * 1024) {
            sizeStr = String.format(Locale.US, "%.1f MB", selectedFileSize / (1024.0 * 1024.0));
        } else {
            sizeStr = String.format(Locale.US, "%d KB", Math.max(1, selectedFileSize / 1024));
        }
        binding.tvPreviewFileSize.setText(sizeStr);
    }

    private void clearFileSelection() {
        selectedFileUri = null;
        selectedFileName = null;
        selectedFileSize = 0;
        binding.cardFilePreview.setVisibility(View.GONE);
    }

    private void saveDocument() {
        if (selectedFileUri == null) {
            Toast.makeText(requireContext(), "Silakan pilih atau tarik file terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Copy file to local internal storage
            File vaultDir = new File(requireContext().getFilesDir(), "vault");
            if (!vaultDir.exists()) vaultDir.mkdirs();

            File destinationFile = new File(vaultDir, System.currentTimeMillis() + "_" + selectedFileName);
            InputStream is = requireContext().getContentResolver().openInputStream(selectedFileUri);
            if (is != null) {
                FileOutputStream fos = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
                fos.close();
                is.close();
            }

            DocumentEntity entity = new DocumentEntity();
            entity.setOriginalName(selectedFileName);
            entity.setStoragePath(destinationFile.getAbsolutePath());
            entity.setMimeType(selectedMimeType);
            entity.setFileSize(destinationFile.length());
            entity.setDocumentType(docTypeKeys[selectedTypeIndex]);

            documentViewModel.saveDocument(entity, resource -> {
                if (resource.getStatus() == Resource.Status.SUCCESS) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Dokumen berhasil disimpan!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Gagal memproses file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
