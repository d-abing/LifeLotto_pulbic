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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val prefs: RecommendationPrefs,
    private val syncDrawHistoryUseCase: SyncDrawHistoryUseCase,
    private val observeDrawHistory: ObserveDrawHistoryUseCase, // Flow<List<LottoDraw>>
) : ViewModel() {

    data class UiState(
        val filter: RangeFilter = RangeFilter.LAST10,
        val all: List<LottoDraw> = emptyList(),
        val stats: StatsResult? = null,
        val isSyncing: Boolean = false,
        val progress: Float? = null
    )

    private val syncing = MutableStateFlow(false)

    val filterFlow: StateFlow<RangeFilter> =
        prefs.rangeFilter
            .map { runCatching { RangeFilter.valueOf(it) }.getOrDefault(RangeFilter.LAST10) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RangeFilter.LAST10)

    private val drawsFlow: Flow<List<LottoDraw>> = observeDrawHistory()

    val ui: StateFlow<UiState> =
        combine(drawsFlow, filterFlow, syncing) { all, filter, isSync ->
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
                progress = null
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        startSync()
    }

    fun setFilter(filter: RangeFilter) {
        viewModelScope.launch { prefs.setRangeFilter(filter.name) }
    }

    fun startSync() {
        viewModelScope.launch {
            syncing.value = true
            val result = runCatching { syncDrawHistoryUseCase() }
            result.onFailure { e -> Log.e("StatisticsVM", "Sync failed", e) }
            syncing.value = false
        }
    }
}

private fun buildStats(draws: List<LottoDraw>): StatsResult {
    val freq = IntArray(46)
    draws.forEach { d -> d.numbers.forEach { n -> if (n in 1..45) freq[n]++ } }
    val pairs = (1..45).map { it to freq[it] }
    val max = pairs.maxOfOrNull { it.second } ?: 0
    val top6 = pairs.sortedByDescending { it.second }.take(6)
    val low6 = pairs.sortedBy { it.second }.take(6)
    return StatsResult(freq, max, top6, low6)
}
