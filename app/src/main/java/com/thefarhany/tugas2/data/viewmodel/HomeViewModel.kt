package com.thefarhany.tugas2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thefarhany.tugas2.data.dto.BalanceResponse
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import com.thefarhany.tugas2.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val _balance = MutableLiveData<BalanceResponse?>()
    val balance: LiveData<BalanceResponse?> = _balance

    private val _pendingRequests = MutableLiveData<List<TransactionResponse>>(emptyList())
    val pendingRequests: LiveData<List<TransactionResponse>> = _pendingRequests

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _successMessage = MutableLiveData<String?>(null)
    val successMessage: LiveData<String?> = _successMessage

    fun loadBalance(cookie: String) {
        viewModelScope.launch {
            try {
                _balance.value = repo.getBalance(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal load balance"
            }
        }
    }

    fun loadPendingRequests(cookie: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                _pendingRequests.value = repo.getPending(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal load pending requests"
            } finally {
                _loading.value = false
            }
        }
    }

    fun approveRequest(cookie: String, requestId: Long) {
        _loading.value = true
        viewModelScope.launch {
            try {
                repo.approveRequest(cookie, requestId)
                _successMessage.value = "Request approved"
                loadPendingRequests(cookie)
                loadBalance(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal approve"
            } finally {
                _loading.value = false
            }
        }
    }

    fun rejectRequest(cookie: String, requestId: Long) {
        _loading.value = true
        viewModelScope.launch {
            try {
                repo.rejectRequest(cookie, requestId)
                _successMessage.value = "Request rejected"
                loadPendingRequests(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal reject"
            } finally {
                _loading.value = false
            }
        }
    }
}
