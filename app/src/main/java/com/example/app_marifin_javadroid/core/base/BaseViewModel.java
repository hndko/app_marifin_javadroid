package com.example.app_marifin_javadroid.core.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base ViewModel providing shared background executor and generic loading/error state LiveData.
 */
public abstract class BaseViewModel extends ViewModel {

    protected final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(4);

    protected final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    protected final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public final LiveData<String> errorMessage = _errorMessage;

    protected void setLoading(boolean loading) {
        _isLoading.postValue(loading);
    }

    protected void setError(String message) {
        _errorMessage.postValue(message);
    }

    protected void clearError() {
        _errorMessage.postValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
    }
}
