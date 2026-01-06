package com.thefarhany.tugas2.data.dto

data class MoneyRequestRequest(
    val payerUsername: String,
    val amount: Double,
    val description: String?
)
