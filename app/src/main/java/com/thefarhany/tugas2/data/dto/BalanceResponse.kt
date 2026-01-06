package com.thefarhany.tugas2.data.dto

data class BalanceResponse(
    val id: Long,
    val userId: Long,
    val username: String,
    val amount: String,
    val updatedAt: String?
)
