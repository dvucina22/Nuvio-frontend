package com.example.nuviofrontend.feature.statistics.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.statistics.dto.TransactionStatisticsResponse
import com.example.nuviofrontend.feature.statistics.data.StatisticsRepository
import com.example.nuviofrontend.feature.statistics.report.StatisticsPdfReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val isLoading: Boolean = false,
    val statistics: TransactionStatisticsResponse? = null,
    val error: String? = null,

    val isGeneratingPdf: Boolean = false,
    val pdfUri: Uri? = null,
    val pdfError: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: StatisticsRepository,
    @ApplicationContext private val context: Context,
    private val pdfReportGenerator: StatisticsPdfReportGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state

    fun loadStatistics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val stats = repository.fetchStatistics()
                _state.value = _state.value.copy(isLoading = false, statistics = stats)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generatePdfReport(currencyIndex: Int) {
        val stats = _state.value.statistics?.data ?: run {
            _state.value = _state.value.copy(pdfError = "No statistics loaded.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isGeneratingPdf = true, pdfUri = null, pdfError = null)
            try {
                val uri = pdfReportGenerator.generate(
                    context = context,
                    title = "Izvještaj poslovanja",
                    data = stats,
                    currencyIndex = currencyIndex
                )
                _state.value = _state.value.copy(isGeneratingPdf = false, pdfUri = uri)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isGeneratingPdf = false, pdfError = e.message)
            }
        }
    }

    fun clearPdfResult() {
        _state.value = _state.value.copy(pdfUri = null, pdfError = null)
    }
}
