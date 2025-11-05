//package com.ananta.faceapp.presentation.screens.detect_screen
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.OptIn
//import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ExperimentalGetImage
//import androidx.compose.animation.animateContentSize
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDownward
//import androidx.compose.material.icons.filled.ArrowUpward
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.SignalCellularAlt
//import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material.icons.filled.Wifi
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.app.ActivityCompat
//import androidx.navigation.NavController
//import com.ananta.faceapp.ApiRepo.AuthApi
//
//import com.ananta.faceapp.data.attendance.BackendModel
//import com.ananta.faceapp.presentation.components.AppAlertDialog
//import com.ananta.faceapp.presentation.components.DelayedVisibility
//import com.ananta.faceapp.presentation.components.FaceDetectionOverlay
//import com.ananta.faceapp.presentation.components.createAlertDialog
//import com.ananta.faceapp.presentation.theme.FaceNetAndroidTheme
//import com.google.gson.Gson
//import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.koin.androidx.compose.koinViewModel
//import java.io.File
//import java.io.FileOutputStream
//
//private val cameraPermissionStatus = mutableStateOf(false)
//private val storagePermissionStatus = mutableStateOf(false)
//private val cameraFacing = mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
//private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
//private lateinit var storagePermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
//
//sealed class NavigationEvent {
//    object NavigateBack : NavigationEvent()
//    object NavigateToHome : NavigationEvent()
//    data class NavigateWithResponse(val responseJson: String) : NavigationEvent()
//    object None : NavigationEvent()
//}
//@kotlin.OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DetectScreen(navController: NavController) {
//    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
//
//    FaceNetAndroidTheme {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//        ) { innerPadding ->
//            Column(modifier = Modifier.padding(innerPadding)) {
//                ScreenUI(navController)
//            }
//        }
//    }
//}
//
//@Composable
//private fun ScreenUI(navController: NavController) {
//    val viewModel: DetectScreenViewModel = koinViewModel()
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    Box {
//        Camera(
//            viewModel = viewModel,
//            context = context,
//            scope = scope,
//            navController = navController,
////            onNavigateToHome = {
////                // Navigate to home and clear back stack
////                navController.navigate("dashboard") {
////                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
////                    launchSingleTop = true
////                }
////            },
//            cameraPermissionStatus = cameraPermissionStatus,
//            cameraFacing = cameraFacing
//        )
//
//        DelayedVisibility(true) {
//            Column {
//                // Detection Status Text at the top
//
//                NetworkSpeedIndicator()
//                DetectionStatusOverlay(viewModel = viewModel)
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                // Metrics display at the bottom
//                val metrics by viewModel.faceDetectionMetricsState.collectAsState()
//                metrics?.let { metricsData ->
//                    Text(
//                        text = "face detection: ${metricsData.timeFaceDetection} ms\n" +
//                                "spoof detection: ${metricsData.timeFaceSpoofDetection} ms",
//                        color = Color.White,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 24.dp),
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
//
//        AppAlertDialog()
//    }
//}
//
//@kotlin.OptIn(ExperimentalPerfettoTraceProcessorApi::class)
//@Composable
//private fun DetectionStatusOverlay(viewModel: DetectScreenViewModel) {
//    val statusText by viewModel.detectionStatusText.collectAsState()
//    val statusColor by viewModel.detectionStatusColor.collectAsState()
//    val isRealFace by viewModel.isFaceRealState.collectAsState()
//    val isFakeFace by viewModel.isFakeUserState.collectAsState()
//    val isLoading by viewModel.isLoadingState.collectAsState()
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Black.copy(alpha = 0.7f)
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Status text with animated color change
//            Text(
//                text = statusText,
//                color = Color(statusColor),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.animateContentSize()
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Status icon based on detection state
//            when {
//                isLoading -> {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp),
//                        color = Color.Blue,
//                        strokeWidth = 2.dp
//                    )
//                }
//
//                isRealFace -> {
//                    Icon(
//                        imageVector = Icons.Default.CheckCircle,
//                        contentDescription = "Real Face Detected",
//                        tint = Color.Green,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//
//                isFakeFace -> {
//
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
//                }
//
//                else -> {
//                    // Scanning animation
//                    Box(
//                        modifier = Modifier.size(24.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            contentDescription = "Searching for face",
//                            tint = Color.Gray,
//                            modifier = Modifier
//                                .size(20.dp)
//                                .alpha(0.7f)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
//@OptIn(ExperimentalGetImage::class)
//@Composable
//private fun Camera(
//    viewModel: DetectScreenViewModel,
//    context: Context,
//    scope: CoroutineScope,
//    navController: NavController,
////    onNavigateToHome: () -> Unit,
//    cameraPermissionStatus: MutableState<Boolean>,
//    cameraFacing: MutableState<Int>
//) {
//    var isShowDialog by remember { mutableStateOf(false) }
//    var isNavigateHome by remember { mutableStateOf(false) }
//    var isShowFakeUserDialog by remember { mutableStateOf(false) }
//    val authApi = AuthApi.getInstance(context)
//    val gson = Gson()
//    var showToast by remember { mutableStateOf(false) }
//    var toastMessage by remember { mutableStateOf("") }
//    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    val isFakeUser by viewModel.isFakeUserState.collectAsState()
//    var attendanceDataResponse by remember { mutableStateOf<BackendModel?>(null) }
//    val capturedFaceImage by viewModel.getCapturedFaceImage()
//        ?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)
//    val faceDetectionOverlay = remember { mutableStateOf<FaceDetectionOverlay?>(null) }
//    var navigationEvent by remember { mutableStateOf<NavigationEvent>(NavigationEvent.None) }
//
//
//    // Permission checks
//    cameraPermissionStatus.value =
//        ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
//                PackageManager.PERMISSION_GRANTED
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    // Camera permission launcher
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        cameraPermissionStatus.value = isGranted
//        if (!isGranted) {
//            camaraPermissionDialog()
//        }
//    }
//
//    // Observe face detection result
//    val isReal by viewModel.isFaceRealState.collectAsState()
//    LaunchedEffect(navigationEvent) {
//        when (val event = navigationEvent) {
//            is NavigationEvent.NavigateBack -> {
//                if (navController.previousBackStackEntry != null) {
//                    navController.popBackStack()
//                } else {
//                    navController.navigate("dashboard") {
//                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    }
//                }
//            }
//            is NavigationEvent.NavigateWithResponse -> {
//                navController.previousBackStackEntry?.savedStateHandle?.set(
//                    "attendanceResponse",
//                    event.responseJson
//                )
//                if (navController.previousBackStackEntry != null) {
//                    navController.popBackStack()
//                } else {
//                    navController.navigate("dashboard")
//                }
//            }
//            is NavigationEvent.NavigateToHome -> {
//                navController.navigate("dashboard") {
//                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    launchSingleTop = true
//                }
//            }
//            NavigationEvent.None -> {}
//        }
//        navigationEvent = NavigationEvent.None // ✅ Must reset
//    }
//
//
////    LaunchedEffect(isReal, isFakeUser, capturedFaceImage) {
////        Log.d("Camera", "isReal: $isReal, isFakeUser: $isFakeUser")
////
////        when {
////         isReal && capturedFaceImage != null -> {
////             delay(2000)
////                isShowDialog = true
////                viewModel.getCapturedFaceImage()?.let { bitmap ->
////                    scope.launch {
////                        try {
////                            viewModel.setLoading(true)
////                            val imageFile = bitmapToFile(context, bitmap)
////                            Log.d("Camera", "attendancePick: $imageFile")
////                            val response = authApi.attendancePick(imageFile)
////                            attendanceResponse = response
////
////                            if (response != null && response.success) {
////
////                                val responseJson = gson.toJson(response)
////                                Log.d("backend api", "doAddAttendance: ${response.data}")
////                                navigationEvent = NavigationEvent.NavigateWithResponse(responseJson)
////                            }
////                            else if(response?.error !=null){
////                                val responseJson = gson.toJson(response)
////                                Log.d("backend api", "doAddAttendancedoAddAttendance: ${response}")
////                                navigationEvent = NavigationEvent.NavigateWithResponse(responseJson)
////
////                            }
////                            else {
////                                navigationEvent =NavigationEvent.NavigateWithResponse("")
////                                toastMessage = "Attendance pick failed"
////                                showToast = true
////                            }
////                        } catch (e: Exception) {
////                            navigationEvent = NavigationEvent.NavigateWithResponse("")
////                            toastMessage = "Error: ${e.message}"
////                            showToast = true
////                        } finally {
////                            viewModel.setLoading(false)
////                        }
////                    }
////                }
////            }
////
////            isFakeUser -> {
////                // Fake face detected - show warning dialog
////                AuthApi.spoofAttempts++
////
////                if (AuthApi.spoofAttempts > 3) {
////                    Toast.makeText(
////                        context,
////                        "Please Call Developer You are Blocked",
////                        Toast.LENGTH_SHORT
////                    ).show()
////                } else {
////                    Toast.makeText(
////                        context,
////                        "Spoof detected ${AuthApi.spoofAttempts} time",
////                        Toast.LENGTH_SHORT
////                    ).show()
////                }
////                navigationEvent = NavigationEvent.NavigateToHome
////            }
////        }
////    }
//    LaunchedEffect(isReal, isFakeUser, capturedFaceImage) {
//        Log.d("Camera", "isReal: $isReal, isFakeUser: $isFakeUser")
//
//        when {
//            isReal && cameraPermissionStatus.value && capturedFaceImage != null -> {
//                isShowDialog = true
//                viewModel.getCapturedFaceImage()?.let { bitmap ->
//                    scope.launch {
//                        try {
//                            viewModel.setLoading(true)
//                            val imageFile = bitmapToFile(context, bitmap)
//                            Log.d("Camera", "attendancePick: $imageFile")
//                            val response = authApi.attendancePick(imageFile)
//                            attendanceResponse = response
//
//                            if (response != null && response.success == true) {
//                                viewModel.setAttendanceResponse(response)
//                                val responseJson = gson.toJson(response)
//                                Log.d("backend api", "attendancePick: ${response.data}")
//                                val bioId = response.data?.userDetails?.bioId?.toString() ?: ""
//                                if (bioId.isNotEmpty()) {
//                                    val attendanceResult = authApi.doAddAttendance(bioId)
//                                    Log.d("backend api", "doAddAttendance result: $attendanceResult")
//                                    if (attendanceResult != null) {
//                                        attendanceDataResponse = attendanceResult // Update if needed
//                                        val attendanceResultJson = gson.toJson(attendanceResult)
//                                        navigationEvent = NavigationEvent.NavigateWithResponse(responseJson) // Pass attendancePick response
//                                    } else {
//                                        navigationEvent = NavigationEvent.NavigateWithResponse("") // Still navigate with attendancePick response
//                                        toastMessage = "Failed to add attendance"
//                                        showToast = true
//                                    }
//                                } else {
//                                    navigationEvent = NavigationEvent.NavigateWithResponse("")
//                                    toastMessage = "BioID not found"
//                                    showToast = true
//                                }
//                            } else {
//                                val responseJson = gson.toJson(response)
//                                Log.d("backend api", "attendancePick failed: ${response}")
//                                navigationEvent = NavigationEvent.NavigateWithResponse( "")
//                                toastMessage = "Attendance pick failed"
//                                showToast = true
//                            }
//                        } catch (e: Exception) {
//                            Log.d("Camera", "attendancePick failed: ${e.message}")
//                            navigationEvent = NavigationEvent.NavigateWithResponse("")
//                            toastMessage = "Error: ${e.message}"
//                            showToast = true
//                        } finally {
//                            viewModel.setLoading(false)
//                        }
////                        try {
////                            viewModel.setLoading(true)
////                            val imageFile = bitmapToFile(context, bitmap)
////                            Log.d("Camera", "attendancePick: $imageFile")
////                            val response = authApi.attendancePick(imageFile)
////                            attendanceResponse = response
////                            if (response != null && response.success == true) {
////                                viewModel.setAttendanceResponse(response)
////                                val bioId = response.data?.userDetails?.bioId?.toString() ?: ""
////                                if (bioId.isNotEmpty()) {
////                                    val attendanceResult = authApi.doAddAttendance(bioId)
////                                    Log.d("backend api", "doAddAttendance result: $attendanceResult")
////                                    if (attendanceResult != null) {
////                                        attendanceDataResponse = attendanceResult // Update if needed
////                                    } else {
////                                        toastMessage = "Failed to add attendance"
////                                        showToast = true
////                                    }
////                                } else {
////                                    toastMessage = "BioID not found"
////                                    showToast = true
////                                }
////                            } else {
////                                toastMessage = "Attendance pick failed"
////                                showToast = true
////                            }
////                        } catch (e: Exception) {
////                            Log.d("Camera", "attendancePick failed: ${e.message}")
////                            toastMessage = "Error: ${e.message}"
////                            showToast = true
////                        } finally {
////                            viewModel.setLoading(false)
////                        }
//                    }
//                }
//            }
//
//            isFakeUser -> {
//                // Fake face detected - show warning dialog
//                AuthApi.spoofAttempts++
//
//                if (AuthApi.spoofAttempts > 3) {
//                    Toast.makeText(
//                        context,
//                        "Please Call Developer You are Blocked",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        context,
//                        "Spoof detected ${AuthApi.spoofAttempts} time",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                navigationEvent = NavigationEvent.NavigateToHome
//            }
//        }
//    }
//
////    if (isShowDialog && capturedFaceImage != null) {
////        AlertDialog(
////            onDismissRequest = { isShowDialog = false },
////            confirmButton = {
////                Button(onClick = {
////                    isShowDialog = false
////                    // ✅ API call yaha karo
//////                    attendancePick(context, capturedFaceImage!!)
////                }) {
////                    Text("Proceed")
////                }
////            },
////            dismissButton = {
////                Button(onClick = { isShowDialog = false }) {
////                    Text("Cancel")
////                }
////            },
////            title = { Text("Captured Face") },
////            text = {
////                Column(
////                    horizontalAlignment = Alignment.CenterHorizontally,
////                    modifier = Modifier.fillMaxWidth()
////                ) {
////                    Image(
////                        bitmap = capturedFaceImage!!.asImageBitmap(),
////                        contentDescription = "Captured Face",
////                        modifier = Modifier
////                            .size(200.dp)
////                            .padding(8.dp)
////                    )
////                }
////            }
////        )
////    }
//
//    // UI
//    DelayedVisibility(cameraPermissionStatus.value) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { ctx ->
//                FaceDetectionOverlay(lifecycleOwner, ctx, viewModel).also {
//                    faceDetectionOverlay.value = it
//                }
//            },
//            update = { it.initializeCamera(cameraFacing.value) }
//        )
//    }
//    // Custom Toast for spoof detection
//    DelayedVisibility(!cameraPermissionStatus.value) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                "Allow Camera and Storage Permissions\nThe app cannot work without these permissions.",
//                textAlign = TextAlign.Center
//            )
//            Button(
//                onClick = {
//                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text(text = "Allow")
//            }
//        }
//    }
//
//    // Loading dialog
//    if (isLoading) {
//        AlertDialog(
//            onDismissRequest = { /* Prevent dismissing while loading */ },
//            confirmButton = {},
//            title = { Text("Processing") },
//            text = { Text("Capturing and processing face image...") }
//        )
//    }
//}
//
//private fun camaraPermissionDialog() {
//    createAlertDialog(
//        "Camera Permission",
//        "The app couldn't function without the camera permission.",
//        "ALLOW",
//        "CLOSE",
//        onPositiveButtonClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
//        onNegativeButtonClick = {
//            // TODO: Handle deny camera permission action
//        }
//    )
//}
//
//private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
//    val file = File(context.cacheDir, "face_image_${System.currentTimeMillis()}.jpg")
//    FileOutputStream(file).use { out ->
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//    }
//    return file
////    val tempFile = File.createTempFile("cropped_", ".raw", context.cacheDir)
////
////    FileOutputStream(tempFile).use { out ->
////        val buffer = ByteBuffer.allocate(bitmap.byteCount)
////        bitmap.copyPixelsToBuffer(buffer)
////        out.write(buffer.array())
////    }
////    return tempFile
//    // Create temp file
////    val tempFile = File.createTempFile("cropped_", ".png", context.cacheDir)
////
////    FileOutputStream(tempFile).use { out ->
////        // PNG = lossless, keeps original quality
////        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
////    }
////    return tempFile
//}
//
//
//// Add this new composable function to monitor network speed
//// Add this class to track actual network usage
//class NetworkSpeedTracker(private val context: Context) {
//    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
//    private var previousRxBytes = 0L
//    private var previousTxBytes = 0L
//    private var previousTime = 0L
//
//    init {
//        previousRxBytes = android.net.TrafficStats.getTotalRxBytes()
//        previousTxBytes = android.net.TrafficStats.getTotalTxBytes()
//        previousTime = System.currentTimeMillis()
//    }
//
//    fun getNetworkSpeed(): Triple<String, String, String> {
//        val currentRxBytes = android.net.TrafficStats.getTotalRxBytes()
//        val currentTxBytes = android.net.TrafficStats.getTotalTxBytes()
//        val currentTime = System.currentTimeMillis()
//
//        val timeDiff = (currentTime - previousTime) / 1000.0
//
//        if (timeDiff > 0) {
//            val downloadBytes = (currentRxBytes - previousRxBytes).toDouble()
//            val uploadBytes = (currentTxBytes - previousTxBytes).toDouble()
//
//            val downloadSpeed = downloadBytes / timeDiff
//            val uploadSpeed = uploadBytes / timeDiff
//
//            previousRxBytes = currentRxBytes
//            previousTxBytes = currentTxBytes
//            previousTime = currentTime
//
//            val downloadSpeedStr = formatSpeed(downloadSpeed)
//            val uploadSpeedStr = formatSpeed(uploadSpeed)
//            val networkType = getNetworkType()
//
//            return Triple(downloadSpeedStr, uploadSpeedStr, networkType)
//        }
//
//        return Triple("0 B/s", "0 B/s", getNetworkType())
//    }
//
//    private fun formatSpeed(bytesPerSecond: Double): String {
//        return when {
//            bytesPerSecond >= 1024 * 1024 -> String.format("%.2f MB/s", bytesPerSecond / (1024 * 1024))
//            bytesPerSecond >= 1024 -> String.format("%.1f KB/s", bytesPerSecond / 1024)
//            bytesPerSecond > 0 -> String.format("%.0f B/s", bytesPerSecond)
//            else -> "0 B/s"
//        }
//    }
//
//    private fun getNetworkType(): String {
//        val network = connectivityManager.activeNetwork
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
//
//        return when {
//            networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
//            networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
//            networkCapabilities?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
//            else -> "No Connection"
//        }
//    }
//}
//
//// Simple NetworkSpeedIndicator without animations
//@Composable
//fun NetworkSpeedIndicator() {
//    val context = LocalContext.current
//    var downloadSpeed by remember { mutableStateOf("0 B/s") }
//    var uploadSpeed by remember { mutableStateOf("0 B/s") }
//    var networkType by remember { mutableStateOf("Unknown") }
//
//    val speedTracker = remember { NetworkSpeedTracker(context) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            val (download, upload, type) = speedTracker.getNetworkSpeed()
//            downloadSpeed = download
//            uploadSpeed = upload
//            networkType = type
//
//            delay(500) // Update every 500ms
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Black.copy(alpha = 0.6f)
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Network type
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = when(networkType) {
//                        "WiFi" -> Icons.Default.Wifi
//                        "Mobile" -> Icons.Default.SignalCellularAlt
//                        else -> Icons.Default.SignalCellularConnectedNoInternet0Bar
//                    },
//                    contentDescription = "Network Type",
//                    tint = if (networkType != "No Connection") Color.Green else Color.Red,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = networkType,
//                    color = Color.White,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//
//            // Download speed
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Default.ArrowDownward,
//                    contentDescription = "Download",
//                    tint = Color.Cyan,
//                    modifier = Modifier.size(14.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = downloadSpeed,
//                    color = Color.White,
//                    fontSize = 11.sp
//                )
//            }
//
//            // Upload speed
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    imageVector = Icons.Default.ArrowUpward,
//                    contentDescription = "Upload",
//                    tint = Color.Yellow,
//                    modifier = Modifier.size(14.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = uploadSpeed,
//                    color = Color.White,
//                    fontSize = 11.sp
//                )
//            }
//        }
//    }
//}

package com.ananta.faceapp.presentation.screens.detect_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
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
import androidx.navigation.NavController
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.data.attendance.BackendModel
import com.ananta.faceapp.presentation.components.AppAlertDialog
import com.ananta.faceapp.presentation.components.DelayedVisibility
import com.ananta.faceapp.presentation.components.FaceDetectionOverlay
import com.ananta.faceapp.presentation.components.createAlertDialog
import com.ananta.faceapp.presentation.theme.FaceNetAndroidTheme
import com.google.gson.Gson
import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val cameraPermissionStatus = mutableStateOf(false)
private val storagePermissionStatus = mutableStateOf(false)
private val cameraFacing = mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
private lateinit var storagePermissionLauncher: ManagedActivityResultLauncher<String, Boolean>

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
    object NavigateToHome : NavigationEvent()
    data class NavigateWithResponse(val responseJson: String) : NavigationEvent()
    object None : NavigationEvent()
}

/* -------------------------------------------------------------------------- */
/*  NETWORK TRACKER – now emits a StateFlow for connection state              */
/* -------------------------------------------------------------------------- */
class NetworkSpeedTracker(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

    private var previousRxBytes = 0L
    private var previousTxBytes = 0L
    private var previousTime = 0L

    // ---- public StateFlow ---------------------------------------------------
    private val _connectionState = MutableStateFlow(true) // true = connected
    val connectionState = _connectionState.asStateFlow()

    init {
        previousRxBytes = android.net.TrafficStats.getTotalRxBytes()
        previousTxBytes = android.net.TrafficStats.getTotalTxBytes()
        previousTime = System.currentTimeMillis()
    }

    fun getNetworkSpeed(): Triple<String, String, String> {
        val currentRxBytes = android.net.TrafficStats.getTotalRxBytes()
        val currentTxBytes = android.net.TrafficStats.getTotalTxBytes()
        val currentTime = System.currentTimeMillis()

        val timeDiff = (currentTime - previousTime) / 1000.0
        var downloadSpeedStr = "0 B/s"
        var uploadSpeedStr = "0 B/s"
        var networkType = getNetworkType()

        if (timeDiff > 0) {
            val downloadBytes = (currentRxBytes - previousRxBytes).toDouble()
            val uploadBytes = (currentTxBytes - previousTxBytes).toDouble()

            val downloadSpeed = downloadBytes / timeDiff
            val uploadSpeed = uploadBytes / timeDiff

            downloadSpeedStr = formatSpeed(downloadSpeed)
            uploadSpeedStr = formatSpeed(uploadSpeed)

            previousRxBytes = currentRxBytes
            previousTxBytes = currentTxBytes
            previousTime = currentTime
        }

        // ---- update connection state ----------------------------------------
        val connected = networkType != "No Connection"
        if (_connectionState.value != connected) {
            _connectionState.value = connected
        }

        return Triple(downloadSpeedStr, uploadSpeedStr, networkType)
    }

    private fun formatSpeed(bytesPerSecond: Double): String = when {
        bytesPerSecond >= 1024 * 1024 -> String.format("%.2f MB/s", bytesPerSecond / (1024 * 1024))
        bytesPerSecond >= 1024 -> String.format("%.1f KB/s", bytesPerSecond / 1024)
        bytesPerSecond > 0 -> String.format("%.0f B/s", bytesPerSecond)
        else -> "0 B/s"
    }

    private fun getNetworkType(): String {
        val network = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(network)
        return when {
            caps?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
            caps?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
            caps?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "No Connection"
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  MAIN SCREEN                                                               */
/* -------------------------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectScreen(navController: NavController) {
    FaceNetAndroidTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ScreenUI(navController)
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
@Composable
private fun ScreenUI(navController: NavController) {
    val viewModel: DetectScreenViewModel = koinViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ---- shared connection state --------------------------------------------
    val speedTracker = remember { NetworkSpeedTracker(context) }
    val isConnected by speedTracker.connectionState.collectAsState()

    Box {
        Camera(
            viewModel = viewModel,
            context = context,
            scope = scope,
            navController = navController,
            cameraPermissionStatus = cameraPermissionStatus,
            cameraFacing = cameraFacing,
            isConnected = isConnected
        )

        DelayedVisibility(true) {
            Column {
                NetworkSpeedIndicator(speedTracker = speedTracker)
                DetectionStatusOverlay(viewModel = viewModel)

                Spacer(modifier = Modifier.weight(1f))

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

/* -------------------------------------------------------------------------- */
@OptIn(ExperimentalPerfettoTraceProcessorApi::class)
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
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusText,
                color = Color(statusColor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize()
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Blue,
                    strokeWidth = 2.dp
                )

                isRealFace -> Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Real Face Detected",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )

                isFakeFace -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Fake Face Detected",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Security Alert!",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                else -> Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Searching for face",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).alpha(0.7f)
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
//@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
//@OptIn(ExperimentalGetImage::class)
//@Composable
//private fun Camera(
//    viewModel: DetectScreenViewModel,
//    context: Context,
//    scope: CoroutineScope,
//    navController: NavController,
//    cameraPermissionStatus: MutableState<Boolean>,
//    cameraFacing: MutableState<Int>,
//    isConnected: Boolean                 // <-- NEW
//) {
//    var navigationEvent by remember { mutableStateOf<NavigationEvent>(NavigationEvent.None) }
//    val authApi = AuthApi.getInstance(context)
//    val gson = Gson()
//    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
//    var attendanceDataResponse by remember { mutableStateOf<BackendModel?>(null) }
//    val isFakeUser by viewModel.isFakeUserState.collectAsState()
//    val isFaceCentered by viewModel.isFaceCenteredState.collectAsState()
//    val speedTracker = remember { NetworkSpeedTracker(context) }
//
//    val capturedFaceImage by viewModel.getCapturedFaceImage()
//        ?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)
//
//    val faceDetectionOverlay = remember { mutableStateOf<FaceDetectionOverlay?>(null) }
//
//    // ------------------------------------------------------------------ permission
//    cameraPermissionStatus.value =
//        ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
//                PackageManager.PERMISSION_GRANTED
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        cameraPermissionStatus.value = isGranted
//        if (!isGranted) camaraPermissionDialog()
//    }
//
//    var showNoInternetDialog by remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            val (download, upload, type) = speedTracker.getNetworkSpeed()
//
//            // Show dialog if no connection
//            showNoInternetDialog = (type == "No Connection")
//
//            delay(1000) // check every second
//        }
//    }
//
//    if (showNoInternetDialog) {
//        AlertDialog(
//            onDismissRequest = {},
//            title = { Text("No Internet Connection") },
//            text = { Text("Your device is not connected to the internet. Please check your connection.") },
//            confirmButton = {
//                Button(onClick = { showNoInternetDialog = false;
//                    navigationEvent = NavigationEvent.NavigateToHome
//                }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//    // ------------------------------------------------------------------ navigation
//    LaunchedEffect(navigationEvent) {
//        when (val event = navigationEvent) {
//            is NavigationEvent.NavigateBack -> {
//                if (navController.previousBackStackEntry != null) {
//                    navController.popBackStack()
//                } else {
//                    navController.navigate("dashboard") {
//                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    }
//                }
//            }
//
//            is NavigationEvent.NavigateWithResponse -> {
//                navController.previousBackStackEntry?.savedStateHandle?.set(
//                    "attendanceResponse",
//                    event.responseJson
//                )
//                if (navController.previousBackStackEntry != null) {
//                    navController.popBackStack()
//                } else {
//                    navController.navigate("dashboard")
//                }
//            }
//
//            is NavigationEvent.NavigateToHome -> {
//                navController.navigate("dashboard") {
//                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                    launchSingleTop = true
//                }
//            }
//
//            NavigationEvent.None -> {}
//        }
//        navigationEvent = NavigationEvent.None
//    }
//
//    // ------------------------------------------------------------------ core logic
//    LaunchedEffect(isFaceCentered, isFakeUser, capturedFaceImage, isConnected) {
//        Log.d("Camera", "centered=$isFaceCentered fake=$isFakeUser connected=$isConnected")
//
//        // ---- SPOOF -------------------------------------------------------
//        if (isFakeUser) {
//            AuthApi.spoofAttempts++
//            if (AuthApi.spoofAttempts > 3) {
//                Toast.makeText(
//                    context,
//                    "Access blocked due to multiple spoof attempts. Please contact the developer.",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
//                Toast.makeText(
//                    context,
//                    "Spoof detected ${AuthApi.spoofAttempts} time(s). Please face the camera properly.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            navigationEvent = NavigationEvent.NavigateToHome
//            return@LaunchedEffect
//        }
//
//        // ---- NO INTERNET → BLOCK EVERYTHING ------------------------------
//        if (!isConnected) {
//            // just wait – nothing else runs
//            return@LaunchedEffect
//        }
//
//        // ---- FACE CENTERED & IMAGE READY ---------------------------------
//        if (isFaceCentered && cameraPermissionStatus.value && capturedFaceImage != null) {
//            viewModel.getCapturedFaceImage()?.let { bitmap ->
//                scope.launch {
//                    try {
//                        viewModel.setLoading(true)
//
//                        val imageFile = bitmapToFile(context, bitmap)
//                        Log.d("Camera", "attendancePick: $imageFile")
//
//                        val response = authApi.attendancePick(imageFile)
//                        attendanceResponse = response
//
//                        if (response != null && response.success == true) {
//                            viewModel.setAttendanceResponse(response)
//                            val responseJson = gson.toJson(response)
//
//                            val bioId = response.data?.userDetails?.bioId?.toString() ?: ""
//                            if (bioId.isNotEmpty()) {
//                                val attendanceResult = authApi.doAddAttendance(bioId)
//                                if (attendanceResult != null) {
//                                    attendanceDataResponse = attendanceResult
//                                    navigationEvent =
//                                        NavigationEvent.NavigateWithResponse(responseJson)
//                                } else {
//                                    createAlertDialog(
//                                        "Server Error",
//                                        "Unable to record attendance. Please try again later.",
//                                        "OK",
//                                        "",
//                                        { navigationEvent = NavigationEvent.NavigateToHome },
//                                        {}
//                                    )
//                                }
//                            } else {
//                                createAlertDialog(
//                                    "Data Error",
//                                    "Bio ID not found. Please try again.",
//                                    "OK",
//                                    "",
//                                    { navigationEvent = NavigationEvent.NavigateToHome },
//                                    {}
//                                )
//                            }
//                        } else {
//                            // API failed – save image locally
//                            val savedUri = saveImageToStorage(context, bitmap)
//                            if (savedUri != null) {
//                                Toast.makeText(
//                                    context,
//                                    "Attendance failed. Image saved for review.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//
//                            createAlertDialog(
//                                "Attendance Failed",
//                                "Unable to mark attendance. Please try again later.",
//                                "OK",
//                                "",
//                                { navigationEvent = NavigationEvent.NavigateToHome },
//                                {}
//                            )
//                        }
//                    } catch (e: SocketTimeoutException) {
//                        createAlertDialog(
//                            "Network Timeout",
//                            "Internet connection seems unstable. Please try again.",
//                            "OK",
//                            "",
//                            { navigationEvent = NavigationEvent.NavigateToHome },
//                            {}
//                        )
//                    } catch (e: IOException) {
////                        createAlertDialog(
////                            "No Internet Connection",
////                            "Please check your internet connection and try again.",
////                            "OK",
////                            "",
////                            { navigationEvent = NavigationEvent.NavigateToHome },
////                            {}
////                        )
//                    } catch (e: Exception) {
//                        val savedUri = saveImageToStorage(context, bitmap)
//                        if (savedUri != null) {
//                            Toast.makeText(
//                                context,
//                                "Error occurred. Image saved for review.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        createAlertDialog(
//                            "Error",
//                            "An unexpected error occurred. Please try again.",
//                            "OK",
//                            "",
//                            { navigationEvent = NavigationEvent.NavigateToHome },
//                            {}
//                        )
//                    } finally {
//                        viewModel.setLoading(false)
//                        viewModel.resetFaceCenteredState()
//                    }
//                }
//            }
//        }
//    }
//
//    // ------------------------------------------------------------------ UI
//    DelayedVisibility(cameraPermissionStatus.value) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { ctx ->
//                FaceDetectionOverlay(lifecycleOwner, ctx, viewModel).also {
//                    faceDetectionOverlay.value = it
//                }
//            },
//            update = { it.initializeCamera(cameraFacing.value) }
//        )
//    }
//
//    DelayedVisibility(!cameraPermissionStatus.value) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                "Allow Camera and Storage Permissions\nThe app cannot work without these permissions.",
//                textAlign = TextAlign.Center
//            )
//            Button(
//                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) { Text("Allow") }
//        }
//    }
//
//    // Loading overlay (kept from original)
//    if (viewModel.isLoadingState.collectAsState().value) {
//        AlertDialog(
//            onDismissRequest = {},
//            title = { Text("Processing") },
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Capturing and processing face image...")
//                    Spacer(modifier = Modifier.height(16.dp))
//                    CircularProgressIndicator(
//                        color = Color.Black,
//                        strokeWidth = 3.dp,
//                        modifier = Modifier.size(40.dp)
//                    )
//                }
//            },
//            confirmButton = {}
//        )
//    }
//
//}

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@OptIn(ExperimentalGetImage::class)
@Composable
private fun Camera(
    viewModel: DetectScreenViewModel,
    context: Context,
    scope: CoroutineScope,
    navController: NavController,
    cameraPermissionStatus: MutableState<Boolean>,
    cameraFacing: MutableState<Int>,
    isConnected: Boolean
) {
    var navigationEvent by remember { mutableStateOf<NavigationEvent>(NavigationEvent.None) }
    val authApi = AuthApi.getInstance(context)
    val gson = Gson()
    var attendanceResponse by remember { mutableStateOf<AttendanceModel?>(null) }
    var attendanceDataResponse by remember { mutableStateOf<BackendModel?>(null) }
    val isFakeUser by viewModel.isFakeUserState.collectAsState()
    val isFaceCentered by viewModel.isFaceCenteredState.collectAsState()
    val speedTracker = remember { NetworkSpeedTracker(context) }

    val capturedFaceImage by viewModel.getCapturedFaceImage()
        ?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)

    val faceDetectionOverlay = remember { mutableStateOf<FaceDetectionOverlay?>(null) }

    // ✅ Add state to control capture process
    var shouldStopCapture by remember { mutableStateOf(false) }

    // Permission checks
    cameraPermissionStatus.value =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionStatus.value = isGranted
        if (!isGranted) camaraPermissionDialog()
    }

    var showNoInternetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val (download, upload, type) = speedTracker.getNetworkSpeed()
            showNoInternetDialog = (type == "No Connection")
            delay(1000)
        }
    }

    if (showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("No Internet Connection") },
            text = { Text("Your device is not connected to the internet. Please check your connection.") },
            confirmButton = {
                Button(onClick = {
                    showNoInternetDialog = false
                    shouldStopCapture = true // ✅ Stop capture
                    navigationEvent = NavigationEvent.NavigateToHome
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Navigation handler
    LaunchedEffect(navigationEvent) {
        when (val event = navigationEvent) {
            is NavigationEvent.NavigateBack -> {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigate("dashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
            is NavigationEvent.NavigateWithResponse -> {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "attendanceResponse",
                    event.responseJson
                )
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigate("dashboard")
                }
            }
            is NavigationEvent.NavigateToHome -> {
                navController.navigate("dashboard") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
            NavigationEvent.None -> {}
        }
        navigationEvent = NavigationEvent.None
    }

    // Core face detection logic
    LaunchedEffect(isFaceCentered, isFakeUser, capturedFaceImage, isConnected, shouldStopCapture) {
        // ✅ Stop processing if capture should stop
        if (shouldStopCapture) {
            return@LaunchedEffect
        }

        Log.d("Camera", "centered=$isFaceCentered fake=$isFakeUser connected=$isConnected")

        // SPOOF detection
        if (isFakeUser) {
            AuthApi.spoofAttempts++
            if (AuthApi.spoofAttempts > 3) {
                Toast.makeText(
                    context,
                    "Access blocked due to multiple spoof attempts. Please contact the developer.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Spoof detected ${AuthApi.spoofAttempts} time(s). Please face the camera properly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            shouldStopCapture = true // ✅ Stop capture
            navigationEvent = NavigationEvent.NavigateToHome
            return@LaunchedEffect
        }

        // No internet check
        if (!isConnected) {
            return@LaunchedEffect
        }

        // Face centered & image ready
        if (isFaceCentered && cameraPermissionStatus.value && capturedFaceImage != null) {
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
                            val responseJson = gson.toJson(response)

                            val bioId = response.data?.userDetails?.bioId?.toString() ?: ""
//                            if (bioId.isNotEmpty()) {
//                                val attendanceResult = authApi.doAddAttendance(bioId)
//                                if (attendanceResult != null) {
//                                    attendanceDataResponse = attendanceResult
//                                    navigationEvent = NavigationEvent.NavigateWithResponse(responseJson)
//                                } else {
//                                    shouldStopCapture = true // ✅ Stop capture before showing dialog
//                                    createAlertDialog(
//                                        "Server Error",
//                                        "Unable to record attendance. Please try again later.",
//                                        "OK",
//                                        "",
//                                        onPositiveButtonClick = {
//                                            navigationEvent = NavigationEvent.NavigateToHome
//                                        },
//                                        onNegativeButtonClick = {}
//                                    )
//                                }
//                            } else {
//                                shouldStopCapture = true // ✅ Stop capture before showing dialog
//                                createAlertDialog(
//                                    "Data Error",
//                                    "Bio ID not found. Please try again.",
//                                    "OK",
//                                    "",
//                                    onPositiveButtonClick = {
//                                        navigationEvent = NavigationEvent.NavigateToHome
//                                    },
//                                    onNegativeButtonClick = {}
//                                )
//                            }


                            if (bioId.isNotEmpty()) {
                                val result = authApi.doAddAttendance(bioId)

                                if (result.data != null) {
                                    attendanceDataResponse = result.data
                                    navigationEvent = NavigationEvent.NavigateWithResponse(responseJson)
                                } else {
                                    shouldStopCapture = true // ✅ Stop capture before showing dialog
                                    createAlertDialog(
                                        "Server Error",
                                        result.errorMessage ?: "Unable to record attendance. Please try again later.",
                                        "OK",
                                        "",
                                        onPositiveButtonClick = {
                                            navigationEvent = NavigationEvent.NavigateToHome
                                        },
                                        onNegativeButtonClick = {}
                                    )
                                }
                            } else {
                                shouldStopCapture = true
                                createAlertDialog(
                                    "Data Error",
                                    "Bio ID not found. Please try again.",
                                    "OK",
                                    "",
                                    onPositiveButtonClick = {
                                        navigationEvent = NavigationEvent.NavigateToHome
                                    },
                                    onNegativeButtonClick = {}
                                )
                            }

                        } else {
                            // API failed – save image locally
                            val savedUri = saveImageToStorage(context, bitmap)
                            if (savedUri != null) {
                                Toast.makeText(
                                    context,
                                    "Attendance failed. Image saved for review.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            shouldStopCapture = true // ✅ Stop capture before showing dialog
                            createAlertDialog(
                                "Attendance Failed",
                                "Unable to mark attendance. Please try again later.",
                                "OK",
                                "",
                                onPositiveButtonClick = {
                                    navigationEvent = NavigationEvent.NavigateToHome
                                },
                                onNegativeButtonClick = {}
                            )
                        }
                    } catch (e: SocketTimeoutException) {
                        shouldStopCapture = true // ✅ Stop capture before showing dialog
                        createAlertDialog(
                            "Network Timeout",
                            "Internet connection seems unstable. Please try again.",
                            "OK",
                            "",
                            onPositiveButtonClick = {
                                navigationEvent = NavigationEvent.NavigateToHome
                            },
                            onNegativeButtonClick = {}
                        )
                    } catch (e: IOException) {
                        // Network error - handled by connection state
                    } catch (e: Exception) {
                        val savedUri = saveImageToStorage(context, bitmap)
                        if (savedUri != null) {
                            Toast.makeText(
                                context,
                                "Error occurred. Image saved for review.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        shouldStopCapture = true // ✅ Stop capture before showing dialog
                        createAlertDialog(
                            "Error",
                            "An unexpected error occurred. Please try again.",
                            "OK",
                            "",
                            onPositiveButtonClick = {
                                navigationEvent = NavigationEvent.NavigateToHome
                            },
                            onNegativeButtonClick = {}
                        )
                    } finally {
                        viewModel.setLoading(false)
                        viewModel.resetFaceCenteredState()
                    }
                }
            }
        }
    }

    // UI Components remain the same...
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
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("Allow") }
        }
    }

    if (viewModel.isLoadingState.collectAsState().value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Processing") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Capturing and processing face image...")
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
            confirmButton = {}
        )
    }
}
/* -------------------------------------------------------------------------- */
private fun camaraPermissionDialog() {
    createAlertDialog(
        "Camera Permission",
        "The app couldn't function without the camera permission.",
        "ALLOW",
        "CLOSE",
        onPositiveButtonClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        onNegativeButtonClick = {}
    )
}

/* -------------------------------------------------------------------------- */
private fun bitmapToFile(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "face_image_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) }
    return file
}

/* -------------------------------------------------------------------------- */
private fun saveImageToStorage(context: Context, bitmap: Bitmap): Uri? {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val filename = "FaceCapture_$timestamp.jpg"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FaceApp")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                it
            } catch (e: Exception) {
                Log.e("SaveImage", "Failed: ${e.message}")
                null
            }
        }
    } else {
        try {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "FaceApp"
            ).also { it.mkdirs() }
            val file = File(dir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                out.flush()
            }
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("SaveImage", "Failed: ${e.message}")
            null
        }
    }
}

