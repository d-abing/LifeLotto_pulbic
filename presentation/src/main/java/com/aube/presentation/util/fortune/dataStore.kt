package com.aube.presentation.util.fortune

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aube.presentation.model.Fortune
import com.aube.presentation.model.LottoRecommendation
import com.aube.presentation.model.RangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.fortuneDataStore by preferencesDataStore(name = "fortune_prefs")
val Context.recommendDataStore by preferencesDataStore(name = "recommend_prefs")
val Context.lottoDataStore by preferencesDataStore(name = "lotto_prefs")

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
    private val ds = context.fortuneDataStore

    suspend fun save(f: Fortune) {
        ds.edit { p ->
            p[FortunePrefsKeys.LAST_DATE] = f.dateEpochDay
            p[FortunePrefsKeys.LAST_SCORE] = f.score
            p[FortunePrefsKeys.LAST_SUMMARY] = f.summary
            p[FortunePrefsKeys.LAST_NUMBERS] = f.luckyNumbers.joinToString(",")
            p[FortunePrefsKeys.LAST_TIME] = f.luckyTime
        }
    }

    val flow = ds.data
        .catch { e -> if (e is java.io.IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw e }
        .map { p ->
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
    private val ds = context.recommendDataStore

    private val keyDate = longPreferencesKey("last_recommend_date")
    private val keyNumbers = stringPreferencesKey("last_recommend_numbers")
    private val keyRangeFilter = stringPreferencesKey("range_filter")
    private val keyUseRandom = booleanPreferencesKey("use_random_for_recommend")
    private val keyTopCount = intPreferencesKey("use_top_count")
    private val keyLowCount = intPreferencesKey("use_low_count")

    val today: Flow<LottoRecommendation?> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val savedDate = prefs[keyDate] ?: return@map null
            val numbers = prefs[keyNumbers]?.split(",")?.mapNotNull { it.toIntOrNull() } ?: return@map null
            LottoRecommendation(savedDate, numbers)
        }

    val rangeFilter: Flow<String> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { it[keyRangeFilter] ?: RangeFilter.LAST10.name }

    val useRandomForRecommend: Flow<Boolean> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { it[keyUseRandom] ?: false }

    val useTopCount: Flow<Int> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { it[keyTopCount] ?: 3 }

    val useLowCount: Flow<Int> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { it[keyLowCount] ?: 3 }

    suspend fun saveToday(rec: LottoRecommendation) {
        ds.edit { prefs ->
            prefs[keyDate] = rec.dateEpochDay
            prefs[keyNumbers] = rec.numbers.joinToString(",")
        }
    }

    suspend fun setRangeFilter(option: String) = ds.edit { it[keyRangeFilter] = option }
    suspend fun setUseRandomForRecommend(value: Boolean) = ds.edit { it[keyUseRandom] = value }

    // 주석 0..8과 구현 0..6이 달라서 통일 권장 (6이 합리적)
    suspend fun setUseTopCount(value: Int) = ds.edit { it[keyTopCount] = value.coerceIn(0, 6) }
    suspend fun setUseLowCount(value: Int) = ds.edit { it[keyLowCount] = value.coerceIn(0, 6) }
}

@Singleton
class LottoPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ds = context.lottoDataStore
    private val KEY_BLUR = booleanPreferencesKey("my_numbers_blur")

    val blurFlow: Flow<Boolean> = ds.data
        .catch { e -> if (e is java.io.IOException) emit(emptyPreferences()) else throw e }
        .map { it[KEY_BLUR] ?: false }

    suspend fun setBlurred(blur: Boolean) {
        ds.edit { it[KEY_BLUR] = blur }
    }
}