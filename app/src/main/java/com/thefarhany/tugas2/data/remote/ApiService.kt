package com.thefarhany.tugas2.data.remote

import com.thefarhany.tugas2.data.dto.AuthResponse
import com.thefarhany.tugas2.data.dto.BalanceResponse
import com.thefarhany.tugas2.data.dto.LoginRequest
import com.thefarhany.tugas2.data.dto.MoneyRequestRequest
import com.thefarhany.tugas2.data.dto.RegisterRequest
import com.thefarhany.tugas2.data.dto.TopUpRequest
import com.thefarhany.tugas2.data.dto.TransferRequest
import com.thefarhany.tugas2.data.dto.UpdateEmailRequest
import com.thefarhany.tugas2.data.dto.UpdateUsernameRequest
import com.thefarhany.tugas2.data.dto.UserResponse
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("api/user/profile")
    suspend fun getProfile(
        @Header("Cookie") cookie: String
    ): UserResponse

    @PUT("api/user/username")
    suspend fun updateUsername(
        @Header("Cookie") cookie: String,
        @Body body: UpdateUsernameRequest
    ): UserResponse

    @PUT("api/user/email")
    suspend fun updateEmail(
        @Header("Cookie") cookie: String,
        @Body body: UpdateEmailRequest
    ): UserResponse

    @Multipart
    @POST("api/user/profile-picture")
    suspend fun uploadProfilePicture(
        @Header("Cookie") cookie: String,
        @Part file: MultipartBody.Part
    ): UserResponse

    @POST("api/transaction/topup")
    suspend fun topUp(
        @Header("Cookie") cookie: String,
        @Body body: TopUpRequest
    ): TransactionResponse

    @POST("api/transaction/transfer")
    suspend fun transfer(
        @Header("Cookie") cookie: String,
        @Body body: TransferRequest
    ): TransactionResponse

    @POST("api/transaction/request")
    suspend fun requestMoney(
        @Header("Cookie") cookie: String,
        @Body body: MoneyRequestRequest
    ): TransactionResponse

    @GET("api/transaction/history")
    suspend fun getTransactionHistory(
        @Header("Cookie") cookie: String
    ): List<TransactionResponse>

    @GET("api/transaction/requests/pending")
    suspend fun getPendingRequests(
        @Header("Cookie") cookie: String
    ): List<TransactionResponse>

    @GET("api/balance")
    suspend fun getBalance(
        @Header("Cookie") cookie: String
    ): BalanceResponse

    @PUT("api/transaction/request/{id}/approve")
    suspend fun approveRequest(
        @Header("Cookie") cookie: String,
        @Path("id") id: Long
    ): TransactionResponse

    @PUT("api/transaction/request/{id}/reject")
    suspend fun rejectRequest(
        @Header("Cookie") cookie: String,
        @Path("id") id: Long
    ): TransactionResponse

}