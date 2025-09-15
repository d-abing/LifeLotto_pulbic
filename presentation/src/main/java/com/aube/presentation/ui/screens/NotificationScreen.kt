package com.aube.presentation.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.viewmodel.NotificationSettingsViewModel

@Composable
fun NotificationScreen(
    modifier: Modifier,
    vm: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    // 저장된 토글 상태 (기본 false)
    var enabled by remember {
        mutableStateOf(prefs.getBoolean("notif_enabled", false))
    }

    // Android 13+ 알림 권한 보유 여부
    var hasPermission by remember {
        mutableStateOf(isNotifGranted(context))
    }

    // 권한 요청 런처
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            vm.enable()
            enabled = true
            prefs.edit().putBoolean("notif_enabled", true).apply()
        } else {
            enabled = false
            prefs.edit().putBoolean("notif_enabled", false).apply()
        }
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔔 추첨 결과 알림", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 36.dp))


        Text(
            text = "매주 토요일 8시 48분에\n당첨 번호와 결과를 보내드립니다",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ON/OFF 스위치
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("OFF / ON")
            Switch(
                checked = enabled,
                onCheckedChange = { on ->
                    if (on) {
                        if (Build.VERSION.SDK_INT >= 33 && !hasPermission) {
                            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            vm.enable()
                            enabled = true
                        }
                    } else {
                        vm.disable()
                        enabled = false
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 권한 상태 표시 + 액션
        PermissionRow(
            hasPermission = hasPermission,
            onRequest = {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onOpenSettings = { context.openAppSettings() }
        )
    }
}

@Composable
private fun PermissionRow(
    hasPermission: Boolean,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val status = when {
            !needsPermission -> "권한 불필요 (Android 12 이하)"
            hasPermission -> "권한 허용됨"
            else -> "권한 거부됨"
        }
        Text("알림 권한 상태: $status")

        if (needsPermission && !hasPermission) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onRequest) { Text("권한 요청") }
                OutlinedButton(onClick = onOpenSettings) { Text("설정으로 이동") }
            }
        }
    }
}

/* ───────────── 유틸 ───────────── */

private fun isNotifGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    startActivity(intent)
}
