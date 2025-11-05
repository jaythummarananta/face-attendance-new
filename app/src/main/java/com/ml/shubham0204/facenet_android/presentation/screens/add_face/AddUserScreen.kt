package com.ananta.faceapp.presentation.screens.add_user

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ananta.faceapp.ApiRepo.AuthApi
import com.ananta.faceapp.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authApi = AuthApi.getInstance(context)
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp
    val appBgColor = remember {
        Color(ContextCompat.getColor(context, R.color.white))
    }
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // State variables
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var isAuthenticated by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bioId by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    val imagePaths = remember { mutableStateListOf<String?>(null, null, null, null) }
    var showCamera by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    var faceFound by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var takeImage by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    var isPermissionRequested by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isPermissionRequested = false
        if (isGranted) {
            hasPermission = true
            showCamera = true
        } else {
            errorMessage = "Camera permission denied"
        }
    }

    // Check camera permission on launch or when needed
    LaunchedEffect(selectedImageIndex) {
        if (selectedImageIndex != null && !hasPermission && !isPermissionRequested) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = true
                showCamera = true
            } else {
                isPermissionRequested = true
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Show error toast if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            errorMessage = null
            if (showCamera) {
                showCamera = false
                selectedImageIndex = null
            }
        }
    }

    // Face detection overlay
    @Composable
    fun FaceDetectionOverlay() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.98f)
                    .height(screenWidth * 0.98f)
                    .border(
                        width = 5.dp,
                        color = if (faceFound) Color.Green else Color.Red,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .align(Alignment.Center)
            )
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
//                    .padding(bottom = screenHeight * 0.12f)
//                    .size(70.dp) // Controls the whole button size
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.camera),
//                    contentDescription = "Capture",
//                    tint = if (faceFound && imageCapture != null) Color.White else Color.Red,
//                    modifier = Modifier.size(70.dp) // Controls only the icon size
//                )
//            }
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
                    .padding(bottom = screenHeight * 0.12f)
                    .size(80.dp)
                    .background(
                        color = if (faceFound && imageCapture != null) {
                            Color.White.copy(alpha = 0.3f)
                        } else {
                            Color.Red.copy(alpha = 0.3f)
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = 4.dp,
                        color = if (faceFound && imageCapture != null) Color.White else Color.Red,
                        shape = CircleShape
                    ),
                enabled = faceFound && imageCapture != null
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (faceFound && imageCapture != null) {
                                Color.White
                            } else {
                                Color.Red
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Capture",
                        tint = if (faceFound && imageCapture != null) {
                            Color.Black
                        } else {
                            Color.White
                        },
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    // Camera preview
    @Composable
    fun CameraPreview() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val faceDetector = remember {
            FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                    .build()
            )
        }

        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                    }
                    val imageCaptureInstance = ImageCapture.Builder().build()
                    imageCapture = imageCaptureInstance

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
                                            if (faces.isNotEmpty()) {
                                                val width = 500f
                                                val height = 720f
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
                                                            imagePaths = imagePaths,
                                                            imageCapture = imageCapture!!,
                                                            setProcessing = { isProcessing = it },
                                                            coroutineScope = coroutineScope,
                                                            imageIndex = selectedImageIndex!!,
                                                            onSuccess = {
                                                                showCamera = false
                                                                selectedImageIndex = null
                                                            })
                                                    }
                                                    takeImage = false
                                                } else {
                                                    faceFound = false
                                                    takeImage = false
                                                }
                                            } else {
                                                faceFound = false
                                            }
                                            imageProxy.close()
                                        }
                                        .addOnFailureListener { e ->
                                            errorMessage = "Face detection failed: ${e.message}"
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
                            imageCaptureInstance,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        errorMessage = "Camera binding failed: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        FaceDetectionOverlay()
    }

    Scaffold(
        containerColor = appBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Register User",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (showCamera && hasPermission) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    CameraPreview()
                }
            } else {
//                if (isProcessing) {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                } else if (!isAuthenticated) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(padding)
//                            .padding(horizontal = 20.dp),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.Start
//                    ) {
//                        Text(
//                            "Enter Password",
//                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
//                        )
//                        Spacer(modifier = Modifier.height(10.dp))
//                        OutlinedTextField(
//                            value = password,
//                            onValueChange = { password = it },
//                            placeholder = { Text("Enter password") },
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                            singleLine = true,
//                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//
//                            modifier = Modifier.fillMaxWidth(),
//
//                            trailingIcon = {
//                                IconButton(onClick = { showPassword = !showPassword }) {
//                                    Icon(
//                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                                        contentDescription = if (showPassword) "Hide password" else "Show password"
//                                    )
//                                }
//                            }
//                        )
//                        Spacer(modifier = Modifier.height(20.dp))
//                        Button(
//                            onClick = {
//                                // Verify password (replace "admin123" with your actual password check)
//                                val storedPassword =
//                                    sharedPreferences.getString("company_password", null)
//                                if (password == storedPassword) { // Fallback default password
//                                    isAuthenticated = true
//                                    Toast.makeText(
//                                        context,
//                                        "Authentication successful",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT)
//                                        .show()
//                                }
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(50.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(
//                                    ContextCompat.getColor(
//                                        context,
//                                        R.color.primary
//                                    )
//                                )
//                            )
//                        ) {
//                            Text("VERIFY")
//                        }
//                    }
//                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(15.dp)
                        ) {
                            item {
                                Text(
                                    "First Name",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    placeholder = { Text("Enter your first name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Last Name",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    placeholder = { Text("Enter your last name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Bio Id",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = bioId,
                                    onValueChange = { bioId = it },
                                    placeholder = { Text("Enter Bio Id") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Department",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = department,
                                    onValueChange = { department = it },
                                    placeholder = { Text("Enter Department") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Choose 4 Face Images",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(380.dp)
                                ) {
                                    items(4) { index ->
                                        ImageWidget(
                                            imagePath = imagePaths[index].orEmpty(),
                                            number = "${index + 1}",
                                            onClick = {
                                                selectedImageIndex = index
                                                if (hasPermission) {
                                                    showCamera = true
                                                } else if (!isPermissionRequested) {
                                                    isPermissionRequested = true
                                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                doRegister(
                                    context = context,
                                    firstName = firstName,
                                    lastName = lastName,
                                    bioId = bioId,
                                    department = department,
                                    imagePaths = imagePaths,
                                    onStart = { isProcessing = true },
                                    onEnd = { isProcessing = false },
                                    onSuccess = { navController.popBackStack() },
                                    authApi = authApi,
                                    coroutineScope = coroutineScope
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.primary
                                    )
                                )
                            ),
                            enabled = !isProcessing // Disable button during processing
                        ) {
                            Text("REGISTER")
                        }
                    }
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            text = "Registering User...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 80.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ImageWidget(imagePath: String, number: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(180.dp, 180.dp)
            .padding(top = 8.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    modifier = Modifier.size(35.dp)
                )
                Text(text = number, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            AsyncImage(
                model = imagePath,
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun captureAndCropImage(
    context: Context,
    imagePaths: MutableList<String?>,
    imageCapture: ImageCapture,
    setProcessing: (Boolean) -> Unit,
    coroutineScope: CoroutineScope,
    imageIndex: Int,
    onSuccess: () -> Unit
) {
    setProcessing(true)
    val tempDir = context.getExternalFilesDir(null) ?: run {
        coroutineScope.launch {
            Toast.makeText(context, "Storage unavailable", Toast.LENGTH_SHORT).show()
            setProcessing(false)
        }
        return
    }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(tempDir, "cropped_face_$timestamp.png")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
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
                        bitmap.recycle()
                        bitmap = rotatedBitmap
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

                    coroutineScope.launch {
                        imagePaths[imageIndex] = file.absolutePath
                        setProcessing(false)
                        onSuccess()
                        Log.d("FaceDetection", "Cropped image saved to: ${file.absolutePath}")
                    }
                } else {
                    coroutineScope.launch {
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT)
                            .show()
                        setProcessing(false)
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                coroutineScope.launch {
                    Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                    setProcessing(false)
                }
                Log.e("FaceDetection", "Image capture failed: $exception")
            }
        }
    )
}

private fun doRegister(
    context: Context,
    firstName: String,
    lastName: String,
    bioId: String,
    department: String,
    imagePaths: List<String?>,
    onStart: () -> Unit,
    onEnd: () -> Unit,
    onSuccess: () -> Unit,
    authApi: AuthApi,
    coroutineScope: CoroutineScope
) {
    if (firstName.isEmpty()) {
        Toast.makeText(context, "Please enter first name", Toast.LENGTH_SHORT).show()
        return
    }
    if (lastName.isEmpty()) {
        Toast.makeText(context, "Please enter last name", Toast.LENGTH_SHORT).show()
        return
    }
    if (department.isEmpty()) {
        Toast.makeText(context, "Please enter department", Toast.LENGTH_SHORT).show()
        return
    }
    val validPaths = imagePaths.filter { it?.isNotEmpty() == true }
    if (validPaths.size != 4) {
        Toast.makeText(context, "Please upload exactly 4 images", Toast.LENGTH_SHORT).show()
        return
    }
    onStart()
    coroutineScope.launch {
        val success = authApi.addUser(
            firstName = firstName,
            lastName = lastName,
            bioId = bioId,
            department = department,
            imagePaths = imagePaths
        )
        onEnd()
        if (success) {
            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
}