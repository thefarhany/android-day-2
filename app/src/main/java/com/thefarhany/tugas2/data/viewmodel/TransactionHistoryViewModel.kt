package com.thefarhany.tugas2.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thefarhany.tugas2.data.remote.dto.TransactionResponse
import com.thefarhany.tugas2.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val repo: TransactionRepository
) : ViewModel() {

    private val _history = MutableLiveData<List<TransactionResponse>>(emptyList())
    val history: LiveData<List<TransactionResponse>> = _history

    private val _pending = MutableLiveData<List<TransactionResponse>>(emptyList())
    val pending: LiveData<List<TransactionResponse>> = _pending

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadHistory(cookie: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _history.value = repo.getHistory(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal load history"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPending(cookie: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _pending.value = repo.getPending(cookie)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal load pending"
            } finally {
                _loading.value = false
            }
        }
    }
}