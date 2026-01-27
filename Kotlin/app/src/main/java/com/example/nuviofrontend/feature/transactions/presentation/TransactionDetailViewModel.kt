package com.example.nuviofrontend.feature.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.transactions.dto.TransactionDetail
import com.example.nuviofrontend.feature.transactions.data.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailState(
    val isLoading: Boolean = false,
    val data: TransactionDetail? = null,
    val error: String? = null
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailState())
    val state: StateFlow<TransactionDetailState> = _state.asStateFlow()

    fun load(transactionId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = transactionRepository.getTransactionDetail(transactionId)
            result.onSuccess { detail ->
                _state.update { it.copy(isLoading = false, data = detail, error = null) }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        data = null,
                        error = e.message ?: "Greška pri dohvaćanju detalja transakcije"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
