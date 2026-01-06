package com.thefarhany.tugas2.data.dto

data class TransferRequest(
    val receiverUsername: String,
    val amount: Double,
    val description: String?
)
