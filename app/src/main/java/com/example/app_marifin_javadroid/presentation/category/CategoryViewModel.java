package com.example.app_marifin_javadroid.presentation.category;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.data.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing Categories UI state and custom category creation.
 */
public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;

    private final MutableLiveData<Resource<CategoryEntity>> _operationResult = new MutableLiveData<>();
    public final LiveData<Resource<CategoryEntity>> operationResult = _operationResult;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        this.categoryRepository = CategoryRepository.getInstance(application);
    }

    public CategoryViewModel(@NonNull Application application, @NonNull CategoryRepository repository) {
        super(application);
        this.categoryRepository = repository;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categoryRepository.getCategoriesLiveData();
    }

    public LiveData<List<CategoryEntity>> getCategoriesByType(String type) {
        return categoryRepository.getCategoriesByTypeLiveData(type);
    }

    public void refreshCategories() {
        categoryRepository.refreshCategories(null);
    }

    public void saveCategory(String id, String name, String type, String icon, String color) {
        if (name == null || name.trim().isEmpty()) {
            _operationResult.setValue(Resource.error("Nama kategori wajib diisi."));
            return;
        }

        boolean isNew = (id == null || id.trim().isEmpty());
        String categoryId = isNew ? UUID.randomUUID().toString() : id;

        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);
        category.setName(name.trim());
        category.setType(type != null ? type : "expense");
        category.setIcon(icon != null ? icon : "ic_category_default");
        category.setColor(color != null ? color : "#1E56A0");
        category.setDefault(false);
        category.setFavorite(false);
        category.setActive(true);

        _operationResult.setValue(Resource.loading());
        categoryRepository.saveCategory(category, isNew, result -> _operationResult.postValue(result));
    }

    public void deleteCategory(CategoryEntity category) {
        if (category == null) return;
        if (category.isDefault()) {
            _operationResult.setValue(Resource.error("Kategori sistem bawaan tidak dapat dihapus."));
            return;
        }

        _operationResult.setValue(Resource.loading());
        categoryRepository.deleteCategory(category, result -> {
            if (result.isSuccess()) {
                _operationResult.postValue(Resource.success(category));
            } else {
                _operationResult.postValue(Resource.error(result.getMessage() != null ? result.getMessage() : "Gagal menghapus kategori."));
            }
        });
    }
}
