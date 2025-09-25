package com.aube.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.model.LottoDraw
import com.aube.domain.usecase.ObserveDrawHistoryUseCase
import com.aube.domain.usecase.SyncDrawHistoryUseCase
import com.aube.presentation.model.RangeFilter
import com.aube.presentation.model.StatsResult
import com.aube.presentation.util.fortune.RecommendationPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val prefs: RecommendationPrefs,
    private val syncDrawHistoryUseCase: SyncDrawHistoryUseCase,
    private val observeDrawHistory: ObserveDrawHistoryUseCase,
) : ViewModel() {

    data class UiState(
        val filter: RangeFilter = RangeFilter.LAST10,
        val all: List<LottoDraw> = emptyList(),
        val stats: StatsResult? = null,
        val isSyncing: Boolean = false,
        val progress: Float? = null,

        val useRandomForRecommend: Boolean = false,
        val useTopCount: Int = 3,
        val useLowCount: Int = 3,
    )

    private val syncing = MutableStateFlow(false)

    val filterFlow: StateFlow<RangeFilter> =
        prefs.rangeFilter
            .map { runCatching { RangeFilter.valueOf(it) }.getOrDefault(RangeFilter.LAST10) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RangeFilter.LAST10)

    private val useRandomFlow: StateFlow<Boolean> =
        prefs.useRandomForRecommend
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val useTopCountFlow: StateFlow<Int> =
        prefs.useTopCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    private val useLowCountFlow: StateFlow<Int> =
        prefs.useLowCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    private val drawsFlow: Flow<List<LottoDraw>> = observeDrawHistory()

    val ui: StateFlow<UiState> =
    combine(
    listOf(drawsFlow, filterFlow, syncing, useRandomFlow, useTopCountFlow, useLowCountFlow)
    ) { array ->
        val all = array[0] as List<LottoDraw>
        val filter = array[1] as RangeFilter
        val isSync = array[2] as Boolean
        val useRandom = array[3] as Boolean
        val topCnt = array[4] as Int
        val lowCnt = array[5] as Int

        val sorted = all.sortedByDescending { it.round }
        val sliced = when (filter) {
            RangeFilter.LAST10 -> sorted.take(10)
            RangeFilter.LAST30 -> sorted.take(30)
            RangeFilter.LAST50 -> sorted.take(50)
            RangeFilter.ALL    -> sorted
        }

        UiState(
            filter = filter,
            all = sorted,
            stats = buildStats(sliced),
            isSyncing = isSync,
            progress = null,
            useRandomForRecommend = useRandom,
            useTopCount = topCnt.coerceIn(0, 6),
            useLowCount = lowCnt.coerceIn(0, 6)
        )
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())


    init {
        startSync()
    }

    fun setFilter(filter: RangeFilter) {
            viewModelScope.launch { prefs.setRangeFilter(filter.name) }

    }

    fun setUseRandomForRecommend(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setUseRandomForRecommend(enabled)
            prefs.setUseTopCount(if (enabled) 0 else 3)
            prefs.setUseLowCount(if (enabled) 0 else 3)
        }
    }

    fun setUseTopCount(value: Int) {
        viewModelScope.launch {
            prefs.setUseTopCount(value)
            if (value + ui.value.useLowCount > 6) {
                prefs.setUseLowCount(6 - value)
            }
        }
    }
    fun setUseLowCount(value: Int) {
        viewModelScope.launch {
            prefs.setUseLowCount(value)
            if (value + ui.value.useTopCount > 6) {
                prefs.setUseTopCount(6 - value)
            }
        }
    }

    fun startSync() {
        viewModelScope.launch {
            syncing.value = true
            runCatching { syncDrawHistoryUseCase() }
                .onFailure {
                    // e -> Log.e("StatisticsVM", "Sync failed", e)
                }
            syncing.value = false
        }
    }
}

private fun buildStats(draws: List<LottoDraw>): StatsResult {
    val freq = IntArray(46)
    draws.forEach { d -> d.numbers.forEach { n -> if (n in 1..45) freq[n]++ } }
    val pairs = (1..45).map { it to freq[it] }
    val max = pairs.maxOfOrNull { it.second } ?: 0
    val top8 = pairs.sortedByDescending { it.second }.take(8)
    val low8 = pairs.sortedBy { it.second }.take(8)
    val freqList = freq.toList()
    return StatsResult(freqList, max, top8, low8)
}