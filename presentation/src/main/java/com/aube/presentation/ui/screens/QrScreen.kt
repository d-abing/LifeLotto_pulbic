package com.aube.presentation.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@OptIn(ExperimentalGetImage::class)
@Composable
fun QrScreen(
    onQrDetected: (String) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 최초 자동 요청 여부 저장 (재진입 구분용)
    val prefs = remember(context) { context.getSharedPreferences("qr_prefs", Context.MODE_PRIVATE) }
    var askedOnceEver by remember { mutableStateOf(prefs.getBoolean("camera_asked_once", false)) }
    var lastRequestWasAuto by remember { mutableStateOf(false) }

    // 현재 타이밍에서의 '다시 묻기' 가능 여부
    val shouldShowRationaleNow =
        activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(
                it, android.Manifest.permission.CAMERA
            )
        } ?: false

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
            if (!granted) {
                if (lastRequestWasAuto) {
                    // 첫 거절 → 즉시 닫기
                    onClose()
                } else {
                    // 재요청 거절 → 화면 유지(설정이동/재요청 버튼 제공)
                    Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    BackHandler {
        onClose()
    }

    // 첫 진입 시 자동 요청
    LaunchedEffect(Unit) {
        if (!hasPermission && !askedOnceEver && !shouldShowRationaleNow) {
            lastRequestWasAuto = true
            askedOnceEver = true
            prefs.edit().putBoolean("camera_asked_once", true).apply()
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // ───────────── 권한 없음: 흰 배경 + 우상단 닫기 + 선택지 ─────────────
    if (!hasPermission) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)  // ← 흰색 배경
        ) {
            // 우측 상단 닫기
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "카메라 권한이 필요합니다",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))

                Row {
                    if (shouldShowRationaleNow) {
                        Button(
                            onClick = {
                                lastRequestWasAuto = false
                                launcher.launch(android.Manifest.permission.CAMERA)
                            }
                        ) { Text("권한 다시 요청") }

                        Spacer(Modifier.width(8.dp))
                    }

                    Button(onClick = { context.openAppSettings() }) {
                        Text("설정으로 이동")
                    }
                }
            }
        }
        return
    }

    // ───────────── 권한 OK: 카메라 스캔 동작부 ─────────────
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS or
                        CameraController.VIDEO_CAPTURE or
                        CameraController.IMAGE_CAPTURE
            )
        }
    }

    var handled by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val scanner = com.google.mlkit.vision.barcode.BarcodeScanning.getClient(
            com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                .build()
        )

        controller.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close(); return@setImageAnalysisAnalyzer
            }
            val input = com.google.mlkit.vision.common.InputImage.fromMediaImage(
                mediaImage, imageProxy.imageInfo.rotationDegrees
            )
            scanner.process(input)
                .addOnSuccessListener { barcodes ->
                    if (handled) return@addOnSuccessListener
                    val url = barcodes.firstOrNull {
                        it.valueType == com.google.mlkit.vision.barcode.common.Barcode.TYPE_URL
                    }?.url?.url
                    if (url != null) {
                        handled = true
                        onQrDetected(url)
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        }

        controller.bindToLifecycle(lifecycleOwner)
        onDispose {
            try { controller.unbind() } catch (_: Throwable) {}
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                androidx.camera.view.PreviewView(it).apply {
                    this.controller = controller
                    scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        // 스캐너 화면에서도 닫기 버튼 유지(요구사항과 동일한 위치)
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                .background(MaterialTheme.colorScheme.tertiary.copy(0.4f), CircleShape),
            onClick = onClose
        ) { Icon(Icons.Default.Close, contentDescription = "닫기") }
    }
}

/* ───────────── 유틸 ───────────── */

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
