package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.model.LottoSet
import com.aube.domain.usecase.DeleteRecommendedNumbersUseCase
import com.aube.domain.usecase.GetRecommendedNumbersUseCase
import com.aube.domain.usecase.SaveRecommendedNumbersUseCase
import com.aube.presentation.model.LottoRecommendation
import com.aube.presentation.model.RecommendUiState
import com.aube.presentation.util.fortune.RecommendationPrefs
import com.aube.presentation.util.generateLottoNumbers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val prefs: RecommendationPrefs,
    private val saveRecommendedNumberUseCase: SaveRecommendedNumbersUseCase,
    private val getRecommendedNumbersUseCase: GetRecommendedNumbersUseCase,
    private val deleteRecommendedNumberUseCase: DeleteRecommendedNumbersUseCase
) : ViewModel() {

    private val _recommendNumbers = MutableStateFlow(RecommendUiState())
    val recommendNumbers: StateFlow<RecommendUiState> = _recommendNumbers.asStateFlow()

    val savedNumbers: StateFlow<List<LottoSet>> =
        getRecommendedNumbersUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            prefs.today
                .collectLatest { cached ->
                    val todayEpoch = LocalDate.now(ZoneId.of("Asia/Seoul")).toEpochDay()
                    if (cached == null || cached.dateEpochDay != todayEpoch) {
                        generateAndSave()
                    } else {
                        _recommendNumbers.update { it.copy(recommended = cached.numbers) }
                    }
                }
        }
    }

    fun refreshToday(numbers: List<Int>? = null) {
        viewModelScope.launch { generateAndSave(numbers) }
    }

    private suspend fun generateAndSave(numbers: List<Int>? = null) {
        val today = LocalDate.now().toEpochDay()
        val newNumbers = generateLottoNumbers(numbers)
        val rec = LottoRecommendation(today, newNumbers)
        prefs.saveToday(rec)
        _recommendNumbers.update { it.copy(recommended = newNumbers) }
    }

    fun saveCurrent() = viewModelScope.launch {
        val current = _recommendNumbers.value.recommended
        runCatching { saveRecommendedNumberUseCase(current) }
    }

    fun deleteCurrent(id: Int) = viewModelScope.launch {
        runCatching { deleteRecommendedNumberUseCase(id) }
    }
}
