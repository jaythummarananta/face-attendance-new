//package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen
//
//import android.Manifest
//import android.content.pm.PackageManager
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.OptIn
//import androidx.camera.core.CameraSelector
//
//import androidx.camera.core.ExperimentalGetImage
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Cameraswitch
//import androidx.compose.material.icons.filled.Face
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.app.ActivityCompat
//import com.ml.shubham0204.facenet_android.R
//import com.ml.shubham0204.facenet_android.presentation.components.AppAlertDialog
//import com.ml.shubham0204.facenet_android.presentation.components.DelayedVisibility
//import com.ml.shubham0204.facenet_android.presentation.components.FaceDetectionOverlay
//import com.ml.shubham0204.facenet_android.presentation.components.createAlertDialog
//import com.ml.shubham0204.facenet_android.presentation.theme.FaceNetAndroidTheme
//import org.koin.androidx.compose.koinViewModel
//
//private val cameraPermissionStatus = mutableStateOf(false)
//private val cameraFacing = mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
//private lateinit var cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
//
//@kotlin.OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DetectScreen(onOpenFaceListClick: (() -> Unit)) {
//    FaceNetAndroidTheme {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            topBar = {
//                TopAppBar(
//                    colors = TopAppBarDefaults.topAppBarColors(),
//                    title = {
//                        Text(
//                            text = stringResource(id = R.string.app_name),
//                            style = MaterialTheme.typography.headlineSmall
//                        )
//                    },
//                    actions = {
//                        IconButton(onClick = onOpenFaceListClick) {
//                            Icon(
//                                imageVector = Icons.Default.Face,
//                                contentDescription = "Open Face List"
//                            )
//                        }
//                        IconButton(
//                            onClick = {
//                                if (cameraFacing.intValue == CameraSelector.LENS_FACING_BACK) {
//                                    cameraFacing.intValue = CameraSelector.LENS_FACING_FRONT
//                                } else {
//                                    cameraFacing.intValue = CameraSelector.LENS_FACING_BACK
//                                }
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Cameraswitch,
//                                contentDescription = "Switch Camera"
//                            )
//                        }
//                    }
//                )
//            }
//        ) { innerPadding ->
//            Column(modifier = Modifier.padding(innerPadding)) { ScreenUI() }
//        }
//    }
//}
//
//@Composable
//private fun ScreenUI() {
//    val viewModel: DetectScreenViewModel = koinViewModel()
//    Box {
//        Camera(viewModel)
//        DelayedVisibility(true) {
//            val metrics by remember{ viewModel.faceDetectionMetricsState }
//            Column {
//                Text(
//                    text = "Recognition on ${viewModel.getNumPeople()} face(s)",
//                    color = Color.White,
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                metrics?.let {
//                    Text(
//                        text = "face detection: ${it.timeFaceDetection} ms" +
//                                "\nface embedding: ${it.timeFaceEmbedding} ms" +
//                                "\nvector search: ${it.timeVectorSearch} ms\n" +
//                                "spoof detection: ${it.timeFaceSpoofDetection} ms",
//                        color = Color.White,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 24.dp),
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
////        DelayedVisibility(viewModel.getNumPeople() == 0L) {
////            Text(
////                text = "No images in database",
////                color = Color.White,
////                modifier =
////                Modifier
////                    .fillMaxWidth()
////                    .padding(horizontal = 16.dp, vertical = 8.dp)
////                    .background(Color.Blue, RoundedCornerShape(16.dp))
////                    .padding(8.dp),
////                textAlign = TextAlign.Center
////            )
////        }
//        AppAlertDialog()
//    }
//}
//
//@OptIn(ExperimentalGetImage::class)
//@Composable
//private fun Camera(viewModel: DetectScreenViewModel) {
//    val context = LocalContext.current
//    cameraPermissionStatus.value =
//        ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
//            PackageManager.PERMISSION_GRANTED
//    val cameraFacing by remember { cameraFacing }
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    cameraPermissionLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if (it) {
//                cameraPermissionStatus.value = true
//            } else {
//                camaraPermissionDialog()
//            }
//        }
//
//    DelayedVisibility(cameraPermissionStatus.value) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { FaceDetectionOverlay(lifecycleOwner, context, viewModel) },
//            update = { it.initializeCamera(cameraFacing) }
//        )
//    }
//    DelayedVisibility(!cameraPermissionStatus.value) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                "Allow Camera Permissions\nThe app cannot work without the camera permission.",
//                textAlign = TextAlign.Center
//            )
//            Button(
//                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text(text = "Allow")
//            }
//        }
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
//            //       close the app
//        }
//    )
//}
package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen

import ShowCustomAlertDialog
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.benchmark.perfetto.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.ml.shubham0204.facenet_android.ApiRepo.AuthApi
import com.ml.shubham0204.facenet_android.ApiRepo.UserFaceAuthModel
import com.ml.shubham0204.facenet_android.presentation.components.AppAlertDialog
import com.ml.shubham0204.facenet_android.presentation.components.DelayedVisibility
import com.ml.shubham0204.facenet_android.presentation.components.FaceDetectionOverlay
import com.ml.shubham0204.facenet_android.presentation.components.createAlertDialog
import com.ml.shubham0204.facenet_android.presentation.theme.FaceNetAndroidTheme
import kotlinx.coroutines.CoroutineScope
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
    var attendanceResponse by remember { mutableStateOf<UserFaceAuthModel?>(null) }

    FaceNetAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(),
                    title = {
                        Text(
                            text = "Attendance",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigate) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            }
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
    var isShowFakeUserDialog by remember { mutableStateOf(false) }
    val authApi = AuthApi.getInstance(context)
    var attendanceResponse by remember { mutableStateOf<UserFaceAuthModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val isFakeUser by viewModel.isFakeUserState.collectAsState()
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
                // Real face detected - proceed with authentication
                isShowDialog = true
                viewModel.getCapturedFaceImage()?.let { bitmap ->
                    scope.launch {
                        try {
                            viewModel.setLoading(true)
                            val imageFile = bitmapToFile(context, bitmap)
                            Log.d("Camera", "attendancePick: $imageFile")
                            // Uncomment if you want to call the API
                            val response = authApi.attendancePick(imageFile)
                            attendanceResponse = response
                            viewModel.setAttendanceResponse(response)
                        } catch (e: Exception) {
                            Log.d("Camera", "attendancePick failed: ${e.message}")
                        } finally {
                            viewModel.setLoading(false)
                        }
                    }
                }
            }

            isFakeUser -> {
                // Fake face detected - show warning dialog
                isShowFakeUserDialog = true
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

    // Fake user detection dialog with enhanced styling
    if (isShowFakeUserDialog) {
        AlertDialog(
            onDismissRequest = {
                isShowFakeUserDialog = false
                viewModel.resetState()
                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
            },
            title = { Text("Processing") },

            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "⚠️ Spoof/Fake face detected!\n\nPlease use your real face for authentication.",
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isShowFakeUserDialog = false
                        viewModel.resetState()
                        faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Try Again", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isShowFakeUserDialog = false
                        viewModel.resetState()
                        faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
                        onNavigateToHome()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
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

    // Response dialog
    attendanceResponse?.let { response ->
        ShowCustomAlertDialog(
            response = response,
            onDismissRequest = {
                attendanceResponse = null
                viewModel.resetState()
                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
                onNavigateToHome()
            },
            onConfirm = {
                viewModel.resetState()
                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value)
                onNavigateToHome()
            }
        )
    }
}
//@Composable
//private fun Camera(
//    viewModel: DetectScreenViewModel,
//    context: Context,
//    scope: CoroutineScope,
//    onNavigateToHome: () -> Unit,
//    cameraPermissionStatus: MutableState<Boolean>,
//    cameraFacing: MutableState<Int>
//) {
//    var isShowDialog by remember { mutableStateOf(false) }
//    val authApi = AuthApi.getInstance(context)
//    var attendanceResponse by remember { mutableStateOf<UserFaceAuthModel?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    val isFakeUser by viewModel.isFakeUserState.collectAsState()
//    val capturedFaceImage by viewModel.getCapturedFaceImage()?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)
//    val faceDetectionOverlay = remember { mutableStateOf<FaceDetectionOverlay?>(null) }
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
//    val isFakeReal by viewModel.isFakeUserState.collectAsState()
//
//    LaunchedEffect(isReal, capturedFaceImage) {
//        if (isReal && cameraPermissionStatus.value && capturedFaceImage != null) {
//            // Show dialog with the detected face image
//            isShowDialog = true
//            viewModel.getCapturedFaceImage()?.let { bitmap ->
//                scope.launch {
//                    try {
//                        viewModel.setLoading(true)
//                        val imageFile = bitmapToFile(context, bitmap)
//                        Log.d("Camera", "attendancePick: $imageFile")
//                        // Uncomment if you want to call the API
//                         val response = authApi.attendancePick(imageFile)
//                         attendanceResponse = response
//                         viewModel.setAttendanceResponse(response)
//                        // Log.d("Camera", "CameraattendancePick: $response")
//                    } catch (e: Exception) {
//                        Log.d("Camera", "attendancePick failed: ${e.message}")
//                    } finally {
//                        viewModel.setLoading(false)
//                    }
//                }
//            }
//        } else if (!isReal && cameraPermissionStatus.value) {
//            // Trigger fake user dialog
//            viewModel.setFakeUser(true)
//        }
//    }
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
//    if (viewModel.getLoading()) {
//        AlertDialog(
//            onDismissRequest = { /* Prevent dismissing while loading */ },
//            confirmButton = {},
//            title = { Text("Processing") },
//            text = { Text("Capturing and processing face image...") }
//        )
//    }
//
//    // Dialog to show detected face image
////    if (isShowDialog && capturedFaceImage != null) {
////        AlertDialog(
////            onDismissRequest = {
////                isShowDialog = false
////                viewModel.setShowDialog(false)
////                viewModel.resetState() // Reset ViewModel state
////                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value) // Reinitialize camera
////                onNavigateToHome() // Navigate to previous screen
////            },
////            title = { Text("Face Detected") },
////            text = {
////                Column(
////                    modifier = Modifier.fillMaxWidth(),
////                    horizontalAlignment = Alignment.CenterHorizontally
////                ) {
////                    Image(
////                        bitmap = capturedFaceImage!!.asImageBitmap(),
////                        contentDescription = "Detected face",
////                        modifier = Modifier
////                            .size(200.dp)
////                            .clip(RoundedCornerShape(8.dp))
////                    )
////                    Spacer(modifier = Modifier.height(16.dp))
////                    Text("Real face detected successfully!")
////                }
////            },
////            confirmButton = {
////                Button(
////                    onClick = {
////                        isShowDialog = false
////                        viewModel.setShowDialog(false)
////                        viewModel.resetState() // Reset ViewModel state
////                        faceDetectionOverlay.value?.initializeCamera(cameraFacing.value) // Reinitialize camera
////                        onNavigateToHome() // Navigate to previous screen
////                    }
////                ) {
////                    Text("OK")
////                }
////            },
////            dismissButton = {
////                TextButton(
////                    onClick = {
////                        isShowDialog = false
////                        viewModel.setShowDialog(false)
////                        viewModel.resetState() // Reset ViewModel state
////                        faceDetectionOverlay.value?.initializeCamera(cameraFacing.value) // Reinitialize camera
////                        onNavigateToHome() // Navigate to previous screen
////                    }
////                ) {
////                    Text("Cancel")
////                }
////            }
////        )
////    }
//
//    // Response dialog
//    attendanceResponse?.let { response ->
//        ShowCustomAlertDialog(
//            response = response,
//            onDismissRequest = {
//                attendanceResponse = null
//                viewModel.resetState() // Reset ViewModel state
//                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value) // Reinitialize camera
//                onNavigateToHome()
//            },
//            onConfirm = {
//                viewModel.resetState() // Reset ViewModel state
//                faceDetectionOverlay.value?.initializeCamera(cameraFacing.value) // Reinitialize camera
//                onNavigateToHome()
//            }
//        )
//    }
//}
//@Composable
//private fun Camera(
//    viewModel: DetectScreenViewModel,
//    context: Context,
//    scope: CoroutineScope,
//    onNavigateToHome: () -> Unit,
//    cameraPermissionStatus: MutableState<Boolean>,
//    cameraFacing: MutableState<Int>
//) {
//    var isShowDialog by remember { mutableStateOf(false) }
//    val authApi = AuthApi.getInstance(context)
//    var attendanceResponse by remember { mutableStateOf<UserFaceAuthModel?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    val isFakeUser by viewModel.isFakeUserState.collectAsState()
//    val capturedFaceImage by viewModel.getCapturedFaceImage()?.let { remember { mutableStateOf(it) } } ?: mutableStateOf(null)
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
//    val isFakeReal by viewModel.isFakeUserState.collectAsState()
//
//    LaunchedEffect(isReal, capturedFaceImage) {
//        if (isReal && cameraPermissionStatus.value && capturedFaceImage != null) {
//            // Show dialog with the detected face image
//            isShowDialog = true
//            viewModel.getCapturedFaceImage()?.let { bitmap ->
//                scope.launch {
//                    try {
//                        viewModel.setLoading(true)
//                        val imageFile = bitmapToFile(context, bitmap)
//                        Log.d("Camera", "attendancePick: $imageFile")
//                        // Uncomment if you want to call the API
//                        // val response = authApi.attendancePick(imageFile)
//                        // attendanceResponse = response
//                        // viewModel.setAttendanceResponse(response)
//                        // Log.d("Camera", "CameraattendancePick: $response")
//                    } catch (e: Exception) {
//                        Log.d("Camera", "attendancePick failed: ${e.message}")
//                    } finally {
//                        viewModel.setLoading(false)
//                    }
//                }
//            }
//        } else if (!isReal && cameraPermissionStatus.value) {
//            // Trigger fake user dialog
//            viewModel.setFakeUser(true)
//        }
//    }
//
//    // UI
//    DelayedVisibility(cameraPermissionStatus.value) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { FaceDetectionOverlay(lifecycleOwner, context, viewModel) },
//            update = { it.initializeCamera(cameraFacing.value) }
//        )
//    }
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
//
//    // Dialog to show detected face image
//    if (isShowDialog && capturedFaceImage != null) {
//        AlertDialog(
//            onDismissRequest = {
//                isShowDialog = false
//                viewModel.setShowDialog(false)
//            },
//            title = { Text("Face Detected") },
//            text = {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(
//                        bitmap = capturedFaceImage!!.asImageBitmap(),
//                        contentDescription = "Detected face",
//                        modifier = Modifier
//                            .size(200.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text("Real face detected successfully!")
//                }
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        isShowDialog = false
//                        viewModel.setShowDialog(false)
//                        // Optionally navigate to home or proceed
//                        // onNavigateToHome()
//                    }
//                ) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        isShowDialog = false
//                        viewModel.setShowDialog(false)
//                    }
//                ) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//
//    // Response dialog
//    attendanceResponse?.let { response ->
//        ShowCustomAlertDialog(
//            response = response,
//            onDismissRequest = {
//                attendanceResponse = null
//                onNavigateToHome()
//            },
//            onConfirm = { onNavigateToHome() }
//        )
//    }
//}
//@Composable
//private fun Camera(
//    viewModel: DetectScreenViewModel,
//    context: Context,
//    scope: CoroutineScope,
//    onNavigateToHome: () -> Unit,
//    cameraPermissionStatus: MutableState<Boolean>,
//    cameraFacing: MutableState<Int>
//) {
//
//    var isShowDialog by remember { mutableStateOf(false) }
//    val authApi = AuthApi.getInstance(context)
//    var attendanceResponse by remember { mutableStateOf<UserFaceAuthModel?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//    val isFakeUser by viewModel.isFakeUserState.collectAsState()
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
//    val isFakeReal by viewModel.isFakeUserState.collectAsState()
//
//    LaunchedEffect(isReal) {
//        if (isReal && cameraPermissionStatus.value) {
//            viewModel.getCapturedFaceImage()?.let { bitmap ->
//
//                scope.launch {
//                    try {
//                        viewModel.setLoading(true)
//                        val imageFile = bitmapToFile(context, bitmap)
//                        Log.d("Camera", "attendancePick: $imageFile")
////                        val response = authApi.attendancePick(imageFile)
////                        attendanceResponse = response
////                        viewModel.setAttendanceResponse(response)
//
////                        Log.d("Camera", "CameraattendancePick: $response")
//                    } catch (e: Exception) {
//                        Log.d("Camera", "attendancePick failed: ${e.message}")
//                    } finally {
//                        viewModel.setLoading(false)
//                        viewModel.setShowDialog(false)
//
//
//                    }
//                }
//            }
//        }
//        else if (!isReal && cameraPermissionStatus.value) {
//            // Trigger fake user dialog
//            viewModel.setFakeUser(true)
//        }
//    }
//
//
//    // UI
//    DelayedVisibility(cameraPermissionStatus.value) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { FaceDetectionOverlay(lifecycleOwner, context, viewModel) },
//            update = { it.initializeCamera(cameraFacing.value) }
//        )
//    }
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
//
////    // Loading dialog
//    if (isLoading) {
//        AlertDialog(
//            onDismissRequest = { /* Prevent dismissing while loading */ },
//            confirmButton = {},
//            title = { Text("Processing") },
//            text = { Text("Capturing and processing face image...") }
//        )
//    }
//
//    // Response dialog
//    attendanceResponse?.let { response ->
//
//        ShowCustomAlertDialog(
//            response = response,
//            onDismissRequest = {
//                attendanceResponse = null
//                onNavigateToHome()
//            },
//            onConfirm = { onNavigateToHome() }
//        )
//    }
//}

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