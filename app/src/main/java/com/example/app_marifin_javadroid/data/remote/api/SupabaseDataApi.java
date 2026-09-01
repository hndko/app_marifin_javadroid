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

    // Budgets
    @GET("rest/v1/budgets?order=start_date.desc")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BudgetDto>> getBudgets(
            @Query("user_id") String userIdFilter
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/budgets")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BudgetDto>> insertBudget(
            @Body com.example.app_marifin_javadroid.data.remote.dto.BudgetDto budget
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/budgets")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BudgetDto>> updateBudget(
            @Query("id") String idFilter,
            @Body com.example.app_marifin_javadroid.data.remote.dto.BudgetDto budget
    );

    @DELETE("rest/v1/budgets")
    Call<Void> deleteBudget(@Query("id") String idFilter);

    // Bills
    @GET("rest/v1/bills?order=due_date.asc")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BillDto>> getBills(
            @Query("user_id") String userIdFilter
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/bills")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BillDto>> insertBill(
            @Body com.example.app_marifin_javadroid.data.remote.dto.BillDto bill
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/bills")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.BillDto>> updateBill(
            @Query("id") String idFilter,
            @Body com.example.app_marifin_javadroid.data.remote.dto.BillDto bill
    );

    @DELETE("rest/v1/bills")
    Call<Void> deleteBill(@Query("id") String idFilter);

    // Goals
    @GET("rest/v1/financial_goals?order=created_at.desc")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.GoalDto>> getGoals(
            @Query("user_id") String userIdFilter
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/financial_goals")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.GoalDto>> insertGoal(
            @Body com.example.app_marifin_javadroid.data.remote.dto.GoalDto goal
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/financial_goals")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.GoalDto>> updateGoal(
            @Query("id") String idFilter,
            @Body com.example.app_marifin_javadroid.data.remote.dto.GoalDto goal
    );

    @DELETE("rest/v1/financial_goals")
    Call<Void> deleteGoal(@Query("id") String idFilter);

    // Documents / Vault
    @GET("rest/v1/documents?order=created_at.desc")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.DocumentDto>> getDocuments(
            @Query("user_id") String userIdFilter
    );

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/documents")
    Call<List<com.example.app_marifin_javadroid.data.remote.dto.DocumentDto>> insertDocument(
            @Body com.example.app_marifin_javadroid.data.remote.dto.DocumentDto document
    );

    @DELETE("rest/v1/documents")
    Call<Void> deleteDocument(@Query("id") String idFilter);
}
