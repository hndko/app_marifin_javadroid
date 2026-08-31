package com.example.app_marifin_javadroid.presentation.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.utils.Validator;
import com.example.app_marifin_javadroid.data.remote.dto.AuthResponse;
import com.example.app_marifin_javadroid.data.repository.AuthRepository;

/**
 * ViewModel for handling Authentication UI logic and input validation.
 */
public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<AuthResponse>> _authResult = new MutableLiveData<>();
    public final LiveData<Resource<AuthResponse>> authResult = _authResult;

    private final MutableLiveData<Resource<Void>> _forgotPasswordResult = new MutableLiveData<>();
    public final LiveData<Resource<Void>> forgotPasswordResult = _forgotPasswordResult;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = AuthRepository.getInstance(application);
    }

    public AuthViewModel(@NonNull Application application, @NonNull AuthRepository authRepository) {
        super(application);
        this.authRepository = authRepository;
    }

    public void login(String email, String password) {
        if (!Validator.isValidEmail(email)) {
            _authResult.setValue(Resource.error("Format email tidak valid."));
            return;
        }

        if (!Validator.isValidPassword(password)) {
            _authResult.setValue(Resource.error("Password minimal 8 karakter."));
            return;
        }

        _authResult.setValue(Resource.loading());
        authRepository.login(email, password, result -> _authResult.postValue(result));
    }

    public void register(String email, String password, String confirmation, String fullName) {
        if (!Validator.isValidName(fullName)) {
            _authResult.setValue(Resource.error("Nama lengkap minimal 2 karakter."));
            return;
        }

        if (!Validator.isValidEmail(email)) {
            _authResult.setValue(Resource.error("Format email tidak valid."));
            return;
        }

        if (!Validator.isValidPassword(password)) {
            _authResult.setValue(Resource.error("Password minimal 8 karakter."));
            return;
        }

        if (!Validator.doPasswordsMatch(password, confirmation)) {
            _authResult.setValue(Resource.error("Konfirmasi password tidak cocok."));
            return;
        }

        _authResult.setValue(Resource.loading());
        authRepository.register(email, password, fullName, result -> _authResult.postValue(result));
    }

    public void forgotPassword(String email) {
        if (!Validator.isValidEmail(email)) {
            _forgotPasswordResult.setValue(Resource.error("Format email tidak valid."));
            return;
        }

        _forgotPasswordResult.setValue(Resource.loading());
        authRepository.forgotPassword(email, result -> _forgotPasswordResult.postValue(result));
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public boolean isOnboardingCompleted() {
        return authRepository.isOnboardingCompleted();
    }
}
