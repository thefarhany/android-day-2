package com.thefarhany.tugas2.data.dto

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val email: String
)

