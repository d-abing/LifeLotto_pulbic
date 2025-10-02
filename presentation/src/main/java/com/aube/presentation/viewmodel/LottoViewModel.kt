package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.model.MyLottoSet
import com.aube.domain.usecase.DeleteMyLottoNumbersUseCase
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.usecase.GetMyLottoNumbersHistoryUseCase
import com.aube.domain.usecase.GetMyLottoNumbersUseCase
import com.aube.domain.usecase.SaveMyLottoNumbersUseCase
import com.aube.domain.util.estimateLatestDateTime
import com.aube.domain.util.estimateLatestRound
import com.aube.domain.util.rankOf
import com.aube.presentation.model.LottoUiState
import com.aube.presentation.model.MatchResult
import com.aube.presentation.model.MyLottoNumbersUiState
import com.aube.presentation.model.toUiState
import com.aube.presentation.util.fortune.LottoPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LottoViewModel @Inject constructor(
    private val prefs: LottoPrefs,
    private val getMyLottoNumbersUseCase: GetMyLottoNumbersUseCase,
    private val saveMyLottoNumbersUseCase: SaveMyLottoNumbersUseCase,
    private val deleteMyLottoNumbersUseCase: DeleteMyLottoNumbersUseCase,
    private val getLottoResultUseCase: GetLottoResultUseCase,
    private val getMyLottoNumbersHistoryUseCase: GetMyLottoNumbersHistoryUseCase,
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

    private var latestNumbers: Pair<List<Int>, Int> = Pair(emptyList(), 0)
    private var matchHistory: List<Int> = emptyList()

    val isBlurred = prefs.blurFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false
    )


    fun loadHome() {
        loadLotto()
        loadMyLottoNumbers()
        getLatestNumbers()
        getMyLottoHistory()
    }

    fun loadLotto(round: Int = latestRound.value) {
        viewModelScope.launch {
            _lottoUiState.value = _lottoUiState.value.copy(isLoading = true)
            val result = getLottoResultUseCase(round)
            result?.let {
                _lottoUiState.value = it.toUiState().copy(isLoading = false)
            } ?: loadLotto(round - 1)
        }
    }

    private fun loadMyLottoNumbers() {
        viewModelScope.launch {
            val beforeDraw = getMyLottoNumbersUseCase(latestRound.value + 1)
            val myNumbers = getMyLottoNumbersUseCase(latestRound.value) - beforeDraw.toSet()
            val matchResult = calculateMatchResult(latestNumbers, myNumbers)
            _myLottoNumbersUiState.value = MyLottoNumbersUiState(
                beforeDraw = beforeDraw,
                myNumbers = myNumbers - beforeDraw.toSet(),
                matchHistory = matchHistory,
                matchResult = matchResult,
            )
        }
    }

    private fun getLatestNumbers(round: Int = latestRound.value) {
        viewModelScope.launch {
            val latestResult = getLottoResultUseCase(round)
            latestResult?. let {
                latestNumbers = Pair(latestResult.winningNumbers, latestResult.bonus)
            } ?: getLatestNumbers(round - 1)
        }
    }

    private fun getMyLottoHistory() {
        viewModelScope.launch {
            val myNumbersHistory  = getMyLottoNumbersHistoryUseCase()
            matchHistory = myNumbersHistory.mapNotNull { it.rank }
        }
    }

    fun saveMyLottoNumbers(numbers: List<Int>) {
        viewModelScope.launch {
            saveMyLottoNumbersUseCase(numbers)
            loadMyLottoNumbers()
            _newCombination.value = emptyList()
        }
    }

    fun deleteMyLottoNumbers(idx: Int) {
        viewModelScope.launch {
            deleteMyLottoNumbersUseCase(idx)
            loadMyLottoNumbers()
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

    fun onQrParsed(round: Int, sets: List<List<Int>>) {
        viewModelScope.launch {
            val r = getLottoResultUseCase(round)

            sets.forEach { numbers ->
                val rank = rankOf(numbers, r?.winningNumbers ?: emptyList(), r?.bonus ?: -1)
                saveMyLottoNumbersUseCase(
                    numbers = numbers,
                    round = round,
                    rank = rank
                )
            }
        }
    }

    fun toggleBlur() = viewModelScope.launch {
        prefs.setBlurred(!isBlurred.value)
    }
}

private fun calculateMatchResult(latestNumbers: Pair<List<Int>, Int>, myNumbers: List<MyLottoSet>): MatchResult? {
    val (winningNumbers, bonus) = latestNumbers
    val matchResult =
        if (myNumbers.isEmpty()) null
        else myNumbers.let { allNumbers ->
        val results = allNumbers.map { lottoSet ->
            val numbers = lottoSet.numbers
            rankOf(numbers, winningNumbers, bonus)
        }.filterNotNull()

        if (results.isEmpty()) {
            MatchResult.Lose
        } else {
            val bestRank = results.min()
            MatchResult.Win(rank = bestRank)
        }
    }

    return matchResult
}