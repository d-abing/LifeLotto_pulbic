package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.presentation.model.LottoRecommendation
import com.aube.presentation.util.fortune.RecommendationPrefs
import com.aube.presentation.util.generateLottoNumbers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class RecommendUiState(
    val recommended: List<Int> = emptyList()
)

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val prefs: RecommendationPrefs,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.today.collectLatest { cached ->
                val today = LocalDate.now().toEpochDay()
                if (cached == null || cached.dateEpochDay != today) {
                    generateAndSave()
                } else {
                    _uiState.update { it.copy(recommended = cached.numbers) }
                }
            }
        }
    }

    fun refreshToday() {
        viewModelScope.launch { generateAndSave() }
    }

    private suspend fun generateAndSave() {
        val today = LocalDate.now().toEpochDay()
        val numbers = generateLottoNumbers()
        val rec = LottoRecommendation(today, numbers)
        prefs.saveToday(rec)
        _uiState.update { it.copy(recommended = numbers) }
    }

    fun saveCurrent() {
        viewModelScope.launch {
            val current = _uiState.value.recommended
        }
    }
}
