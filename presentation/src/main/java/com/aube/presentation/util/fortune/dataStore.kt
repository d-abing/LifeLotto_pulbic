package com.aube.presentation.util.fortune

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aube.presentation.model.Fortune
import com.aube.presentation.model.LottoRecommendation
import com.aube.presentation.model.RangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("fortune_prefs")

object FortunePrefsKeys {
    val LAST_DATE = longPreferencesKey("last_date")
    val LAST_SCORE = intPreferencesKey("last_score")
    val LAST_SUMMARY = stringPreferencesKey("last_summary")
    val LAST_NUMBERS = stringPreferencesKey("last_numbers")
    val LAST_TIME = stringPreferencesKey("last_time")
}

@Singleton
class FortunePrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun save(f: Fortune) {
        context.dataStore.edit { p ->
            p[FortunePrefsKeys.LAST_DATE] = f.dateEpochDay
            p[FortunePrefsKeys.LAST_SCORE] = f.score
            p[FortunePrefsKeys.LAST_SUMMARY] = f.summary
            p[FortunePrefsKeys.LAST_NUMBERS] = f.luckyNumbers.joinToString(",")
            p[FortunePrefsKeys.LAST_TIME] = f.luckyTime
        }
    }
    val flow = context.dataStore.data.map { p ->
        val d = p[FortunePrefsKeys.LAST_DATE] ?: return@map null
        Fortune(
            dateEpochDay = d,
            score = p[FortunePrefsKeys.LAST_SCORE] ?: 0,
            summary = p[FortunePrefsKeys.LAST_SUMMARY].orEmpty(),
            luckyNumbers = p[FortunePrefsKeys.LAST_NUMBERS].orEmpty()
                .split(",").filter { it.isNotBlank() }.map { it.toInt() },
            luckyTime = p[FortunePrefsKeys.LAST_TIME].orEmpty(),
        )
    }
}

@Singleton
class RecommendationPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore("recommend_prefs")

    // 기존 키
    private val keyDate = longPreferencesKey("last_recommend_date")
    private val keyNumbers = stringPreferencesKey("last_recommend_numbers")
    private val keyRangeFilter = stringPreferencesKey("range_filter")

    // 신규 키
    private val keyUseRandom = booleanPreferencesKey("use_random_for_recommend")
    private val keyTopCount = intPreferencesKey("use_top_count")
    private val keyLowCount = intPreferencesKey("use_low_count")

    // 오늘 추천 번호
    val today: Flow<LottoRecommendation?> = context.dataStore.data.map { prefs ->
        val savedDate = prefs[keyDate] ?: return@map null
        val numbers = prefs[keyNumbers]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: return@map null
        LottoRecommendation(savedDate, numbers)
    }

    // 통계 범위 필터 (LAST10 / LAST30 / …)
    val rangeFilter: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[keyRangeFilter] ?: RangeFilter.LAST10.name
    }

    // 추천 모드 (통계 미사용 = 랜덤)
    val useRandomForRecommend: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[keyUseRandom] ?: false
    }

    // 추천에 사용할 많이 나온 번호 개수 (0..8)
    val useTopCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyTopCount] ?: 3
    }

    // 추천에 사용할 적게 나온 번호 개수 (0..8)
    val useLowCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyLowCount] ?: 3
    }

    suspend fun saveToday(rec: LottoRecommendation) {
        context.dataStore.edit { prefs ->
            prefs[keyDate] = rec.dateEpochDay
            prefs[keyNumbers] = rec.numbers.joinToString(",")
        }
    }

    suspend fun setRangeFilter(option: String) {
        context.dataStore.edit { prefs ->
            prefs[keyRangeFilter] = option
        }
    }

    suspend fun setUseRandomForRecommend(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[keyUseRandom] = value
        }
    }

    suspend fun setUseTopCount(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[keyTopCount] = value.coerceIn(0, 6)
        }
    }

    suspend fun setUseLowCount(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[keyLowCount] = value.coerceIn(0, 6)
        }
    }
}
