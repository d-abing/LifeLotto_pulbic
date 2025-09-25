package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.domain.service.NotificationScheduler
import com.aube.presentation.util.fortune.NotifPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val scheduler: NotificationScheduler,
    private val notifPrefs: NotifPrefs
) : ViewModel() {

    val enabled = notifPrefs.enabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun toggleEnable(on: Boolean) {
        viewModelScope.launch {
            notifPrefs.setEnabled(on)
            if (on) scheduler.enable() else scheduler.disable()
        }
    }

    fun enable() = scheduler.enable()
    fun disable() = scheduler.disable()
}
