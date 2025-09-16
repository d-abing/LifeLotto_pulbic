package com.aube.lifelotto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.Intent.ACTION_MY_PACKAGE_REPLACED
import com.aube.domain.service.NotificationScheduler
import dagger.hilt.android.EntryPointAccessors

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_BOOT_COMPLETED,
            ACTION_MY_PACKAGE_REPLACED -> {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context,
                    ReceiverEntryPoint::class.java
                )
                entryPoint.notificationScheduler().enable()
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface ReceiverEntryPoint {
    fun notificationScheduler(): NotificationScheduler
}
