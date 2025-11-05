////package com.ananta.faceapp.presentation.components
////
////import android.annotation.SuppressLint
////import android.content.Context
////import android.graphics.Bitmap
////import android.graphics.Canvas
////import android.graphics.Color
////import android.graphics.Matrix
////import android.graphics.Paint
////import android.graphics.RectF
////import android.util.Log
////import android.util.Size
////import android.view.SurfaceHolder
////import android.view.SurfaceView
////import android.widget.FrameLayout
////import androidx.camera.core.AspectRatio
////import androidx.camera.core.CameraSelector
////import androidx.camera.core.ExperimentalGetImage
////import androidx.camera.core.ImageAnalysis
////import androidx.camera.core.Preview
////import androidx.camera.lifecycle.ProcessCameraProvider
////import androidx.camera.view.PreviewView
////import androidx.core.content.ContextCompat
////import androidx.core.graphics.toRectF
////import androidx.core.view.doOnLayout
////import androidx.lifecycle.LifecycleOwner
////import com.ananta.faceapp.domain.face_detection.MediapipeFaceDetector
////import com.ananta.faceapp.presentation.screens.detect_screen.DetectScreenViewModel
////import java.util.concurrent.Executors
////import kotlinx.coroutines.CoroutineScope
////import kotlinx.coroutines.Dispatchers
////import kotlinx.coroutines.launch
////import kotlinx.coroutines.withContext
////import androidx.core.graphics.createBitmap
////
////@SuppressLint("ViewConstructor")
////@ExperimentalGetImage
////class FaceDetectionOverlay(
////    private val lifecycleOwner: LifecycleOwner,
////    private val context: Context,
////    private val viewModel: DetectScreenViewModel,
////) : FrameLayout(context) {
////
////    private var overlayWidth: Int = 0
////    private var overlayHeight: Int = 0
////
////    private var imageTransform: Matrix = Matrix()
////    private var boundingBoxTransform: Matrix = Matrix()
////    private var isImageTransformedInitialized = false
////    private var isBoundingBoxTransformedInitialized = false
////
////    private lateinit var frameBitmap: Bitmap
////    private var isProcessing = false
////    private var cameraFacing: Int = CameraSelector.LENS_FACING_BACK
////    private lateinit var boundingBoxOverlay: BoundingBoxOverlay
////    private lateinit var previewView: PreviewView
////
////    var predictions: Array<Prediction> = arrayOf()
////
////    init {
////        initializeCamera(cameraFacing)
////        doOnLayout {
////            overlayHeight = it.measuredHeight
////            overlayWidth = it.measuredWidth
////        }
////    }
////
////
////    fun initializeCamera(cameraFacing: Int) {
////        this.cameraFacing = cameraFacing
////        this.isImageTransformedInitialized = false
////        this.isBoundingBoxTransformedInitialized = false
////        this.predictions = arrayOf() // Clear previous predictions
////        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
////        val previewView = PreviewView(context)
////        val executor = ContextCompat.getMainExecutor(context)
////        cameraProviderFuture.addListener(
////            {
////                val cameraProvider = cameraProviderFuture.get()
////                val preview = Preview.Builder()
////                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//////                    .setTargetResolution(Size(1280, 720)) // Reduced from 1920x1080
//////                    .setTargetResolution(Size( /* full resolution */ 1920, 1080))
////
////                    .build().also {
////                    it.setSurfaceProvider(previewView.surfaceProvider)
////                }
////                val cameraSelector =
////                    CameraSelector.Builder().requireLensFacing(cameraFacing).build()
////                val frameAnalyzer =
////                    ImageAnalysis.Builder()
////                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
////
//////                        .setTargetResolution(Size( /* full resolution */ 1920, 1080))
////                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
////                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
////                frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
////                cameraProvider.unbindAll()
////                cameraProvider.bindToLifecycle(
////                    lifecycleOwner, cameraSelector, preview, frameAnalyzer
////                )
////            }, executor
////        )
////        if (childCount >= 2) {
////            removeView(this.previewView)
////            removeView(this.boundingBoxOverlay)
////        }
////        this.previewView = previewView
////        addView(this.previewView)
////
////        val boundingBoxOverlayParams =
////            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
////        this.boundingBoxOverlay = BoundingBoxOverlay(context)
////        this.boundingBoxOverlay.setWillNotDraw(false)
////        this.boundingBoxOverlay.setZOrderOnTop(true)
////        addView(this.boundingBoxOverlay, boundingBoxOverlayParams)
////    }
////
////
////    private val analyzer = ImageAnalysis.Analyzer { image ->
////        if (isProcessing) {
////            image.close()
////            return@Analyzer
////        }
////        isProcessing = true
////
////
////        Log.d(
////            "FaceDetectionOverlay",
////            "Image analysis started  width :${image.image!!.width} x height : ${image.image!!.height}"
////        )
////        // Transform android.net.Image to Bitmap
////        frameBitmap = createBitmap(image.image!!.width, image.image!!.height,
////                Bitmap.Config.ARGB_8888 // Explicitly set high-quality config
////        )
////        frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)
////
////        // Configure frameHeight and frameWidth for output2overlay transformation matrix
////        // and apply it to `frameBitmap`
////        if (!isImageTransformedInitialized) {
////            imageTransform = Matrix()
////            imageTransform.apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
////            isImageTransformedInitialized = true
////        }
////
////
////
////        frameBitmap = Bitmap.createBitmap(
////            frameBitmap, 0, 0, frameBitmap.width, frameBitmap.height, imageTransform, false
////        )
////
////        if (!isBoundingBoxTransformedInitialized) {
////            boundingBoxTransform = Matrix()
////            boundingBoxTransform.apply {
////                setScale(
////                    overlayWidth / frameBitmap.width.toFloat(),
////                    overlayHeight / frameBitmap.height.toFloat()
////                )
////                if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
////                    postScale(
////                        -1f, 1f, overlayWidth.toFloat() / 2.0f, overlayHeight.toFloat() / 2.0f
////                    )
////                }
////            }
////            isBoundingBoxTransformedInitialized = true
////        }
////
////        CoroutineScope(Dispatchers.Default).launch {
////            val predictions = ArrayList<Prediction>()
////            val (metrics, results) = viewModel.imageVectorUseCase.detectFaces(frameBitmap)
////
////            // Assume metrics is of type FaceDetectionMetrics
////            var hasRealFace = false
////            var hasSpoofFace = false
////            var capturedFaceBitmap: Bitmap? = null
////
////            // Check if any faces were detected
////            if (results.isEmpty()) {
////                // No faces detected
////                withContext(Dispatchers.Main) {
////                    viewModel.onNoFaceDetected()
////                    this@FaceDetectionOverlay.predictions = arrayOf()
////                    boundingBoxOverlay.invalidate()
////                    isProcessing = false
////                }
////            } else {
////                // Process detection results
////                for (result in results) {
////                    val box = result.boundingBox.toRectF()
////                    var label = "Face"
////                    val isSpoof = result.spoofResult?.isSpoof ?: false
////
////                    // Apply transformation to the bounding box
////                    boundingBoxTransform.mapRect(box)
////
////
////                    val scaleFactor = 1.2f // Overall width/height scale
////                    val widthIncrease = (box.width() * (scaleFactor - 1f)) / 1.2f
////                    val heightIncrease = (box.height() * (scaleFactor - 1f)) / 2
////                    box.left -= widthIncrease
////                    box.right += widthIncrease
////
////// Increase top and bottom more aggressively
////                    val topExtra = heightIncrease * 4.5f   // Top extension
////                    val bottomExtra = heightIncrease * 1.5f // Bottom extension
////                    box.top -= topExtra
////                    box.bottom += bottomExtra
////
////// Optionally, clamp to overlay bounds
////                    box.left = maxOf(0f, box.left)
////                    box.right = minOf(overlayWidth.toFloat(), box.right)
////                    box.top = maxOf(0f, box.top)
////                    box.bottom = minOf(overlayHeight.toFloat(), box.bottom)
////
////                    if (result.spoofResult != null && isSpoof) {
////                        label = "Spoof: ${result.spoofResult.score}"
////                        hasSpoofFace = true
////                    } else {
////                        // Real face detected
////                        hasRealFace = true
////                        label = "Real"
////                        if (capturedFaceBitmap == null) {
////                            // Crop the **first real face only** (or you can store multiple)
//////                            capturedFaceBitmap = cropFaceFromOverlay(frameBitmap, box)
//////                            capturedFaceBitmap = cropFaceFromBitmap(frameBitmap, box, boundingBoxTransform)
////                            capturedFaceBitmap = frameBitmap
////                        }
////                    }
////                    predictions.add(Prediction(box, label, isSpoof))
////                }
////
////                withContext(Dispatchers.Main) {
////                    if (hasRealFace) {
////                        // Update ViewModel with FaceDetectionMetrics for real face
////                        viewModel.onFaceDetected(
////                            isReal = true,
////                            bitmap = capturedFaceBitmap,
////                            faceDetectionMetrics = metrics
////                        )
////                    } else if (hasSpoofFace) {
////                        // For spoofed faces only
////                        viewModel.onFaceDetected(
////                            isReal = false, bitmap = null, faceDetectionMetrics = null
////                        )
////                    }
////
////                    // Update UI elements
////                    this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
////                    boundingBoxOverlay.invalidate()
////                    isProcessing = false
////                }
////            }
////            image.close()
////        }
////    }
////
////private fun cropFaceFromOverlay(frameBitmap: Bitmap, overlayBox: RectF): Bitmap? {
////    return try {
////        val inverse = Matrix()
////        if (!boundingBoxTransform.invert(inverse)) {
////            Log.e("FaceDetectionOverlay", "Failed to invert boundingBoxTransform")
////            return null
////        }
////
////        val originalBox = RectF()
////        inverse.mapRect(originalBox, overlayBox)
////
////        // Clamp coordinates to bitmap bounds
////        val left = maxOf(0, originalBox.left.toInt())
////        val top = maxOf(0, originalBox.top.toInt())
////        val right = minOf(frameBitmap.width, originalBox.right.toInt())
////        val bottom = minOf(frameBitmap.height, originalBox.bottom.toInt())
////
////        val width = right - left
////        val height = bottom - top
////        if (width <= 0 || height <= 0) {
////            Log.e("FaceDetectionOverlay", "Invalid crop dimensions: width=$width, height=$height")
////            return null
////        }
////
////        // Crop without scaling or compression
////        Bitmap.createBitmap(frameBitmap, left, top, width, height, null, false)
//////        Bitmap.createBitmap(frameBitmap, left, top, width, height)
////
////    } catch (e: Exception) {
////        Log.e("FaceDetectionOverlay", "Error cropping face from overlay", e)
////        null
////    }
////}
//////    private fun cropFaceFromBitmap(
//////        frameBitmap: Bitmap,
//////        overlayBox: RectF,
//////        boundingBoxTransform: Matrix
//////    ): Bitmap? {
//////        return try {
//////            // Create inverse matrix to map overlay coords -> bitmap coords
//////            val inverse = Matrix()
//////            if (!boundingBoxTransform.invert(inverse)) return null
//////
//////            val originalBox = RectF()
//////            inverse.mapRect(originalBox, overlayBox)
//////
//////            // Clamp the coordinates inside bitmap bounds
//////            val left = maxOf(0, originalBox.left.toInt())
//////            val top = maxOf(0, originalBox.top.toInt())
//////            val right = minOf(frameBitmap.width, originalBox.right.toInt())
//////            val bottom = minOf(frameBitmap.height, originalBox.bottom.toInt())
//////
//////            val width = right - left
//////            val height = bottom - top
//////
//////            if (width > 0 && height > 0) {
//////                Bitmap.createBitmap(frameBitmap, left, top, width, height)
//////            } else null
//////        } catch (e: Exception) {
//////            Log.e("FaceDetectionOverlay", "Error cropping face bitmap", e)
//////            null
//////        }
//////    }
////
////    // Helper function to extract face bitmap from the frame
////    private fun extractFaceBitmap(
////        frameBitmap: Bitmap, boundingBox: android.graphics.Rect
////    ): Bitmap? {
////        return try {
////            // Ensure the bounding box is within the bitmap bounds
////            val left = maxOf(0, boundingBox.left)
////            val top = maxOf(0, boundingBox.top)
////            val right = minOf(frameBitmap.width, boundingBox.right)
////            val bottom = minOf(frameBitmap.height, boundingBox.bottom)
////
////            val width = right - left
////            val height = bottom - top
////
////            if (width > 0 && height > 0) {
////                Bitmap.createBitmap(frameBitmap, left, top, width, height)
////            } else {
////                null
////            }
////        } catch (e: Exception) {
////            Log.e("FaceDetectionOverlay", "Error extracting face bitmap", e)
////            null
////        }
////    }
////
////    data class Prediction(
////        var bbox: RectF, var label: String = "", var isSpoof: Boolean = false
////    )
////
////    inner class BoundingBoxOverlay(context: Context) : SurfaceView(context),
////        SurfaceHolder.Callback {
////
////        private val boxPaintFill = Paint().apply {
////            color = Color.TRANSPARENT // Transparent background
////            style = Paint.Style.FILL
////        }
////        private val boxPaintStroke = Paint().apply {
////            style = Paint.Style.STROKE
////            strokeWidth = 4.0f // Border thickness
////        }
////        private val textPaint = Paint().apply {
////            strokeWidth = 2.0f
////            textSize = 48f // Increased text size
////            textAlign = Paint.Align.RIGHT // Align text to the right
////        }
////
////        override fun surfaceCreated(holder: SurfaceHolder) {}
////
////        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
////
////        override fun surfaceDestroyed(holder: SurfaceHolder) {}
////
////        override fun onDraw(canvas: Canvas) {
////            predictions.forEach {
////                // Set border color based on spoof status
////                boxPaintStroke.color = if (it.isSpoof) Color.RED else Color.GREEN
////                // Draw transparent fill
////                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintFill)
////                // Draw border
////                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintStroke)
////                // Set text color based on spoof status
////                textPaint.color = if (it.isSpoof) Color.RED else Color.GREEN
////                // Draw label at top-right corner of the bounding box
////                canvas.drawText(
////                    it.label, it.bbox.right - 8f, // Small offset from right edge
////                    it.bbox.top + textPaint.textSize, // Align with top, account for text size
////                    textPaint
////                )
////            }
////        }
////    }
////}
//////
//////package com.ml.shubham0204.facenet_android.presentation.components
//////
//////import android.annotation.SuppressLint
//////import android.content.Context
//////import android.graphics.Bitmap
//////import android.graphics.Canvas
//////import android.graphics.Color
//////import android.graphics.Matrix
//////import android.graphics.Paint
//////import android.graphics.RectF
//////import android.util.Log
//////import android.util.Size
//////import android.view.SurfaceHolder
//////import android.view.SurfaceView
//////import android.widget.FrameLayout
//////import androidx.camera.core.AspectRatio
//////import androidx.camera.core.CameraSelector
//////import androidx.camera.core.ExperimentalGetImage
//////import androidx.camera.core.ImageAnalysis
//////import androidx.camera.core.ImageProxy
//////import androidx.camera.core.Preview
//////import androidx.camera.lifecycle.ProcessCameraProvider
//////import androidx.camera.view.PreviewView
//////import androidx.core.content.ContextCompat
//////import androidx.core.graphics.toRectF
//////import androidx.core.view.doOnLayout
//////import androidx.lifecycle.LifecycleOwner
//////import com.ananta.faceapp.presentation.screens.detect_screen.DetectScreenViewModel
//////import kotlinx.coroutines.CoroutineScope
//////import kotlinx.coroutines.Dispatchers
//////import kotlinx.coroutines.launch
//////import kotlinx.coroutines.withContext
//////import java.nio.ByteBuffer
//////import java.util.concurrent.Executors
//////
//////@SuppressLint("ViewConstructor")
//////@ExperimentalGetImage
//////class FaceDetectionOverlay(
//////    private val lifecycleOwner: LifecycleOwner,
//////    private val context: Context,
//////    private val viewModel: DetectScreenViewModel,
//////) : FrameLayout(context) {
//////
//////    private var overlayWidth: Int = 0
//////    private var overlayHeight: Int = 0
//////
//////    private var imageTransform: Matrix = Matrix()
//////    private var boundingBoxTransform: Matrix = Matrix()
//////    private var isImageTransformedInitialized = false
//////    private var isBoundingBoxTransformedInitialized = false
//////
//////    private lateinit var frameBitmap: Bitmap
//////    private var isProcessing = false
//////    private var cameraFacing: Int = CameraSelector.LENS_FACING_BACK
//////    private lateinit var boundingBoxOverlay: BoundingBoxOverlay
//////    private lateinit var previewView: PreviewView
//////
//////    var predictions: Array<Prediction> = arrayOf()
//////
//////    init {
//////        initializeCamera(cameraFacing)
//////        doOnLayout {
//////            overlayHeight = it.measuredHeight
//////            overlayWidth = it.measuredWidth
//////        }
//////    }
//////
//////    fun initializeCamera(cameraFacing: Int) {
//////        this.cameraFacing = cameraFacing
//////        this.isImageTransformedInitialized = false
//////        this.isBoundingBoxTransformedInitialized = false
//////        this.predictions = arrayOf() // Clear previous predictions
//////        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//////        val previewView = PreviewView(context)
//////        val executor = ContextCompat.getMainExecutor(context)
//////        cameraProviderFuture.addListener(
//////            {
//////                val cameraProvider = cameraProviderFuture.get()
//////                val preview = Preview.Builder().build().also {
//////                    it.setSurfaceProvider(previewView.surfaceProvider)
//////                }
//////                val cameraSelector =
//////                    CameraSelector.Builder().requireLensFacing(cameraFacing).build()
//////                val frameAnalyzer =
//////                    ImageAnalysis.Builder()
//////                        .setTargetResolution(Size( /* full resolution */ 1920, 1080))
//////                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//////                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
//////                frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
//////                cameraProvider.unbindAll()
//////                cameraProvider.bindToLifecycle(
//////                    lifecycleOwner, cameraSelector, preview, frameAnalyzer
//////                )
//////            }, executor
//////        )
//////        if (childCount >= 2) {
//////            removeView(this.previewView)
//////            removeView(this.boundingBoxOverlay)
//////        }
//////        this.previewView = previewView
//////        addView(this.previewView)
//////
//////        val boundingBoxOverlayParams =
//////            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//////        this.boundingBoxOverlay = BoundingBoxOverlay(context)
//////        this.boundingBoxOverlay.setWillNotDraw(false)
//////        this.boundingBoxOverlay.setZOrderOnTop(true)
//////        addView(this.boundingBoxOverlay, boundingBoxOverlayParams)
//////    }
//////
//////    private val analyzer = ImageAnalysis.Analyzer { image ->
//////        if (isProcessing) {
//////            image.close()
//////            return@Analyzer
//////        }
//////        isProcessing = true
//////
//////        Log.d(
//////            "FaceDetectionOverlay",
//////            "Image analysis started  width: ${image.width} x height: ${image.height}"
//////        )
//////
//////        // Convert ImageProxy to Bitmap directly
//////        frameBitmap = image.toBitmap()
//////
//////        // Apply rotation to match display orientation
//////        if (!isImageTransformedInitialized) {
//////            imageTransform = Matrix()
//////            imageTransform.postRotate(image.imageInfo.rotationDegrees.toFloat())
//////            isImageTransformedInitialized = true
//////        }
//////
//////        frameBitmap = Bitmap.createBitmap(
//////            frameBitmap, 0, 0, frameBitmap.width, frameBitmap.height, imageTransform, false
//////        )
//////
//////        if (!isBoundingBoxTransformedInitialized) {
//////            boundingBoxTransform = Matrix()
//////            boundingBoxTransform.apply {
//////                setScale(
//////                    overlayWidth / frameBitmap.width.toFloat(),
//////                    overlayHeight / frameBitmap.height.toFloat()
//////                )
//////                if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
//////                    postScale(
//////                        -1f, 1f, overlayWidth.toFloat() / 2.0f, overlayHeight.toFloat() / 2.0f
//////                    )
//////                }
//////            }
//////            isBoundingBoxTransformedInitialized = true
//////        }
//////
//////        CoroutineScope(Dispatchers.Default).launch {
//////            val predictions = ArrayList<Prediction>()
//////            val (metrics, results) = viewModel.imageVectorUseCase.detectFaces(frameBitmap)
//////
//////            var hasRealFace = false
//////            var hasSpoofFace = false
//////            var capturedFaceBitmap: Bitmap? = null
//////
//////            if (results.isEmpty()) {
//////                withContext(Dispatchers.Main) {
//////                    viewModel.onNoFaceDetected()
//////                    this@FaceDetectionOverlay.predictions = arrayOf()
//////                    boundingBoxOverlay.invalidate()
//////                    isProcessing = false
//////                }
//////            } else {
//////                for (result in results) {
//////                    val box = result.boundingBox.toRectF()
//////                    var label = "Face"
//////                    val isSpoof = result.spoofResult?.isSpoof ?: false
//////
//////                    // Apply transformation to the bounding box
//////                    boundingBoxTransform.mapRect(box)
//////
//////                    val scaleFactor = 1.2f // Overall width/height scale
//////                    val widthIncrease = (box.width() * (scaleFactor - 1f)) / 1.2f
//////                    val heightIncrease = (box.height() * (scaleFactor - 1f)) / 2
//////                    box.left -= widthIncrease
//////                    box.right += widthIncrease
//////
//////                    val topExtra = heightIncrease * 4.5f   // Top extension
//////                    val bottomExtra = heightIncrease * 1.5f // Bottom extension
//////                    box.top -= topExtra
//////                    box.bottom += bottomExtra
//////
//////                    // Clamp to overlay bounds
//////                    box.left = maxOf(0f, box.left)
//////                    box.right = minOf(overlayWidth.toFloat(), box.right)
//////                    box.top = maxOf(0f, box.top)
//////                    box.bottom = minOf(overlayHeight.toFloat(), box.bottom)
//////
//////                    if (result.spoofResult != null && isSpoof) {
//////                        label = "Spoof: ${result.spoofResult.score}"
//////                        hasSpoofFace = true
//////                    } else {
//////                        hasRealFace = true
//////                        label = "Real"
//////                        if (capturedFaceBitmap == null) {
////////                            capturedFaceBitmap = cropFaceFromOverlay(frameBitmap, box)
//////                            capturedFaceBitmap = frameBitmap
//////                        }
//////                    }
//////                    predictions.add(Prediction(box, label, isSpoof))
//////                }
//////
//////                withContext(Dispatchers.Main) {
//////                    if (hasRealFace) {
//////                        viewModel.onFaceDetected(
//////                            isReal = true,
//////                            bitmap = capturedFaceBitmap,
//////                            faceDetectionMetrics = metrics
//////                        )
//////                    } else if (hasSpoofFace) {
//////                        viewModel.onFaceDetected(
//////                            isReal = false, bitmap = null, faceDetectionMetrics = null
//////                        )
//////                    }
//////
//////                    this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
//////                    boundingBoxOverlay.invalidate()
//////                    isProcessing = false
//////                }
//////            }
//////            image.close()
//////        }
//////    }
//////
//////    // Helper function to convert ImageProxy to Bitmap for RGBA_8888 format
//////    private fun ImageProxy.toBitmap(): Bitmap {
//////        val image = this.image ?: throw IllegalStateException("ImageProxy image is null")
//////        val buffer: ByteBuffer = this.planes[0].buffer
//////        val bytes = ByteArray(buffer.remaining())
//////        buffer.get(bytes)
//////
//////        // Create Bitmap from RGBA_8888 data
//////        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
//////        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytes))
//////        return bitmap
//////    }
//////
//////    private fun cropFaceFromOverlay(frameBitmap: Bitmap, overlayBox: RectF): Bitmap? {
//////        return try {
//////            val inverse = Matrix()
//////            if (!boundingBoxTransform.invert(inverse)) {
//////                Log.e("FaceDetectionOverlay", "Failed to invert boundingBoxTransform")
//////                return null
//////            }
//////
//////            val originalBox = RectF()
//////            inverse.mapRect(originalBox, overlayBox)
//////
//////            // Clamp coordinates to bitmap bounds
//////            val left = maxOf(0, originalBox.left.toInt())
//////            val top = maxOf(0, originalBox.top.toInt())
//////            val right = minOf(frameBitmap.width, originalBox.right.toInt())
//////            val bottom = minOf(frameBitmap.height, originalBox.bottom.toInt())
//////
//////            val width = right - left
//////            val height = bottom - top
//////            if (width <= 0 || height <= 0) {
//////                Log.e("FaceDetectionOverlay", "Invalid crop dimensions: width=$width, height=$height")
//////                return null
//////            }
//////
//////            Bitmap.createBitmap(frameBitmap, left, top, width, height, null, false)
//////        } catch (e: Exception) {
//////            Log.e("FaceDetectionOverlay", "Error cropping face from overlay", e)
//////            null
//////        }
//////    }
//////
//////    data class Prediction(
//////        var bbox: RectF, var label: String = "", var isSpoof: Boolean = false
//////    )
//////
//////    inner class BoundingBoxOverlay(context: Context) : SurfaceView(context),
//////        SurfaceHolder.Callback {
//////
//////        private val boxPaintFill = Paint().apply {
//////            color = Color.TRANSPARENT // Transparent background
//////            style = Paint.Style.FILL
//////        }
//////        private val boxPaintStroke = Paint().apply {
//////            style = Paint.Style.STROKE
//////            strokeWidth = 4.0f // Border thickness
//////        }
//////        private val textPaint = Paint().apply {
//////            strokeWidth = 2.0f
//////            textSize = 48f // Increased text size
//////            textAlign = Paint.Align.RIGHT // Align text to the right
//////        }
//////
//////        override fun surfaceCreated(holder: SurfaceHolder) {}
//////
//////        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//////
//////        override fun surfaceDestroyed(holder: SurfaceHolder) {}
//////
//////        override fun onDraw(canvas: Canvas) {
//////            predictions.forEach {
//////                // Set border color based on spoof status
//////                boxPaintStroke.color = if (it.isSpoof) Color.RED else Color.GREEN
//////                // Draw transparent fill
//////                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintFill)
//////                // Draw border
//////                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintStroke)
//////                // Set text color based on spoof status
//////                textPaint.color = if (it.isSpoof) Color.RED else Color.GREEN
//////                // Draw label at top-right corner of the bounding box
//////                canvas.drawText(
//////                    it.label, it.bbox.right - 8f, // Small offset from right edge
//////                    it.bbox.top + textPaint.textSize, // Align with top, account for text size
//////                    textPaint
//////                )
//////            }
//////        }
//////    }
//////}
//
//package com.ananta.faceapp.presentation.components
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Matrix
//import android.graphics.Paint
//import android.graphics.RectF
//import android.util.Log
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.widget.FrameLayout
//import androidx.camera.core.AspectRatio
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ExperimentalGetImage
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.content.ContextCompat
//import androidx.core.graphics.toRectF
//import androidx.core.view.doOnLayout
//import androidx.lifecycle.LifecycleOwner
//import com.ananta.faceapp.domain.face_detection.MediapipeFaceDetector
//import com.ananta.faceapp.presentation.screens.detect_screen.DetectScreenViewModel
//import java.util.concurrent.Executors
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import androidx.core.graphics.createBitmap
//import kotlin.math.abs
//
//@SuppressLint("ViewConstructor")
//@ExperimentalGetImage
//class FaceDetectionOverlay(
//    private val lifecycleOwner: LifecycleOwner,
//    private val context: Context,
//    private val viewModel: DetectScreenViewModel,
//) : FrameLayout(context) {
//
//    private var overlayWidth: Int = 0
//    private var overlayHeight: Int = 0
//
//    private var imageTransform: Matrix = Matrix()
//    private var boundingBoxTransform: Matrix = Matrix()
//    private var isImageTransformedInitialized = false
//    private var isBoundingBoxTransformedInitialized = false
//
//    private lateinit var frameBitmap: Bitmap
//    private var isProcessing = false
//    private var cameraFacing: Int = CameraSelector.LENS_FACING_BACK
//    private lateinit var boundingBoxOverlay: BoundingBoxOverlay
//    private lateinit var previewView: PreviewView
//
//    var predictions: Array<Prediction> = arrayOf()
//
//    // Center zone configuration (percentage of screen)
//    private val CENTER_ZONE_WIDTH_PERCENT = 0.5f  // 50% of screen width
//    private val CENTER_ZONE_HEIGHT_PERCENT = 0.6f // 60% of screen height
//    private val FACE_SIZE_MIN_PERCENT = 0.25f     // Face should be at least 25% of center zone
//    private val FACE_SIZE_MAX_PERCENT = 0.85f     // Face should be at most 85% of center zone
//
//    init {
//        initializeCamera(cameraFacing)
//        doOnLayout {
//            overlayHeight = it.measuredHeight
//            overlayWidth = it.measuredWidth
//        }
//    }
//
//    fun initializeCamera(cameraFacing: Int) {
//        this.cameraFacing = cameraFacing
//        this.isImageTransformedInitialized = false
//        this.isBoundingBoxTransformedInitialized = false
//        this.predictions = arrayOf()
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        val previewView = PreviewView(context)
//        val executor = ContextCompat.getMainExecutor(context)
//        cameraProviderFuture.addListener(
//            {
//                val cameraProvider = cameraProviderFuture.get()
//                val preview = Preview.Builder()
//                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//                    .build().also {
//                        it.setSurfaceProvider(previewView.surfaceProvider)
//                    }
//                val cameraSelector =
//                    CameraSelector.Builder().requireLensFacing(cameraFacing).build()
//                val frameAnalyzer =
//                    ImageAnalysis.Builder()
//                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
//                frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner, cameraSelector, preview, frameAnalyzer
//                )
//            }, executor
//        )
//        if (childCount >= 2) {
//            removeView(this.previewView)
//            removeView(this.boundingBoxOverlay)
//        }
//        this.previewView = previewView
//        addView(this.previewView)
//
//        val boundingBoxOverlayParams =
//            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//        this.boundingBoxOverlay = BoundingBoxOverlay(context)
//        this.boundingBoxOverlay.setWillNotDraw(false)
//        this.boundingBoxOverlay.setZOrderOnTop(true)
//        addView(this.boundingBoxOverlay, boundingBoxOverlayParams)
//    }
//
//    private fun isFaceCentered(faceBox: RectF): Boolean {
//        // Calculate center zone bounds
//        val centerZoneLeft = overlayWidth * (1 - CENTER_ZONE_WIDTH_PERCENT) / 2
//        val centerZoneRight = overlayWidth * (1 + CENTER_ZONE_WIDTH_PERCENT) / 2
//        val centerZoneTop = overlayHeight * (1 - CENTER_ZONE_HEIGHT_PERCENT) / 2
//        val centerZoneBottom = overlayHeight * (1 + CENTER_ZONE_HEIGHT_PERCENT) / 2
//
//        val centerZoneWidth = centerZoneRight - centerZoneLeft
//        val centerZoneHeight = centerZoneBottom - centerZoneTop
//
//        // Calculate face center
//        val faceCenterX = (faceBox.left + faceBox.right) / 2
//        val faceCenterY = (faceBox.top + faceBox.bottom) / 2
//
//        // Calculate screen center
//        val screenCenterX = overlayWidth / 2f
//        val screenCenterY = overlayHeight / 2f
//
//        // Check if face center is within center zone
//        val isInCenterZone = faceCenterX >= centerZoneLeft &&
//                faceCenterX <= centerZoneRight &&
//                faceCenterY >= centerZoneTop &&
//                faceCenterY <= centerZoneBottom
//
//        // Check face size relative to center zone
//        val faceWidth = faceBox.width()
//        val faceHeight = faceBox.height()
//        val minWidth = centerZoneWidth * FACE_SIZE_MIN_PERCENT
//        val maxWidth = centerZoneWidth * FACE_SIZE_MAX_PERCENT
//        val minHeight = centerZoneHeight * FACE_SIZE_MIN_PERCENT
//        val maxHeight = centerZoneHeight * FACE_SIZE_MAX_PERCENT
//
//        val isSizeAppropriate = faceWidth >= minWidth && faceWidth <= maxWidth &&
//                faceHeight >= minHeight && faceHeight <= maxHeight
//
//        // Check alignment (face shouldn't be too tilted)
//        val horizontalOffset = abs(faceCenterX - screenCenterX)
//        val verticalOffset = abs(faceCenterY - screenCenterY)
//        val maxHorizontalOffset = overlayWidth * 0.15f // 15% tolerance
//        val maxVerticalOffset = overlayHeight * 0.15f
//
//        val isWellAligned = horizontalOffset <= maxHorizontalOffset &&
//                verticalOffset <= maxVerticalOffset
//
//        return isInCenterZone && isSizeAppropriate && isWellAligned
//    }
//
//    private val analyzer = ImageAnalysis.Analyzer { image ->
//        if (isProcessing) {
//            image.close()
//            return@Analyzer
//        }
//        isProcessing = true
//
//        Log.d(
//            "FaceDetectionOverlay",
//            "Image analysis started width: ${image.image!!.width} x height: ${image.image!!.height}"
//        )
//
//        frameBitmap = createBitmap(
//            image.image!!.width,
//            image.image!!.height,
//            Bitmap.Config.ARGB_8888
//        )
//        frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)
//
//        if (!isImageTransformedInitialized) {
//            imageTransform = Matrix()
//            imageTransform.apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
//            isImageTransformedInitialized = true
//        }
//
//        frameBitmap = Bitmap.createBitmap(
//            frameBitmap, 0, 0, frameBitmap.width, frameBitmap.height, imageTransform, false
//        )
//
//        if (!isBoundingBoxTransformedInitialized) {
//            boundingBoxTransform = Matrix()
//            boundingBoxTransform.apply {
//                setScale(
//                    overlayWidth / frameBitmap.width.toFloat(),
//                    overlayHeight / frameBitmap.height.toFloat()
//                )
//                if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
//                    postScale(
//                        -1f, 1f, overlayWidth.toFloat() / 2.0f, overlayHeight.toFloat() / 2.0f
//                    )
//                }
//            }
//            isBoundingBoxTransformedInitialized = true
//        }
//
//        CoroutineScope(Dispatchers.Default).launch {
//            val predictions = ArrayList<Prediction>()
//            val (metrics, results) = viewModel.imageVectorUseCase.detectFaces(frameBitmap)
//
//            var hasRealFace = false
//            var hasSpoofFace = false
//            var capturedFaceBitmap: Bitmap? = null
//            var isFaceCenteredFlag = false
//
//            if (results.isEmpty()) {
//                withContext(Dispatchers.Main) {
//                    viewModel.onNoFaceDetected()
//                    this@FaceDetectionOverlay.predictions = arrayOf()
//                    boundingBoxOverlay.invalidate()
//                    isProcessing = false
//                }
//            } else {
//                for (result in results) {
//                    val box = result.boundingBox.toRectF()
//                    var label = "Face"
//                    val isSpoof = result.spoofResult?.isSpoof ?: false
//
//                    boundingBoxTransform.mapRect(box)
//
//                    val scaleFactor = 1.2f
//                    val widthIncrease = (box.width() * (scaleFactor - 1f)) / 1.2f
//                    val heightIncrease = (box.height() * (scaleFactor - 1f)) / 2
//                    box.left -= widthIncrease
//                    box.right += widthIncrease
//
//                    val topExtra = heightIncrease * 4.5f
//                    val bottomExtra = heightIncrease * 1.5f
//                    box.top -= topExtra
//                    box.bottom += bottomExtra
//
//                    box.left = maxOf(0f, box.left)
//                    box.right = minOf(overlayWidth.toFloat(), box.right)
//                    box.top = maxOf(0f, box.top)
//                    box.bottom = minOf(overlayHeight.toFloat(), box.bottom)
//
//                    // Check if face is centered
//                    val isCentered = isFaceCentered(box)
//
//                    if (result.spoofResult != null && isSpoof) {
//                        label = "Spoof: ${result.spoofResult.score}"
//                        hasSpoofFace = true
//                    } else {
//                        hasRealFace = true
//                        if (isCentered) {
//                            label = "Centered ✓"
//                            isFaceCenteredFlag = true
//                            if (capturedFaceBitmap == null) {
//                                capturedFaceBitmap = frameBitmap
//                            }
//                        } else {
//                            label = "Center face"
//                        }
//                    }
//                    predictions.add(Prediction(box, label, isSpoof, isCentered))
//                }
//
//                withContext(Dispatchers.Main) {
//                    if (hasRealFace) {
//                        viewModel.onFaceDetected(
//                            isReal = true,
//                            bitmap = capturedFaceBitmap,
//                            faceDetectionMetrics = metrics,
//                            isCentered = isFaceCenteredFlag
//                        )
//                    } else if (hasSpoofFace) {
//                        viewModel.onFaceDetected(
//                            isReal = false,
//                            bitmap = null,
//                            faceDetectionMetrics = null,
//                            isCentered = false
//                        )
//                    }
//
//                    this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
//                    boundingBoxOverlay.invalidate()
//                    isProcessing = false
//                }
//            }
//            image.close()
//        }
//    }
//
//    data class Prediction(
//        var bbox: RectF,
//        var label: String = "",
//        var isSpoof: Boolean = false,
//        var isCentered: Boolean = false
//    )
//
//    inner class BoundingBoxOverlay(context: Context) : SurfaceView(context),
//        SurfaceHolder.Callback {
//
//        private val boxPaintFill = Paint().apply {
//            color = Color.TRANSPARENT
//            style = Paint.Style.FILL
//        }
//        private val boxPaintStroke = Paint().apply {
//            style = Paint.Style.STROKE
//            strokeWidth = 4.0f
//        }
//        private val textPaint = Paint().apply {
//            strokeWidth = 2.0f
//            textSize = 48f
//            textAlign = Paint.Align.RIGHT
//        }
//        private val centerGuidePaint = Paint().apply {
//            color = Color.WHITE
//            style = Paint.Style.STROKE
//            strokeWidth = 2.0f
//            alpha = 100
//        }
//
//        override fun surfaceCreated(holder: SurfaceHolder) {}
//
//        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//
//        override fun surfaceDestroyed(holder: SurfaceHolder) {}
//
//        override fun onDraw(canvas: Canvas) {
//            // Draw center guide zone
//            val centerZoneLeft = width * (1 - CENTER_ZONE_WIDTH_PERCENT) / 2
//            val centerZoneRight = width * (1 + CENTER_ZONE_WIDTH_PERCENT) / 2
//            val centerZoneTop = height * (1 - CENTER_ZONE_HEIGHT_PERCENT) / 2
//            val centerZoneBottom = height * (1 + CENTER_ZONE_HEIGHT_PERCENT) / 2
//
//            canvas.drawRect(
//                centerZoneLeft,
//                centerZoneTop,
//                centerZoneRight,
//                centerZoneBottom,
//                centerGuidePaint
//            )
//
//            predictions.forEach {
//                // Set border color based on status
//                boxPaintStroke.color = when {
//                    it.isSpoof -> Color.RED
//                    it.isCentered -> Color.GREEN
//                    else -> Color.YELLOW
//                }
//
//                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintFill)
//                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintStroke)
//
//                textPaint.color = boxPaintStroke.color
//                canvas.drawText(
//                    it.label,
//                    it.bbox.right - 8f,
//                    it.bbox.top + textPaint.textSize,
//                    textPaint
//                )
//            }
//        }
//    }
//}
package com.ananta.faceapp.presentation.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import androidx.core.view.doOnLayout
import androidx.lifecycle.LifecycleOwner
import com.ananta.faceapp.domain.face_detection.MediapipeFaceDetector
import com.ananta.faceapp.presentation.screens.detect_screen.DetectScreenViewModel
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import kotlin.math.abs

