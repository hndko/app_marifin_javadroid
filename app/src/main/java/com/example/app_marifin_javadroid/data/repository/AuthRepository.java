package com.example.app_marifin_javadroid.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.app_marifin_javadroid.core.common.Resource;
import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.example.app_marifin_javadroid.data.remote.api.RetrofitClient;
import com.example.app_marifin_javadroid.data.remote.api.SupabaseAuthApi;
import com.example.app_marifin_javadroid.data.remote.dto.AuthRequest;
import com.example.app_marifin_javadroid.data.remote.dto.AuthResponse;
import com.example.app_marifin_javadroid.data.remote.dto.ProfileDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for managing Authentication and User Profile operations.
 */
public class AuthRepository {

    private static volatile AuthRepository INSTANCE;
    private final SupabaseAuthApi authApi;
    private final SecureSessionManager sessionManager;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public interface RepositoryCallback<T> {
        void onResult(Resource<T> result);
    }

    public AuthRepository(@NonNull Context context) {
        this.authApi = RetrofitClient.getInstance(context).getAuthApi();
        this.sessionManager = SecureSessionManager.getInstance(context);
    }

    public static AuthRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (AuthRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public boolean isOnboardingCompleted() {
        return sessionManager.isOnboardingCompleted();
    }

    public void setOnboardingCompleted(boolean completed) {
        sessionManager.setOnboardingCompleted(completed);
    }

    public SecureSessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Executes login with email and password.
     */
    public void login(@NonNull String email, @NonNull String password, @NonNull RepositoryCallback<AuthResponse> callback) {
        executor.execute(() -> {
            try {
                AuthRequest request = new AuthRequest(email, password);
                Call<AuthResponse> call = authApi.signInWithPassword(request);
                Response<AuthResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    String fullName = authResponse.getUser() != null ? authResponse.getUser().getFullName() : "";
                    String userId = authResponse.getUser() != null ? authResponse.getUser().getId() : "";

                    sessionManager.saveSession(
                            authResponse.getAccessToken(),
                            authResponse.getRefreshToken(),
                            userId,
                            email,
                            fullName
                    );

                    callback.onResult(Resource.success(authResponse));
                } else {
                    String errorMsg = parseErrorMessage(response, response.code() == 400 ? "Email atau password salah." : "Gagal masuk. Kode: " + response.code());
                    callback.onResult(Resource.error(errorMsg));
                }
            } catch (Exception e) {
                callback.onResult(Resource.error("Koneksi gagal: " + (e.getMessage() != null ? e.getMessage() : "Periksa jaringan internet.")));
            }
        });
    }

    /**
     * Executes user registration with full name metadata.
     */
    public void register(@NonNull String email, @NonNull String password, @NonNull String fullName,
                         @NonNull RepositoryCallback<AuthResponse> callback) {
        executor.execute(() -> {
            try {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("full_name", fullName);

                AuthRequest request = new AuthRequest(email, password, metadata);
                Call<AuthResponse> call = authApi.signUp(request);
                Response<AuthResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    String userId = authResponse.getUser() != null ? authResponse.getUser().getId() : "";

                    if (authResponse.getAccessToken() != null) {
                        sessionManager.saveSession(
                                authResponse.getAccessToken(),
                                authResponse.getRefreshToken(),
                                userId,
                                email,
                                fullName
                        );
                    }

                    callback.onResult(Resource.success(authResponse));
                } else {
                    String errorMsg = parseErrorMessage(response, "Pendaftaran gagal (Kode " + response.code() + "). Silakan coba lagi.");
                    callback.onResult(Resource.error(errorMsg));
                }
            } catch (Exception e) {
                callback.onResult(Resource.error("Koneksi gagal: " + (e.getMessage() != null ? e.getMessage() : "Periksa jaringan internet.")));
            }
        });
    }

    private String parseErrorMessage(Response<?> response, String fallback) {
        if (response != null && response.errorBody() != null) {
            try {
                String errorJson = response.errorBody().string();
                if (errorJson.contains("User already registered") || errorJson.contains("user_already_exists")) {
                    return "Email sudah terdaftar. Silakan gunakan email lain atau langsung Masuk.";
                } else if (errorJson.contains("Password should be at least")) {
                    return "Kata sandi terlalu pendek. Minimal 6 karakter.";
                } else if (errorJson.contains("Invalid login credentials") || errorJson.contains("invalid_grant")) {
                    return "Email atau kata sandi tidak cocok.";
                } else if (errorJson.contains("Email rate limit exceeded")) {
                    return "Terlalu banyak percobaan. Harap tunggu beberapa saat.";
                }
            } catch (Exception ignored) {}
        }
        return fallback;
    }

    /**
     * Sends password recovery email.
     */
    public void forgotPassword(@NonNull String email, @NonNull RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                AuthRequest request = new AuthRequest(email, "");
                Call<Void> call = authApi.recoverPassword(request);
                Response<Void> response = call.execute();

                if (response.isSuccessful()) {
                    callback.onResult(Resource.success(null));
                } else {
                    callback.onResult(Resource.error("Gagal mengirim email reset password."));
                }
            } catch (Exception e) {
                callback.onResult(Resource.error("Koneksi internet bermasalah. Coba lagi."));
            }
        });
    }

    /**
     * Logs out the user and clears session tokens.
     */
    public void logout(@NonNull RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            String token = sessionManager.getAccessToken();
            if (token != null) {
                try {
                    authApi.logout("Bearer " + token).execute();
                } catch (Exception ignored) {
                    // Ignore network error on logout
                }
            }
            sessionManager.clearSession();
            callback.onResult(Resource.success(null));
        });
    }

    /**
     * Gets user profile from Supabase PostgREST or local cache.
     */
    public void getProfile(@NonNull RepositoryCallback<ProfileDto> callback) {
        executor.execute(() -> {
            String token = sessionManager.getAccessToken();
            String userId = sessionManager.getUserId();

            if (token == null || userId == null) {
                callback.onResult(Resource.error("Sesi telah berakhir. Silakan login kembali."));
                return;
            }

            try {
                Call<List<ProfileDto>> call = authApi.getProfile("Bearer " + token, "eq." + userId);
                Response<List<ProfileDto>> response = call.execute();

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ProfileDto profile = response.body().get(0);
                    sessionManager.updateProfile(profile.getFullName(), profile.getAvatarUrl(), profile.getPhone());
                    callback.onResult(Resource.success(profile));
                } else {
                    // Fallback to session cache
                    ProfileDto localProfile = new ProfileDto(userId, sessionManager.getFullName(), sessionManager.getPhone());
                    callback.onResult(Resource.success(localProfile));
                }
            } catch (Exception e) {
                // Fallback to local session on offline
                ProfileDto localProfile = new ProfileDto(userId, sessionManager.getFullName(), sessionManager.getPhone());
                callback.onResult(Resource.success(localProfile));
            }
        });
    }
}
