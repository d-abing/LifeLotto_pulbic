package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.usecase.DeleteMyLottoNumbersUseCase
import com.aube.domain.usecase.GetLottoResultUseCase
import com.aube.domain.usecase.GetMyLottoNumbersUseCase
import com.aube.domain.usecase.SaveMyLottoNumbersUseCase
import com.aube.presentation.model.LottoUiState
import com.aube.presentation.model.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LottoViewModel @Inject constructor(
    private val getMyLottoNumbersUseCase: GetMyLottoNumbersUseCase,
    private val saveMyLottoNumbersUseCase: SaveMyLottoNumbersUseCase,
    private val deleteMyLottoNumbersUseCase: DeleteMyLottoNumbersUseCase,
    private val getLottoResultUseCase: GetLottoResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LottoUiState>(LottoUiState())
    val uiState: StateFlow<LottoUiState> = _uiState

    fun loadLotto(round: Int) {
        viewModelScope.launch {
            val result = getLottoResultUseCase(round)
            _uiState.value = result.toUiState(getMyLottoNumbersUseCase()?.numbers)
        }
    }

    fun saveMyLottoNumbers(numbers: List<List<Int>>) {
        viewModelScope.launch {
            saveMyLottoNumbersUseCase(numbers)
        }
    }

    fun deleteMyLottoNumbers(idx: Int) {
        viewModelScope.launch {
            deleteMyLottoNumbersUseCase(idx)
        }
    }
}
