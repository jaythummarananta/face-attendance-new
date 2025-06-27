package com.ml.shubham0204.facenet_android.presentation.screens.add_face

//@Composable
//fun FaceDetectionPage(navController: NavController) {
//    var faceFound by remember { mutableStateOf(false) }
//    var isProcessing by remember { mutableStateOf(false) }
//    var takeImage by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    // Show error toast if any
//    LaunchedEffect(errorMessage) {
//        errorMessage?.let {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            errorMessage = null
//            navController.popBackStack() // Navigate back on error
//        }
//    }
//
//    // Overlay Composable
//    @Composable
//    fun Overlay() {
//        Box(modifier = Modifier.fillMaxSize()) {
//            // Top overlay
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(216.dp) // 30% of 720dp
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .align(Alignment.TopCenter)
//            )
//            // Bottom overlay
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(216.dp) // 30% of 720dp
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .align(Alignment.BottomCenter)
//            )
//            // Left overlay
//            Box(
//                modifier = Modifier
//                    .width(36.dp) // 10% of 360dp
//                    .height(288.dp) // 40.01% of 720dp
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .align(Alignment.CenterStart)
//            )
//            // Right overlay
//            Box(
//                modifier = Modifier
//                    .width(36.dp) // 10% of 360dp
//                    .height(288.dp) // 40.01% of 720dp
//                    .background(Color.Black.copy(alpha = 0.6f))
//                    .align(Alignment.CenterEnd)
//            )
//            // Center detection area
//            Box(
//                modifier = Modifier
//                    .width(295.dp) // 82% of 360dp
//                    .height(295.dp) // 41% of 720dp
//                    .border(
//                        width = 5.dp,
//                        color = if (faceFound) Color.Green else Color.Red,
//                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
//                    )
//                    .align(Alignment.Center)
//            )
//            // Camera button
//            IconButton(
//                onClick = {
//                    if (faceFound && imageCapture != null) {
//                        takeImage = true
//                    } else if (imageCapture == null) {
//                        Toast.makeText(context, "Camera not ready", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 30.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.camera),
//                    contentDescription = "Capture",
//                    tint = if (faceFound && imageCapture != null) Color.White else Color.Red,
//                    modifier = Modifier.size(60.dp)
//                )
//            }
//        }
//    }
//
//    // Main Content
//    Box(modifier = Modifier.fillMaxSize()) {
//        if (isProcessing) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center)
//            )
//        } else {
//            FaceDetectionOverlay(
//                faceDetectorOptions = FaceDetectorOptions.Builder()
//                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
//                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
//                    .build(),
//                overlay = { Overlay() },
//                resultCallback = { faces ->
//                    if (faces.isNotEmpty()) {
//                        val width = 360f // Approximate screen width in dp
//                        val height = 720f // Approximate screen height in dp
//                        val xPositionStart = width * 0.15f
//                        val xPositionEnd = width * (1f - 0.15f)
//                        val yPositionStart = height * 0.30f
//                        val yPositionEnd = height * (1f - 0.30f)
//
//                        val face = faces.first()
//                        val boundingBox = face.getBoundingBox()
//                        if (boundingBox != null &&
//                            boundingBox.left >= xPositionStart &&
//                            boundingBox.right <= xPositionEnd &&
//                            boundingBox.top >= yPositionStart &&
//                            boundingBox.bottom <= yPositionEnd) {
//                            faceFound = true
//                            if (takeImage && imageCapture != null) {
//                                captureAndCropImage(
//                                    context,
//                                    navController,
//                                    imageCapture!!,
//                                    { isProcessing = it }
//                                )
//                                takeImage = false
//                            }
//                        } else {
//                            faceFound = false
//                            takeImage = false
//                        }
//                    } else {
//                        faceFound = false
//                    }
//                },
//                onImageCaptureCreated = { capture ->
//                    imageCapture = capture
//                },
//                onError = { message ->
//                    errorMessage = message
//                },
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//}
//
//private fun captureAndCropImage(
//    context: Context,
//    navController: NavController,
//    imageCapture: ImageCapture,
//    setProcessing: (Boolean) -> Unit
//) {
//    setProcessing(true)
//    val tempDir = context.getExternalFilesDir(null)
//    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
//    val file = File(tempDir, "cropped_face_$timestamp.png")
//
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
//
//    imageCapture.takePicture(
//        outputOptions,
//        Executors.newSingleThreadExecutor(),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                // Crop the image
//                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
//                if (bitmap != null) {
//                    val cropWidth = (bitmap.width * 0.74f).toInt()
//                    val cropHeight = (bitmap.height * 0.38f).toInt()
//                    val offsetX = (bitmap.width - cropWidth) / 2
//                    val offsetY = (bitmap.height - cropHeight) / 2
//
//                    val croppedBitmap = Bitmap.createBitmap(
//                        bitmap,
//                        offsetX,
//                        offsetY,
//                        cropWidth,
//                        cropHeight
//                    )
//
//                    FileOutputStream(file).use { out ->
//                        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                    }
//                    croppedBitmap.recycle()
//                    bitmap.recycle()
//
//                    navController.previousBackStackEntry?.savedStateHandle?.set("image_path", file.absolutePath)
//                    navController.popBackStack()
//
//                    Log.d("FaceDetection", "Cropped image saved to: ${file.absolutePath}")
//                } else {
//                    Log.e("FaceDetection", "Failed to decode bitmap")
//                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
//                }
//                setProcessing(false)
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Log.e("FaceDetection", "Image capture failed: $exception")
//                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
//                setProcessing(false)
//            }
//        }
//    )
//}

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.ml.shubham0204.facenet_android.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun FaceDetectionPage(navController: NavController, imageIndex: Int) {

    var faceFound by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var takeImage by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    var isPermissionRequested by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isPermissionRequested = false
        if (isGranted) {
            hasPermission = true
        } else {
            errorMessage = "Camera permission denied"
        }
    }

    // Check camera permission on launch
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasPermission = true
        } else if (!isPermissionRequested) {
            isPermissionRequested = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show error toast if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            errorMessage = null
            coroutineScope.launch(Dispatchers.Main) {
                Log.d(
                    "FaceDetection",
                    "Navigating back from error, Thread: ${Thread.currentThread().name}"
                )
                navController.popBackStack()
            }
        }
    }

    // Overlay Composable
    @Composable
    fun Overlay() {
        Box(modifier = Modifier.fillMaxHeight()) {
            // Top overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.32f) // 30% of 720dp
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.TopCenter)
            )
            // Bottom overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.32f) // 30% of 720dp
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.BottomCenter)
            )
            // Left overlay
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.1f)
                    .height(screenHeight * 0.360f)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.CenterStart)
            )
            // Right overlay
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.1f)
                    .height(screenHeight * 0.360f)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.CenterEnd)
            )
            // Center detection area
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.82f)
                    .height(screenWidth * 0.82f)
                    .border(
                        width = 5.dp,
                        color = if (faceFound) Color.Green else Color.Red,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )
                    .align(Alignment.Center)
            )
            // Camera button
            IconButton(
                onClick = {
                    if (faceFound && imageCapture != null) {
                        takeImage = true
                    } else if (imageCapture == null) {
                        Toast.makeText(context, "Camera not ready", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = screenHeight * 0.120f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Capture",
                    tint = if (faceFound && imageCapture != null) Color.White else Color.Red,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }

    // Main Content
    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera permission required",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            }
        } else if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            FaceDetectionOverlay(
                faceDetectorOptions = FaceDetectorOptions.Builder()
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                    .build(),
                overlay = { Overlay() },
                resultCallback = { faces ->
                    if (faces.isNotEmpty()) {
                        val width = 360f // Approximate screen width in dp
                        val height = 720f // Approximate screen height in dp
                        val xPositionStart = width * 0.15f
                        val xPositionEnd = width * (1f - 0.15f)
                        val yPositionStart = height * 0.30f
                        val yPositionEnd = height * (1f - 0.30f)

                        val face = faces.first()
                        val boundingBox = face.boundingBox
                        if (boundingBox != null &&
                            boundingBox.left >= xPositionStart &&
                            boundingBox.right <= xPositionEnd &&
                            boundingBox.top >= yPositionStart &&
                            boundingBox.bottom <= yPositionEnd
                        ) {
                            faceFound = true
                            if (takeImage && imageCapture != null) {
                                captureAndCropImage(
                                    context = context,
                                    navController = navController,
                                    imageCapture = imageCapture!!,
                                    setProcessing = { isProcessing = it },
                                    coroutineScope = coroutineScope,
                                    imageIndex = imageIndex
                                )
                                takeImage = false
                            }
                        } else {
                            faceFound = false
                            takeImage = false
                        }
                    } else {
                        faceFound = false
                    }
                },
                onImageCaptureCreated = { capture ->
                    imageCapture = capture
                },
                onError = { message ->
                    errorMessage = message
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun FaceDetectionOverlay(
    faceDetectorOptions: FaceDetectorOptions,
    overlay: @Composable () -> Unit,
    resultCallback: (List<Face>) -> Unit,
    onImageCaptureCreated: (ImageCapture) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val faceDetector = remember { FaceDetection.getClient(faceDetectorOptions) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) { view ->
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(view.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder().build()
        onImageCaptureCreated(imageCapture)

        val imageAnalyzer = androidx.camera.core.ImageAnalysis.Builder()
            .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        faceDetector.process(inputImage)
                            .addOnSuccessListener { faces ->
                                resultCallback(faces)
                                imageProxy.close()
                            }
                            .addOnFailureListener { e ->
                                onError("Face detection failed: ${e.message}")
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture,
                imageAnalyzer
            )
        } catch (e: Exception) {
            onError("Camera binding failed: ${e.message}")
        }
    }

    overlay()
}

private fun captureAndCropImage(
    context: Context,
    navController: NavController,
    imageCapture: ImageCapture,
    setProcessing: (Boolean) -> Unit, coroutineScope: kotlinx.coroutines.CoroutineScope,
    imageIndex: Int = 0

) {
    setProcessing(true)

    val tempDir = context.getExternalFilesDir(null) ?: run {
        coroutineScope.launch(Dispatchers.Main) {
            Log.d("FaceDetection", "Storage unavailable, Thread: ${Thread.currentThread().name}")
            Toast.makeText(context, "Storage unavailable", Toast.LENGTH_SHORT).show()
            setProcessing(false)
        }
        return
    }
//    val tempDir = context.getExternalFilesDir(null)
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(tempDir, "cropped_face_$timestamp.png")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Crop the image
                var bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    val exif = ExifInterface(file.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    val rotationDegrees = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                    // Rotate bitmap if needed
                    val matrix = Matrix()
                    if (rotationDegrees != 0) {
                        matrix.postRotate(rotationDegrees.toFloat())
                        val rotatedBitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            matrix,
                            true
                        )
                        bitmap.recycle() // Recycle original bitmap
                        bitmap = rotatedBitmap // Use rotated bitmap for cropping
                    }
                    val cropWidth = (bitmap.width * 0.82f).toInt()
                    val cropHeight = (bitmap.height * 0.82f).toInt()
                    val offsetX = (bitmap.width - cropWidth) / 2
                    val offsetY = (bitmap.height - cropHeight) / 2

                    val croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        offsetX,
                        offsetY,
                        cropWidth,
                        cropHeight
                    )

                    FileOutputStream(file).use { out ->
                        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    croppedBitmap.recycle()
                    bitmap.recycle()
                    // Navigate back on main thread
                    coroutineScope.launch(Dispatchers.Main) {
                        Log.d("FaceDetection", "Navigating back, Thread: ${Thread.currentThread().name}")
                        navController.previousBackStackEntry?.savedStateHandle
                            ?.set("image_path_$imageIndex", file.absolutePath)
                        navController.popBackStack()
                        setProcessing(false)
                        Log.d("FaceDetection", "Cropped image saved to: ${file.absolutePath}")
                    }
                    Log.d("FaceDetection", "Cropped image saved to: ${file.absolutePath}")
                } else {
                    coroutineScope.launch(Dispatchers.Main) {
                        Log.d("FaceDetection", "Failed to decode bitmap, Thread: ${Thread.currentThread().name}")
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                        setProcessing(false)
                    }
                    Log.e("FaceDetection", "Failed to decode bitmap")
                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                }
                setProcessing(false)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("FaceDetection", "Image capture failed: $exception")
                coroutineScope.launch(Dispatchers.Main) {
                    Log.d("FaceDetection", "Image capture failed: $exception, Thread: ${Thread.currentThread().name}")
                    Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                    setProcessing(false)
                }
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                setProcessing(false)
            }
        }
    )
}