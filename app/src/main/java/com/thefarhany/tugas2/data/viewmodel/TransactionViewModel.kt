package com.thefarhany.tugas2.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thefarhany.tugas2.data.dto.BalanceResponse
import com.thefarhany.tugas2.data.dto.MoneyRequestRequest
import com.thefarhany.tugas2.data.dto.TopUpRequest
import com.thefarhany.tugas2.data.dto.TransferRequest
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import com.thefarhany.tugas2.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val _balance = MutableLiveData<BalanceResponse?>()
    val balance: LiveData<BalanceResponse?> = _balance

    private val _lastTransaction = MutableLiveData<TransactionResponse?>()
    val lastTransaction: LiveData<TransactionResponse?> = _lastTransaction

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _successMessage = MutableLiveData<String?>(null)
    val successMessage: LiveData<String?> = _successMessage

    fun loadBalance(cookie: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                _balance.value = repo.getBalance(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal ambil saldo"
            } finally {
                _loading.value = false
            }
        }
    }

    fun topUp(cookie: String, amount: Double, description: String?) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val result = repo.topUp(cookie, TopUpRequest(amount, description))
                _lastTransaction.value = result
                _successMessage.value = "Top up Berhasil"
                loadBalance(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Top up Gagal"
            } finally {
                _loading.value = false
            }
        }
    }

    fun transfer(cookie: String, receiverUsername: String, amount: Double, description: String?) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val result = repo.transfer(cookie, TransferRequest(receiverUsername, amount, description))
                _lastTransaction.value = result
                _successMessage.value = "Transfer Berhasil"
                loadBalance(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Transfer Gagal"
            } finally {
                _loading.value = false
            }
        }
    }

    fun requestMoney(cookie: String, payerUsername: String, amount: Double, description: String?) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val result = repo.requestMoney(cookie, MoneyRequestRequest(payerUsername, amount, description))
                _lastTransaction.value = result
                _successMessage.value = "Request CREATED: id=${result.id}, amount=${result.amount}, status=${result.status}"
            } catch (e: Exception) {
                _error.value = e.message ?: "Request Gagal"
            } finally {
                _loading.value = false
            }
        }
    }
}
