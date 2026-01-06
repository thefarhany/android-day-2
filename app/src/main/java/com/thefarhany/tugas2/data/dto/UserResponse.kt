package com.thefarhany.tugas2.data.dto

data class UserResponse(
    val id: Long,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val profilePicture: String?,
    val createdAt: String?,
    val updatedAt: String?
)
