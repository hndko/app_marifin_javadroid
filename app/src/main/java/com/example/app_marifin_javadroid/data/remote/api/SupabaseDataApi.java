package com.example.app_marifin_javadroid.data.remote.api;

import com.example.app_marifin_javadroid.data.remote.dto.AccountDto;
import com.example.app_marifin_javadroid.data.remote.dto.CategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit Interface for PostgREST Financial Accounts and Categories endpoints.
 */
public interface SupabaseDataApi {

    // Financial Accounts
    @GET("rest/v1/financial_accounts?order=name.asc")
    Call<List<AccountDto>> getAccounts(@Query("user_id") String userIdFilter);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/financial_accounts")
    Call<List<AccountDto>> insertAccount(@Body AccountDto account);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/financial_accounts")
    Call<List<AccountDto>> updateAccount(@Query("id") String idFilter, @Body AccountDto account);

    @DELETE("rest/v1/financial_accounts")
    Call<Void> deleteAccount(@Query("id") String idFilter);

    // Categories
    @GET("rest/v1/categories?order=name.asc")
    Call<List<CategoryDto>> getCategories(@Query("or") String filter);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/categories")
    Call<List<CategoryDto>> insertCategory(@Body CategoryDto category);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/categories")
    Call<List<CategoryDto>> updateCategory(@Query("id") String idFilter, @Body CategoryDto category);

    @DELETE("rest/v1/categories")
    Call<Void> deleteCategory(@Query("id") String idFilter);

    // Transactions
    @GET("rest/v1/transactions?order=transaction_date.desc")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.TransactionDto>> getTransactions(
            @Query("user_id") String userIdFilter,
            @Query("limit") int limit
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/transactions")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.TransactionDto>> insertTransaction(
            @Body com.example.app_marifin_javadroid.data.remote.dto.TransactionDto transaction
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/transactions")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.TransactionDto>> updateTransaction(
            @Query("id") String idFilter,
            @Body com.example.app_marifin_javadroid.data.remote.dto.TransactionDto transaction
    );

    @DELETE("rest/v1/transactions")
    Call<Void> deleteTransaction(@Query("id") String idFilter);
}
