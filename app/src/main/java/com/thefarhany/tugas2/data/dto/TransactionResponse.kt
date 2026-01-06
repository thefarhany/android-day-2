package com.thefarhany.tugas2.data.remote.dto

data class TransactionResponse(
    val id: Long,
    val senderId: Long,
    val senderUsername: String,
    val receiverId: Long?,
    val receiverUsername: String?,
    val type: String,
    val amount: String,
    val status: String,
    val description: String?,
    val createdAt: String?
)
