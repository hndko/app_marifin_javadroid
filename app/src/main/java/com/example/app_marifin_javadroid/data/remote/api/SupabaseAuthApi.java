package com.example.app_marifin_javadroid.data.remote.api;

import com.example.app_marifin_javadroid.data.remote.dto.AuthRequest;
import com.example.app_marifin_javadroid.data.remote.dto.AuthResponse;
import com.example.app_marifin_javadroid.data.remote.dto.ProfileDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit Interface for Supabase GoTrue Auth and Profiles PostgREST endpoints.
 */
public interface SupabaseAuthApi {

    @Headers({"Content-Type: application/json"})
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body AuthRequest request);

    @Headers({"Content-Type: application/json"})
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signInWithPassword(@Body AuthRequest request);

    @Headers({"Content-Type: application/json"})
    @POST("auth/v1/recover")
    Call<Void> recoverPassword(@Body AuthRequest request);

    @POST("auth/v1/logout")
    Call<Void> logout(@Header("Authorization") String bearerToken);

    @GET("auth/v1/user")
    Call<AuthResponse.UserDto> getUser(@Header("Authorization") String bearerToken);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/profiles")
    Call<List<ProfileDto>> upsertProfile(
            @Header("Authorization") String bearerToken,
            @Body ProfileDto profile
    );

    @GET("rest/v1/profiles")
    Call<List<ProfileDto>> getProfile(
            @Header("Authorization") String bearerToken,
            @Query("id") String userIdFilter // e.g. "eq.<uuid>"
    );
}
