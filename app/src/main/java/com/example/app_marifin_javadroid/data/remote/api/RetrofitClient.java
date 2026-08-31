package com.example.app_marifin_javadroid.data.remote.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.app_marifin_javadroid.core.security.SecureSessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit Network Client Singleton with Supabase apikey/token interceptors and timeouts.
 */
public class RetrofitClient {

    // Default Supabase Endpoint (can be loaded via BuildConfig or local config)
    public static final String DEFAULT_BASE_URL = "https://your-project.supabase.co/";
    public static final String DEFAULT_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.your-anon-key";

    private static volatile RetrofitClient INSTANCE;
    private final Retrofit retrofit;
    private final SupabaseAuthApi authApi;
    private final SupabaseDataApi dataApi;

    private RetrofitClient(@NonNull Context context) {
        SecureSessionManager sessionManager = SecureSessionManager.getInstance(context);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("apikey", DEFAULT_ANON_KEY);

            // If request doesn't have an Authorization header and user is logged in, attach token
            String existingAuth = original.header("Authorization");
            if (existingAuth == null && sessionManager.isLoggedIn()) {
                String token = sessionManager.getAccessToken();
                if (token != null) {
                    builder.header("Authorization", "Bearer " + token);
                }
            }

            return chain.proceed(builder.build());
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        authApi = retrofit.create(SupabaseAuthApi.class);
        dataApi = retrofit.create(SupabaseDataApi.class);
    }

    public static RetrofitClient getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (RetrofitClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitClient(context);
                }
            }
        }
        return INSTANCE;
    }

    public SupabaseAuthApi getAuthApi() {
        return authApi;
    }

    public SupabaseDataApi getDataApi() {
        return dataApi;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
