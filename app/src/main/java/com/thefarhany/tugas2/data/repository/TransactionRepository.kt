package com.thefarhany.tugas2.data.repository

import com.thefarhany.tugas2.data.dto.BalanceResponse
import com.thefarhany.tugas2.data.dto.MoneyRequestRequest
import com.thefarhany.tugas2.data.dto.TopUpRequest
import com.thefarhany.tugas2.data.dto.TransferRequest
import com.thefarhany.tugas2.data.remote.ApiService
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getHistory(cookie: String): List<TransactionResponse> =
        apiService.getTransactionHistory(cookie)

    suspend fun getPending(cookie: String): List<TransactionResponse> =
        apiService.getPendingRequests(cookie)

    suspend fun getBalance(cookie: String): BalanceResponse =
        apiService.getBalance(cookie)

    suspend fun topUp(cookie: String, request: TopUpRequest): TransactionResponse =
        apiService.topUp(cookie, request)

    suspend fun transfer(cookie: String, request: TransferRequest): TransactionResponse =
        apiService.transfer(cookie, request)

    suspend fun requestMoney(cookie: String, request: MoneyRequestRequest): TransactionResponse =
        apiService.requestMoney(cookie, request)

    suspend fun approveRequest(cookie: String, requestId: Long): TransactionResponse =
        apiService.approveRequest(cookie, requestId)

    suspend fun rejectRequest(cookie: String, requestId: Long): TransactionResponse =
        apiService.rejectRequest(cookie, requestId)
}