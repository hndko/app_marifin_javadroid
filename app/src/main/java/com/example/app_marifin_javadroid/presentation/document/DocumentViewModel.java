package com.example.app_marifin_javadroid.presentation.document;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.repository.DocumentRepository;

import java.util.List;

/**
 * ViewModel managing Document Vault state, uploads, and deletions.
 */
public class DocumentViewModel extends AndroidViewModel {

    private final DocumentRepository documentRepository;

    public DocumentViewModel(@NonNull Application application) {
        super(application);
        this.documentRepository = DocumentRepository.getInstance(application);
    }

    public LiveData<List<DocumentEntity>> getDocuments() {
        return documentRepository.getDocumentsLiveData();
    }

    public void saveDocument(DocumentEntity document, DocumentRepository.RepositoryCallback<DocumentEntity> callback) {
        documentRepository.saveDocument(document, callback);
    }

    public void deleteDocument(DocumentEntity document, DocumentRepository.RepositoryCallback<Void> callback) {
        documentRepository.deleteDocument(document, callback);
    }

    public void refreshDocuments() {
        documentRepository.refreshDocuments(null);
    }
}
