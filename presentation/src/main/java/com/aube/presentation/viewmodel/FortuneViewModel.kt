package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.presentation.model.FortuneUiState
import com.aube.presentation.util.fortune.FortuneGenerator
import com.aube.presentation.util.fortune.FortunePrefs
import com.aube.presentation.util.fortune.SeedKeyProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject



@HiltViewModel
class FortuneViewModel @Inject constructor(
    private val prefs: FortunePrefs,
    private val seedKeyProvider: SeedKeyProvider
) : ViewModel() {

    private val _state = MutableStateFlow(FortuneUiState())
    val state: StateFlow<FortuneUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { prefs.flow.collect { _state.update { it.copy(fortune = it.fortune ?: it.fortune) } } }
        ensureToday()
    }

    fun ensureToday() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val today = LocalDate.now()
            val current = _state.value.fortune
            if (current?.dateEpochDay != today.toEpochDay()) {
                val gen = FortuneGenerator.generate(today, seedKeyProvider.get())
                prefs.save(gen)
                _state.update { it.copy(isLoading = false, fortune = gen) }
            } else _state.update { it.copy(isLoading = false) }
        }
    }
}