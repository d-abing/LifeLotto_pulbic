package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.usecase.DeleteMyLottoNumbersUseCase
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.usecase.GetMyLottoNumbersUseCase
import com.aube.domain.usecase.SaveMyLottoNumbersUseCase
import com.aube.presentation.model.LottoUiState
import com.aube.presentation.model.MatchResult
import com.aube.presentation.model.MyLottoNumbersUiState
import com.aube.presentation.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class LottoViewModel @Inject constructor(
    private val getMyLottoNumbersUseCase: GetMyLottoNumbersUseCase,
    private val saveMyLottoNumbersUseCase: SaveMyLottoNumbersUseCase,
    private val deleteMyLottoNumbersUseCase: DeleteMyLottoNumbersUseCase,
    private val getLottoResultUseCase: GetLottoResultUseCase
) : ViewModel() {

    private val _lottoUiState = MutableStateFlow<LottoUiState>(LottoUiState())
    val lottoUiState: StateFlow<LottoUiState> = _lottoUiState

    private val _myLottoNumbersUiState = MutableStateFlow<MyLottoNumbersUiState>(MyLottoNumbersUiState())
    val myLottoNumbersUiState: StateFlow<MyLottoNumbersUiState> = _myLottoNumbersUiState

    private val _latestRound = MutableStateFlow(estimateLatestRound())
    val latestRound: StateFlow<Int> = _latestRound

    private val _latestDate = MutableStateFlow(estimateLatestDateTime())
    val latestDate: StateFlow<LocalDateTime> = _latestDate

    private val _newCombination = MutableStateFlow<List<Int>>(emptyList())
    val newCombination: StateFlow<List<Int>> = _newCombination

    fun loadHome() {
        loadLotto()
        loadMyLottoNumbers()
    }

    fun loadLotto(round: Int = latestRound.value) {
        viewModelScope.launch {
            val result = getLottoResultUseCase(round)
            _lottoUiState.value = result.toUiState()
        }
    }

    private fun loadMyLottoNumbers() {
        viewModelScope.launch {
            val myNumbers = getMyLottoNumbersUseCase(latestDate.value)
            _myLottoNumbersUiState.value = MyLottoNumbersUiState(
                myNumbers = myNumbers?.map { it.numbers },
                matchResult = MatchResult.Lose
            )
        }
    }

    fun saveMyLottoNumbers(numbers: List<Int>) {
        viewModelScope.launch {
            saveMyLottoNumbersUseCase(numbers)
        }
        loadMyLottoNumbers()
    }

    fun deleteMyLottoNumbers(idx: Int) {
        viewModelScope.launch {
            deleteMyLottoNumbersUseCase(idx)
        }
    }

    fun deleteNumberFromCombination(number: Int) {
        _newCombination.value = _newCombination.value.filter { it != number }
    }

    fun addNumberToCombination(number: Int) {
        if (_newCombination.value.size >= 6 || _newCombination.value.contains(number)) return
        _newCombination.value += number
        _newCombination.value = _newCombination.value.sorted()
    }
}


private fun estimateLatestRound(): Int {
    val firstDrawDate = LocalDate.of(2002, 12, 7)
    val today = LocalDate.now()
    val weeks = ChronoUnit.WEEKS.between(firstDrawDate, today)
    return weeks.toInt() + 1
}

fun estimateLatestDateTime(): LocalDateTime {
    val baseDate = LocalDate.of(2002, 12, 7) // 1회차 추첨일
    val drawTime = LocalTime.of(20, 45)      // 추첨 시간
    val latestRound = estimateLatestRound()  // 이미 구현된 함수
    val latestDate = baseDate.plusWeeks((latestRound - 1).toLong())
    return LocalDateTime.of(latestDate, drawTime)
}