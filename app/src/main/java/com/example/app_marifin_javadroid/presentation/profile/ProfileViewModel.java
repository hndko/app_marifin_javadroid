package com.example.app_marifin_javadroid.presentation.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.data.remote.dto.ProfileDto;
import com.example.app_marifin_javadroid.data.repository.AuthRepository;

/**
 * ViewModel for managing user profile screen, preferences, and logout.
 */
public class ProfileViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<ProfileDto>> _profileResult = new MutableLiveData<>();
    public final LiveData<Resource<ProfileDto>> profileResult = _profileResult;

    private final MutableLiveData<Resource<Void>> _logoutResult = new MutableLiveData<>();
    public final LiveData<Resource<Void>> logoutResult = _logoutResult;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = AuthRepository.getInstance(application);
    }

    public void loadProfile() {
        _profileResult.setValue(Resource.loading());
        authRepository.getProfile(result -> _profileResult.postValue(result));
    }

    public void logout() {
        _logoutResult.setValue(Resource.loading());
        authRepository.logout(result -> _logoutResult.postValue(result));
    }
}
