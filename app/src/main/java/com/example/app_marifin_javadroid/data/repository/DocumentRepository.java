package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.core.utils.NetworkHelper;
import com.example.app_marifin_javadroid.data.local.AppDatabase;
import com.example.app_marifin_javadroid.data.local.dao.DocumentDao;
import com.example.app_marifin_javadroid.data.local.entity.DocumentEntity;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseDataApi;
import com.example.app_marifin_javadroid.data.remote.dto.DocumentDto;
import com.example.app_marifin_javadroid.data.remote.mapper.DocumentMapper;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Repository coordinating Document Vault persistence, file security validation, and cloud sync.
 */
public class DocumentRepository {

    public static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    public static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "application/pdf"
    ));

    private static volatile DocumentRepository INSTANCE;
    private final DocumentDao documentDao;
    private final SupabaseDataApi dataApi;
    private final SecureSessionManager sessionManager;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> resource);
    }

    private DocumentRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.documentDao = db.documentDao();
        this.dataApi = RetrofitClient.getInstance(this.context).getDataApi();
        this.sessionManager = SecureSessionManager.getInstance(this.context);
    }

    public static DocumentRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DocumentRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DocumentRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<DocumentEntity>> getDocumentsLiveData() {
        String userId = sessionManager.getUserId();
        return documentDao.getDocumentsLiveData(userId != null ? userId : "");
    }

    public void refreshDocuments(RepositoryCallback<List<DocumentEntity>> callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (callback != null) callback.onResult(Resource.error("User belum terautentikasi."));
            return;
        }

        executor.execute(() -> {
            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    Response<List<DocumentDto>> response = dataApi.getDocuments("eq." + userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        List<DocumentEntity> entities = DocumentMapper.toEntityList(response.body());
                        documentDao.insertAll(entities);
                        if (callback != null) callback.onResult(Resource.success(entities));
                        return;
                    }
                } catch (Exception ignored) {}
            }
            List<DocumentEntity> local = documentDao.getDocumentsSync(userId);
            if (callback != null) callback.onResult(Resource.success(local));
        });
    }

    public void saveDocument(@NonNull DocumentEntity document, RepositoryCallback<DocumentEntity> callback) {
        String userId = sessionManager.getUserId();
        if (userId != null) document.setUserId(userId);

        if (document.getFileSize() > MAX_FILE_SIZE_BYTES) {
            if (callback != null) callback.onResult(Resource.error("Ukuran file melebihi batas 5MB."));
            return;
        }

        executor.execute(() -> {
            documentDao.insert(document);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    DocumentDto dto = DocumentMapper.toDto(document);
                    if (dto != null) dataApi.insertDocument(dto).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(document));
        });
    }

    public void deleteDocument(@NonNull DocumentEntity document, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            documentDao.delete(document);

            if (NetworkHelper.isNetworkAvailable(context)) {
                try {
                    dataApi.deleteDocument("eq." + document.getId()).execute();
                } catch (Exception ignored) {}
            }

            if (callback != null) callback.onResult(Resource.success(null));
        });
    }
}