@Composable
fun NetworkSpeedIndicator(speedTracker: NetworkSpeedTracker) {
    var downloadSpeed by remember { mutableStateOf("0 B/s") }
    var uploadSpeed by remember { mutableStateOf("0 B/s") }
    var networkType by remember { mutableStateOf("Unknown") }
//    var showNoInternetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val (download, upload, type) = speedTracker.getNetworkSpeed()
            downloadSpeed = download
            uploadSpeed = upload
            networkType = type

            // Show dialog if no connection
//            showNoInternetDialog = (type == "No Connection")

            delay(1000) // check every second
        }
    }

//    if (showNoInternetDialog) {
//AlertDialog(
//            onDismissRequest = {},
//            title = { Text("No Internet Connection") },
//            text = { Text("Your device is not connected to the internet. Please check your connection.") },
//            confirmButton = {
//                Button(onClick = { showNoInternetDialog = false;
//
//                }) {
//                    Text("OK")
//                }
//            }
//        )
//    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when(networkType) {
                        "WiFi" -> Icons.Default.Wifi
                        "Mobile" -> Icons.Default.SignalCellularAlt
                        else -> Icons.Default.SignalCellularConnectedNoInternet0Bar
                    },
                    contentDescription = "Network Type",
                    tint = if (networkType != "No Connection") Color.Green else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = networkType,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Download",
                    tint = Color.Cyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = downloadSpeed,
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Upload",
                    tint = Color.Yellow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = uploadSpeed,
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }
    }
}
