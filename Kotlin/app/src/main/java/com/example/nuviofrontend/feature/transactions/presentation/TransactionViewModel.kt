package com.example.nuviofrontend.feature.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.transactions.dto.TransactionFilterRequest
import com.example.core.transactions.dto.TransactionListItem
import com.example.nuviofrontend.feature.transactions.data.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionsState(
    val query: String = "",
    val results: List<TransactionListItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val statusOptions: List<String> = emptyList(),
    val typeOptions: List<String> = emptyList(),
    val isFilterDataLoading: Boolean = false
)

data class TransactionsFilterState(
    val selectedStatuses: Set<String> = emptySet(),
    val selectedTypes: Set<String> = emptySet(),
    val amountRange: ClosedFloatingPointRange<Float> = 0f..500000f,
    val productCountRange: ClosedFloatingPointRange<Float> = 0f..50f,
    val dateFrom: String = "",
    val dateTo: String = ""
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var currentFilterState: TransactionsFilterState? = null

    private val pageSize = 12
    private var currentPage = 1
    private var total: Long? = null

    private val defaultStatusOptions = listOf(
        "PENDING",
        "APPROVED",
        "DECLINED",
        "VOIDED"
    )

    private val defaultTypeOptions = listOf(
        "SALE",
        "VOID"
    )

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(1000)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val trimmed = query.trim()
                    currentPage = 1
                    total = null

                    performSearch(trimmed, currentFilterState)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    fun applyFilters(filterState: TransactionsFilterState) {
        currentFilterState = filterState
        currentPage = 1
        total = null

        val query = _state.value.query.trim()
        performSearch(query, filterState)
    }

    private fun performSearch(query: String, filterState: TransactionsFilterState?) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    endReached = false,
                    error = null,
                    results = emptyList()
                )
            }

            val request = buildFilterRequest(query, filterState, page = 1, pageSize = pageSize)
            val result = transactionRepository.getTransactionHistory(request)

            result.onSuccess { resp ->
                currentPage = resp.page
                total = resp.total

                _state.update {
                    it.copy(
                        isLoading = false,
                        results = resp.transactions,
                        endReached = resp.transactions.isEmpty() ||
                                resp.transactions.size < pageSize ||
                                (total != null && resp.transactions.size.toLong() >= total!!),
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        results = emptyList(),
                        endReached = true,
                        error = e.message ?: "Transactions search failed"
                    )
                }
            }
        }
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.endReached) return

        val trimmed = current.query.trim()
        val nextPage = currentPage + 1

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }

            val request = buildFilterRequest(trimmed, currentFilterState, page = nextPage, pageSize = pageSize)
            val result = transactionRepository.getTransactionHistory(request)

            result.onSuccess { resp ->
                currentPage = resp.page
                total = resp.total

                val merged = current.results + resp.transactions
                val end = resp.transactions.isEmpty() ||
                        resp.transactions.size < pageSize ||
                        (total != null && merged.size.toLong() >= total!!)

                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        results = merged,
                        endReached = end,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Transactions search failed"
                    )
                }
            }
        }
    }

    private fun buildFilterRequest(
        query: String,
        filterState: TransactionsFilterState?,
        page: Int,
        pageSize: Int
    ): TransactionFilterRequest {
        val dateFrom = filterState?.dateFrom?.trim().orEmpty().takeIf { it.isNotBlank() }
        val dateTo = filterState?.dateTo?.trim().orEmpty().takeIf { it.isNotBlank() }

        val minAmount = filterState?.amountRange?.start?.toLong()?.takeIf { it > 0L }
        val maxAmount = filterState?.amountRange?.endInclusive?.toLong()?.takeIf { it > 0L }

        val minCount = filterState?.productCountRange?.start?.toInt()?.takeIf { it > 0 }
        val maxCount = filterState?.productCountRange?.endInclusive?.toInt()?.takeIf { it > 0 }

        return TransactionFilterRequest(
            search = query.takeIf { it.isNotBlank() },
            dateFrom = dateFrom,
            dateTo = dateTo,
            statuses = filterState?.selectedStatuses?.toList().orEmpty(),
            types = filterState?.selectedTypes?.toList().orEmpty(),
            amountMin = minAmount,
            amountMax = maxAmount,
            productCountMin = minCount,
            productCountMax = maxCount,
            page = page,
            pageSize = pageSize,
            userId = null
        )
    }

    fun ensureFilterDataLoaded() {
        val current = _state.value
        if ((current.statusOptions.isNotEmpty() || current.typeOptions.isNotEmpty()) || current.isFilterDataLoading) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isFilterDataLoading = true) }

            _state.update {
                it.copy(
                    statusOptions = defaultStatusOptions,
                    typeOptions = defaultTypeOptions,
                    isFilterDataLoading = false
                )
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
