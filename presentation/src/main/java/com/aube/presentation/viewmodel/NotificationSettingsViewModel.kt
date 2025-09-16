package com.aube.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.aube.domain.service.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val scheduler: NotificationScheduler
) : ViewModel() {
    fun enable() = scheduler.enable()
    fun disable() = scheduler.disable()
}
