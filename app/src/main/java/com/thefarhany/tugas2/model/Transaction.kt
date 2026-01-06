package com.thefarhany.tugas2.model

data class Transaction(
    val id: String,
    val amount: String,
    val senderName: String,
    val receiverName: String,
    val status: String,
    val date: String,
    val type: String
)
