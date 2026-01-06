package com.thefarhany.tugas2.data.repository

import com.thefarhany.tugas2.data.dto.LoginRequest
import com.thefarhany.tugas2.data.dto.RegisterRequest
import com.thefarhany.tugas2.data.remote.RetrofitClient

class AuthRepository {
    suspend fun login(email: String, password: String) = RetrofitClient.apiService.login(
        LoginRequest(email, password)
    )

    suspend fun register(username: String, phoneNumber: String, email: String, password: String) =
        RetrofitClient.apiService.register(RegisterRequest(username, phoneNumber, email, password))
}