@SuppressLint("ViewConstructor")
@ExperimentalGetImage
class FaceDetectionOverlay(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val viewModel: DetectScreenViewModel,
) : FrameLayout(context) {

    private var overlayWidth: Int = 0
    private var overlayHeight: Int = 0

    private var imageTransform: Matrix = Matrix()
    private var boundingBoxTransform: Matrix = Matrix()
    private var isImageTransformedInitialized = false
    private var isBoundingBoxTransformedInitialized = false

    private lateinit var frameBitmap: Bitmap
    private var isProcessing = false
    private var cameraFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var boundingBoxOverlay: BoundingBoxOverlay
    private lateinit var previewView: PreviewView

    var predictions: Array<Prediction> = arrayOf()

    // Full screen configuration - face can be anywhere on screen
    private val CENTER_ZONE_WIDTH_PERCENT = 1.0f  // 100% of screen width (full width)
    private val CENTER_ZONE_HEIGHT_PERCENT = 1.0f // 100% of screen height (full height)
    private val FACE_SIZE_MIN_PERCENT = 0.20f     // Face should be at least 20% of screen
    private val FACE_SIZE_MAX_PERCENT = 0.90f     // Face should be at most 90% of screen

    init {
        initializeCamera(cameraFacing)
        doOnLayout {
            overlayHeight = it.measuredHeight
            overlayWidth = it.measuredWidth
        }
    }

    fun initializeCamera(cameraFacing: Int) {
        this.cameraFacing = cameraFacing
        this.isImageTransformedInitialized = false
        this.isBoundingBoxTransformedInitialized = false
        this.predictions = arrayOf()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(cameraFacing).build()
                val frameAnalyzer =
                    ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
                frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, frameAnalyzer
                )
            }, executor
        )
        if (childCount >= 2) {
            removeView(this.previewView)
            removeView(this.boundingBoxOverlay)
        }
        this.previewView = previewView
        addView(this.previewView)

        val boundingBoxOverlayParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.boundingBoxOverlay = BoundingBoxOverlay(context)
        this.boundingBoxOverlay.setWillNotDraw(false)
        this.boundingBoxOverlay.setZOrderOnTop(true)
        addView(this.boundingBoxOverlay, boundingBoxOverlayParams)
    }

    private fun isFaceCentered(faceBox: RectF): Boolean {
        // For full screen: just check if face is reasonably sized and visible
        // No need for strict centering since zone is 100% of screen

        val faceWidth = faceBox.width()
        val faceHeight = faceBox.height()

        // Check face size relative to screen
        val minWidth = overlayWidth * FACE_SIZE_MIN_PERCENT
        val maxWidth = overlayWidth * FACE_SIZE_MAX_PERCENT
        val minHeight = overlayHeight * FACE_SIZE_MIN_PERCENT
        val maxHeight = overlayHeight * FACE_SIZE_MAX_PERCENT

        val isSizeAppropriate = faceWidth >= minWidth && faceWidth <= maxWidth &&
                faceHeight >= minHeight && faceHeight <= maxHeight

        // Check if face is fully visible (not cut off at edges)
        val isFullyVisible = faceBox.left >= 0 &&
                faceBox.right <= overlayWidth &&
                faceBox.top >= 0 &&
                faceBox.bottom <= overlayHeight

        // Optional: Check if face is relatively centered (loose tolerance for full screen)
        val faceCenterX = (faceBox.left + faceBox.right) / 2
        val faceCenterY = (faceBox.top + faceBox.bottom) / 2
        val screenCenterX = overlayWidth / 2f
        val screenCenterY = overlayHeight / 2f

        val horizontalOffset = abs(faceCenterX - screenCenterX)
        val verticalOffset = abs(faceCenterY - screenCenterY)
        val maxHorizontalOffset = overlayWidth * 0.40f // 40% tolerance (very loose)
        val maxVerticalOffset = overlayHeight * 0.40f

        val isReasonablyCentered = horizontalOffset <= maxHorizontalOffset &&
                verticalOffset <= maxVerticalOffset

        return isSizeAppropriate && isFullyVisible && isReasonablyCentered
    }

    private val analyzer = ImageAnalysis.Analyzer { image ->
        if (isProcessing) {
            image.close()
            return@Analyzer
        }
        isProcessing = true

        Log.d(
            "FaceDetectionOverlay",
            "Image analysis started width: ${image.image!!.width} x height: ${image.image!!.height}"
        )

        frameBitmap = createBitmap(
            image.image!!.width,
            image.image!!.height,
            Bitmap.Config.ARGB_8888
        )
        frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)

        if (!isImageTransformedInitialized) {
            imageTransform = Matrix()
            imageTransform.apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
            isImageTransformedInitialized = true
        }

        frameBitmap = Bitmap.createBitmap(
            frameBitmap, 0, 0, frameBitmap.width, frameBitmap.height, imageTransform, false
        )

        if (!isBoundingBoxTransformedInitialized) {
            boundingBoxTransform = Matrix()
            boundingBoxTransform.apply {
                setScale(
                    overlayWidth / frameBitmap.width.toFloat(),
                    overlayHeight / frameBitmap.height.toFloat()
                )
                if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
                    postScale(
                        -1f, 1f, overlayWidth.toFloat() / 2.0f, overlayHeight.toFloat() / 2.0f
                    )
                }
            }
            isBoundingBoxTransformedInitialized = true
        }

        CoroutineScope(Dispatchers.Default).launch {
            val predictions = ArrayList<Prediction>()
            val (metrics, results) = viewModel.imageVectorUseCase.detectFaces(frameBitmap)

            var hasRealFace = false
            var hasSpoofFace = false
            var capturedFaceBitmap: Bitmap? = null
            var isFaceCenteredFlag = false

            if (results.isEmpty()) {
                withContext(Dispatchers.Main) {
                    viewModel.onNoFaceDetected()
                    this@FaceDetectionOverlay.predictions = arrayOf()
                    boundingBoxOverlay.invalidate()
                    isProcessing = false
                }
            } else {
                for (result in results) {
                    val box = result.boundingBox.toRectF()
                    var label = "Face"
                    val isSpoof = result.spoofResult?.isSpoof ?: false

                    boundingBoxTransform.mapRect(box)

                    val (distanceLabel, distanceCm) = estimateFaceDistance(box)

                    val scaleFactor = 1.2f
                    val widthIncrease = (box.width() * (scaleFactor - 1f)) / 1.2f
                    val heightIncrease = (box.height() * (scaleFactor - 1f)) / 2
                    box.left -= widthIncrease
                    box.right += widthIncrease

                    val topExtra = heightIncrease * 4.5f
                    val bottomExtra = heightIncrease * 1.5f
                    box.top -= topExtra
                    box.bottom += bottomExtra

                    box.left = maxOf(0f, box.left)
                    box.right = minOf(overlayWidth.toFloat(), box.right)
                    box.top = maxOf(0f, box.top)
                    box.bottom = minOf(overlayHeight.toFloat(), box.bottom)

                    // Check if face is centered
                    val isCentered = isFaceCentered(box)

                    val isCaptureAllowed = isCentered && distanceCm in 25..40


                    Log.d("FaceDetectionOverlay", "isCentered: $isCentered, distanceCm: $distanceCm")
                    label = if (isSpoof) {
                        "Spoof: ${result.spoofResult.score} ($distanceLabel)"
                    } else if (isCaptureAllowed) {
                        distanceLabel
                    } else {
                        distanceLabel
                    }


                    if (hasRealFace == false) hasRealFace = !isSpoof
                    if (isCaptureAllowed && capturedFaceBitmap == null) {
                        capturedFaceBitmap = frameBitmap
                        isFaceCenteredFlag = true
                    }

//                    if (result.spoofResult != null && isSpoof) {
//                        label = "Spoof: ${result.spoofResult.score}"
//                        hasSpoofFace = true
//                    } else {
//                        hasRealFace = true
//                        if (isCentered) {
//                            label = "Centered ✓"
//                            isFaceCenteredFlag = true
//                            if (capturedFaceBitmap == null) {
//                                capturedFaceBitmap = frameBitmap
//                            }
//                        } else {
//                            label = "Center face"
//                        }
//                    }
                    predictions.add(Prediction(box, label, isSpoof, isCentered))

                    withContext(Dispatchers.Main) {
                        if (hasRealFace) {
                            viewModel.onFaceDetected(
                                isReal = true,
                                bitmap = capturedFaceBitmap,
                                faceDetectionMetrics = metrics,
                                isCentered = isFaceCenteredFlag,
                                distanceMessage = distanceLabel
                            )
                        } else if (hasSpoofFace) {
                            viewModel.onFaceDetected(
                                isReal = false,
                                bitmap = null,
                                faceDetectionMetrics = null,
                                isCentered = false,
                                        distanceMessage = distanceLabel

                            )
                        }

                        this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
                        boundingBoxOverlay.invalidate()
                        isProcessing = false
                    }

                }

            }
            image.close()
        }
    }

    data class Prediction(
        var bbox: RectF,
        var label: String = "",
        var isSpoof: Boolean = false,
        var isCentered: Boolean = false
    )

    inner class BoundingBoxOverlay(context: Context) : SurfaceView(context),
        SurfaceHolder.Callback {

        private val boxPaintFill = Paint().apply {
            color = Color.TRANSPARENT
            style = Paint.Style.FILL
        }
        private val boxPaintStroke = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
        }
        private val textPaint = Paint().apply {
            strokeWidth = 2.0f
            textSize = 48f
            textAlign = Paint.Align.RIGHT
        }
        private val centerGuidePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
            alpha = 100
        }

        override fun surfaceCreated(holder: SurfaceHolder) {}

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder) {}

        override fun onDraw(canvas: Canvas) {
            // No need to draw center guide zone for full screen mode
            // Optionally draw a subtle frame border to show active area
            canvas.drawRect(
                10f,
                10f,
                width - 10f,
                height - 10f,
                centerGuidePaint
            )

            predictions.forEach {
                // Set border color based on status
                boxPaintStroke.color = when {
                    it.isSpoof -> Color.RED
                    it.isCentered -> Color.GREEN
                    else -> Color.YELLOW
                }

                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintFill)
                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintStroke)

                textPaint.color = boxPaintStroke.color
                canvas.drawText(
                    it.label,
                    it.bbox.right - 8f,
                    it.bbox.top + textPaint.textSize,
                    textPaint
                )
            }
        }
    }
    private fun estimateFaceDistance(faceBox: RectF): Pair<String, Int> {
        val faceWidthRatio = faceBox.width() / overlayWidth.toFloat() // fraction of screen width

        // Estimate numeric distance in cm
        val distanceCm = when {
            faceWidthRatio > 0.70f -> 20
            faceWidthRatio > 0.55f -> 30
            faceWidthRatio > 0.45f -> 40
            faceWidthRatio > 0.35f -> 50
            else -> 60
        }

        // Return label text + distance in cm
        val distanceLabel = when {
            distanceCm < 25 -> "Very Close - Move Back"
            distanceCm in 25..35 -> "Perfect Distance"
            distanceCm in 36..45 -> "Perfect Distance"
            distanceCm in 46..55 -> "Far - Come Closer"
            else -> "Too Far - Come Closer"
        }

        return Pair(distanceLabel, distanceCm)
    }


}