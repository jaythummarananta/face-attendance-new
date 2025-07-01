package com.ananta.faceapp.presentation.screens.detect_screen

import ShowCustomAlertDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.ananta.globalwallet.ui.composables.CustomToast
import com.ananta.faceapp.ApiRepo.AuthApi

import com.ananta.faceapp.data.attendance.AttendanceModel
import com.ananta.faceapp.data.attendance.BackendModel
import com.ananta.faceapp.presentation.components.AppAlertDialog
import com.ananta.faceapp.presentation.components.DelayedVisibility
import com.ananta.faceapp.presentation.components.FaceDetectionOverlay
import com.ananta.faceapp.presentation.components.createAlertDialog
import com.ananta.faceapp.presentation.theme.FaceNetAndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream

private val cameraPermissionStatus = mutableStateOf(false)
private val storagePermissionStatus = mutableStateOf(false)
private val cameraFacing = mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
private lateinit var storagePermissionLauncher: ManagedActivityResultLauncher<String, Boolean>

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectScreen(onNavigate: () -> Unit) {
    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }

    FaceNetAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ScreenUI(onNavigate)
            }
        }
    }
}

@Composable
private fun ScreenUI(onNavigate: () -> Unit) {
    val viewModel: DetectScreenViewModel = koinViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box {
        Camera(
            viewModel = viewModel,
            context = context,
            scope = scope,
            onNavigateToHome = onNavigate,
            cameraPermissionStatus = cameraPermissionStatus,
            cameraFacing = cameraFacing
        )

        DelayedVisibility(true) {
            Column {
                // Detection Status Text at the top
                DetectionStatusOverlay(viewModel = viewModel)

                Spacer(modifier = Modifier.weight(1f))

                // Metrics display at the bottom
                val metrics by viewModel.faceDetectionMetricsState.collectAsState()
                metrics?.let { metricsData ->
                    Text(
                        text = "face detection: ${metricsData.timeFaceDetection} ms\n" +
                                "spoof detection: ${metricsData.timeFaceSpoofDetection} ms",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        AppAlertDialog()
    }
}

@kotlin.OptIn(ExperimentalPerfettoTraceProcessorApi::class)
@Composable
private fun DetectionStatusOverlay(viewModel: DetectScreenViewModel) {
    val statusText by viewModel.detectionStatusText.collectAsState()
    val statusColor by viewModel.detectionStatusColor.collectAsState()
    val isRealFace by viewModel.isFaceRealState.collectAsState()
    val isFakeFace by viewModel.isFakeUserState.collectAsState()
    val isLoading by viewModel.isLoadingState.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status text with animated color change
            Text(
                text = statusText,
                color = Color(statusColor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status icon based on detection state
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Blue,
                        strokeWidth = 2.dp
                    )
                }

                isRealFace -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Real Face Detected",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                }

                isFakeFace -> {

//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Warning,
//                            contentDescription = "Fake Face Detected",
//                            tint = Color.Red,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Security Alert!",
//                            color = Color.Red,
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
                }

                else -> {
                    // Scanning animation
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Searching for face",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(0.7f)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@OptIn(ExperimentalGetImage::class)
@Composable
private fun Camera(
    viewModel: DetectScreenViewModel,
    context: Context,
    scope: CoroutineScope,
    onNavigateToHome: () -> Unit,
    cameraPermissionStatus: MutableState<Boolean>,
    cameraFacing: MutableState<Int>
) {
    var isShowDialog by remember { mutableStateOf(false) }
    var isNavigateHome by remember { mutableStateOf(false) }
    var isShowFakeUserDialog by remember { mutableStateOf(false) }
    val authApi = AuthApi.getInstance(context)
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val isFakeUser by viewModel.isFakeUserState.collectAsState()
    var attendanceDataResponse by remember { mutableStateOf<BackendModel?>(null) }
    val capturedFaceImage by viewModel.getCapturedFaceImage()
        ?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)
    val faceDetectionOverlay = remember { mutableStateOf<FaceDetectionOverlay?>(null) }

    // Permission checks
    cameraPermissionStatus.value =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionStatus.value = isGranted
        if (!isGranted) {
            camaraPermissionDialog()
        }
    }

    // Observe face detection result
    val isReal by viewModel.isFaceRealState.collectAsState()

    LaunchedEffect(isReal, isFakeUser, capturedFaceImage) {
        Log.d("Camera", "isReal: $isReal, isFakeUser: $isFakeUser")

        when {
            isReal && cameraPermissionStatus.value && capturedFaceImage != null -> {
                isShowDialog = true
                viewModel.getCapturedFaceImage()?.let { bitmap ->
                    scope.launch {
                        try {
                            viewModel.setLoading(true)
                            val imageFile = bitmapToFile(context, bitmap)
                            Log.d("Camera", "attendancePick: $imageFile")
                            val response = authApi.attendancePick(imageFile)
                            attendanceResponse = response
                            if (response != null && response.success == true) {
                                viewModel.setAttendanceResponse(response)
                                val bioId = response.data?.userDetails?.bioId?.toString() ?: ""
                                if (bioId.isNotEmpty()) {
                                    val attendanceResult = authApi.doAddAttendance(bioId)
                                    Log.d("backend api", "doAddAttendance result: $attendanceResult")
                                    if (attendanceResult != null) {
                                        attendanceDataResponse = attendanceResult // Update if needed
                                    } else {
                                        toastMessage = "Failed to add attendance"
                                        showToast = true
                                    }
                                } else {
                                    toastMessage = "BioID not found"
                                    showToast = true
                                }
                            } else {
                                toastMessage = "Attendance pick failed"
                                showToast = true
                            }
                        } catch (e: Exception) {
                            Log.d("Camera", "attendancePick failed: ${e.message}")
                            toastMessage = "Error: ${e.message}"
                            showToast = true
                        } finally {
                            viewModel.setLoading(false)
                        }
                    }
                }
            }

            isFakeUser -> {
                // Fake face detected - show warning dialog
                AuthApi.spoofAttempts++
                if (AuthApi.spoofAttempts > 3) {
                    Toast.makeText(
                        context,
                        "Please Call Developer You are Blocked",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "Spoof detected ${AuthApi.spoofAttempts} time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onNavigateToHome()
            }
        }
    }

    // UI
    DelayedVisibility(cameraPermissionStatus.value) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                FaceDetectionOverlay(lifecycleOwner, ctx, viewModel).also {
                    faceDetectionOverlay.value = it
                }
            },
            update = { it.initializeCamera(cameraFacing.value) }
        )
    }
    // Custom Toast for spoof detection
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
    ) {
        CustomToast(
            message = toastMessage,
            isVisible = showToast,
            onDismiss = { showToast = false },
            duration = 2000L
        )
    }

    DelayedVisibility(!cameraPermissionStatus.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Allow Camera and Storage Permissions\nThe app cannot work without these permissions.",
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Allow")
            }
        }
    }

    // Loading dialog
    if (isLoading) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissing while loading */ },
            confirmButton = {},
            title = { Text("Processing") },
            text = { Text("Capturing and processing face image...") }
        )
    }
    attendanceResponse?.let { response ->
        ShowCustomAlertDialog(
            response = response,
//            onDismissRequest = {
//                attendanceResponse = null
//                viewModel.resetState()
//                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
//                onNavigateToHome()
//            },
//            onConfirm = {
//                viewModel.resetState()
//                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
//                onNavigateToHome()
//            }
        )

        // Auto-dismiss after 2 seconds
        LaunchedEffect(Unit) {
            delay(2000)
            onNavigateToHome()
        }
    }


}

private fun camaraPermissionDialog() {
    createAlertDialog(
        "Camera Permission",
        "The app couldn't function without the camera permission.",
        "ALLOW",
        "CLOSE",
        onPositiveButtonClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        onNegativeButtonClick = {
            // TODO: Handle deny camera permission action
        }
    )
}

private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "face_image_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}