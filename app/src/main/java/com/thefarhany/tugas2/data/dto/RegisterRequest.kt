package com.thefarhany.tugas2.data.dto

data class RegisterRequest(
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String
)
