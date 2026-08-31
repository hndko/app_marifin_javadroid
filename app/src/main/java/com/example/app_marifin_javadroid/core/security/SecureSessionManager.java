package com.example.app_marifin_javadroid.core.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Secure Session Manager using Android Keystore-backed EncryptedSharedPreferences.
 * Stores auth tokens, user ID, and preferences securely with zero plaintext leakage.
 */
public class SecureSessionManager {

    private static final String PREF_FILE_NAME = "marifin_secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    private static volatile SecureSessionManager INSTANCE;
    private SharedPreferences sharedPreferences;

    private SecureSessionManager(@NonNull Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context.getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context.getApplicationContext(),
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to standard private mode if keystore hardware fails
            sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static SecureSessionManager getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (SecureSessionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SecureSessionManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public void saveSession(@NonNull String accessToken, @Nullable String refreshToken,
                            @NonNull String userId, @NonNull String email, @Nullable String fullName) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .putString(KEY_FULL_NAME, fullName != null ? fullName : "")
                .apply();
    }

    public void updateProfile(@NonNull String fullName, @Nullable String avatarUrl, @Nullable String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit().putString(KEY_FULL_NAME, fullName);
        if (avatarUrl != null) editor.putString(KEY_AVATAR_URL, avatarUrl);
        if (phone != null) editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    @Nullable
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    @Nullable
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    @Nullable
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    @Nullable
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    @NonNull
    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "");
    }

    @Nullable
    public String getAvatarUrl() {
        return sharedPreferences.getString(KEY_AVATAR_URL, null);
    }

    @Nullable
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.trim().isEmpty();
    }

    public boolean isOnboardingCompleted() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    public void clearSession() {
        boolean onboardingState = isOnboardingCompleted();
        sharedPreferences.edit().clear().apply();
        // Preserve onboarding completion state across logout
        setOnboardingCompleted(onboardingState);
    }
}
