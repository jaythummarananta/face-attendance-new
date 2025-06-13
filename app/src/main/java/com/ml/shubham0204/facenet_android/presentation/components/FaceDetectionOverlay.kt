//package com.ml.shubham0204.facenet_android.presentation.components
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Matrix
//import android.graphics.Paint
//import android.graphics.RectF
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
//import com.ml.shubham0204.facenet_android.presentation.screens.detect_screen.DetectScreenViewModel
//import java.util.concurrent.Executors
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//@SuppressLint("ViewConstructor")
//@ExperimentalGetImage
//class FaceDetectionOverlay(
//    private val lifecycleOwner: LifecycleOwner,
//    private val context: Context,
//    private val viewModel: DetectScreenViewModel
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
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        val previewView = PreviewView(context)
//        val executor = ContextCompat.getMainExecutor(context)
//        cameraProviderFuture.addListener(
//            {
//                val cameraProvider = cameraProviderFuture.get()
//                val preview =
//                    Preview.Builder().build().also {
//                        it.setSurfaceProvider(previewView.surfaceProvider)
//                    }
//                val cameraSelector =
//                    CameraSelector.Builder().requireLensFacing(cameraFacing).build()
//                val frameAnalyzer =
//                    ImageAnalysis.Builder()
//                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                        .build()
//                frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    frameAnalyzer
//                )
//            },
//            executor
//        )
//        if (childCount == 2) {
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
//    private val analyzer =
//        ImageAnalysis.Analyzer { image ->
//            if (isProcessing) {
//                image.close()
//                return@Analyzer
//            }
//            isProcessing = true
//
//            // Transform android.net.Image to Bitmap
//            frameBitmap =
//                Bitmap.createBitmap(
//                    image.image!!.width,
//                    image.image!!.height,
//                    Bitmap.Config.ARGB_8888
//                )
//            frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)
//
//            // Configure frameHeight and frameWidth for output2overlay transformation matrix
//            // and apply it to `frameBitmap`
//            if (!isImageTransformedInitialized) {
//                imageTransform = Matrix()
//                imageTransform.apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
//                isImageTransformedInitialized = true
//            }
//            frameBitmap =
//                Bitmap.createBitmap(
//                    frameBitmap,
//                    0,
//                    0,
//                    frameBitmap.width,
//                    frameBitmap.height,
//                    imageTransform,
//                    false
//                )
//
//            if (!isBoundingBoxTransformedInitialized) {
//                boundingBoxTransform = Matrix()
//                boundingBoxTransform.apply {
//                    setScale(
//                        overlayWidth / frameBitmap.width.toFloat(),
//                        overlayHeight / frameBitmap.height.toFloat()
//                    )
//                    if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
//                        // Mirror the bounding box coordinates
//                        // for front-facing camera
//                        postScale(
//                            -1f,
//                            1f,
//                            overlayWidth.toFloat() / 2.0f,
//                            overlayHeight.toFloat() / 2.0f
//                        )
//                    }
//                }
//                isBoundingBoxTransformedInitialized = true
//            }
//            CoroutineScope(Dispatchers.Default).launch {
//                val predictions = ArrayList<Prediction>()
//                val (metrics, results) = viewModel.imageVectorUseCase.getNearestPersonName(frameBitmap)
//                results.forEach {
//                    (name, boundingBox, spoofResult) ->
//                    val box = boundingBox.toRectF()
//                    var personName = name
//                    if (viewModel.getNumPeople().toInt() == 0) {
//                        personName = ""
//                    }
//                    if (spoofResult != null && spoofResult.isSpoof) {
//                        personName = "$personName (Spoof: ${spoofResult.score})"
//                    }
//                    boundingBoxTransform.mapRect(box)
//                    predictions.add(Prediction(box, personName))
//                }
//                withContext(Dispatchers.Main) {
//                    viewModel.faceDetectionMetricsState.value = metrics
//                    this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
//                    boundingBoxOverlay.invalidate()
//                    isProcessing = false
//                }
//            }
//            image.close()
//        }
//
//    data class Prediction(var bbox: RectF, var label: String)
//
//    inner class BoundingBoxOverlay(context: Context) :
//        SurfaceView(context), SurfaceHolder.Callback {
//
//        private val boxPaint =
//            Paint().apply {
//                color = Color.parseColor("#4D90caf9")
//                style = Paint.Style.FILL
//            }
//        private val textPaint =
//            Paint().apply {
//                strokeWidth = 2.0f
//                textSize = 36f
//                color = Color.WHITE
//            }
//
//        override fun surfaceCreated(holder: SurfaceHolder) {}
//
//        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//
//        override fun surfaceDestroyed(holder: SurfaceHolder) {}
//
//        override fun onDraw(canvas: Canvas) {
//            predictions.forEach {
//                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaint)
//                canvas.drawText(it.label, it.bbox.centerX(), it.bbox.centerY(), textPaint)
//            }
//        }
//    }
//}
package com.ml.shubham0204.facenet_android.presentation.components

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
import com.ml.shubham0204.facenet_android.presentation.screens.detect_screen.DetectScreenViewModel
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("ViewConstructor")
@ExperimentalGetImage
class FaceDetectionOverlay(
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val viewModel: DetectScreenViewModel
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
    this.predictions = arrayOf() // Clear previous predictions
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val previewView = PreviewView(context)
    val executor = ContextCompat.getMainExecutor(context)
    cameraProviderFuture.addListener(
        {
            val cameraProvider = cameraProviderFuture.get()
            val preview =
                Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(cameraFacing).build()
            val frameAnalyzer =
                ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
            frameAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                frameAnalyzer
            )
        },
        executor
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



    private val analyzer = ImageAnalysis.Analyzer { image ->
        if (isProcessing) {
            image.close()
            return@Analyzer
        }
        isProcessing = true

        // Transform android.net.Image to Bitmap
        frameBitmap = Bitmap.createBitmap(
            image.image!!.width,
            image.image!!.height,
            Bitmap.Config.ARGB_8888
        )
        frameBitmap.copyPixelsFromBuffer(image.planes[0].buffer)

        // Configure frameHeight and frameWidth for output2overlay transformation matrix
        // and apply it to `frameBitmap`
        if (!isImageTransformedInitialized) {
            imageTransform = Matrix()
            imageTransform.apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
            isImageTransformedInitialized = true
        }
        frameBitmap = Bitmap.createBitmap(
            frameBitmap,
            0,
            0,
            frameBitmap.width,
            frameBitmap.height,
            imageTransform,
            false
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
                        -1f,
                        1f,
                        overlayWidth.toFloat() / 2.0f,
                        overlayHeight.toFloat() / 2.0f
                    )
                }
            }
            isBoundingBoxTransformedInitialized = true
        }

        CoroutineScope(Dispatchers.Default).launch {
            val predictions = ArrayList<Prediction>()
            val (metrics, results) = viewModel.imageVectorUseCase.detectFaces(frameBitmap)

            // Assume metrics is of type FaceDetectionMetrics
            var hasRealFace = false
            var hasSpoofFace = false
            var capturedFaceBitmap: Bitmap? = null

            // Check if any faces were detected
            if (results.isEmpty()) {
                // No faces detected
                withContext(Dispatchers.Main) {
                    viewModel.onNoFaceDetected()
                    this@FaceDetectionOverlay.predictions = arrayOf()
                    boundingBoxOverlay.invalidate()
                    isProcessing = false
                }
            } else {
                // Process detection results
                for (result in results) {
                    val box = result.boundingBox.toRectF()
                    var label = "Face"
                    val isSpoof = result.spoofResult?.isSpoof ?: false

                    if (result.spoofResult != null && isSpoof) {
                        label = "Spoof: ${result.spoofResult.score}"
                        hasSpoofFace = true
                    } else {
                        // Real face detected
                        hasRealFace = true
                        label = "Real"
                        if (capturedFaceBitmap == null) {
                            capturedFaceBitmap = frameBitmap
                        }
                    }

                    // Apply transformation to the bounding box
                    boundingBoxTransform.mapRect(box)

                    // Increase the size of the bounding box by scaling its dimensions
                    val scaleFactor = 1.2f // Increase size by 20%
                    val widthIncrease = (box.width() * (scaleFactor - 1f)) / 2
                    val heightIncrease = (box.height() * (scaleFactor - 1f)) / 2
                    box.left -= widthIncrease
                    box.right += widthIncrease
                    box.top -= heightIncrease
                    box.bottom += heightIncrease

                    // Ensure the box stays within overlay bounds
                    box.left = maxOf(0f, box.left)
                    box.right = minOf(overlayWidth.toFloat(), box.right)
                    box.top = maxOf(0f, box.top)
                    box.bottom = minOf(overlayHeight.toFloat(), box.bottom)

                    predictions.add(Prediction(box, label, isSpoof))
                }

                withContext(Dispatchers.Main) {
                    if (hasRealFace) {
                        // Update ViewModel with FaceDetectionMetrics for real face
                        viewModel.onFaceDetected(
                            isReal = true,
                            bitmap = capturedFaceBitmap,
                            faceDetectionMetrics = metrics
                        )
                    } else if (hasSpoofFace) {
                        // For spoofed faces only
                        viewModel.onFaceDetected(
                            isReal = false,
                            bitmap = null,
                            faceDetectionMetrics = null
                        )
                    }

                    // Update UI elements
                    this@FaceDetectionOverlay.predictions = predictions.toTypedArray()
                    boundingBoxOverlay.invalidate()
                    isProcessing = false
                }
            }
            image.close()
        }
    }

    // Helper function to extract face bitmap from the frame
    private fun extractFaceBitmap(frameBitmap: Bitmap, boundingBox: android.graphics.Rect): Bitmap? {
        return try {
            // Ensure the bounding box is within the bitmap bounds
            val left = maxOf(0, boundingBox.left)
            val top = maxOf(0, boundingBox.top)
            val right = minOf(frameBitmap.width, boundingBox.right)
            val bottom = minOf(frameBitmap.height, boundingBox.bottom)

            val width = right - left
            val height = bottom - top

            if (width > 0 && height > 0) {
                Bitmap.createBitmap(frameBitmap, left, top, width, height)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FaceDetectionOverlay", "Error extracting face bitmap", e)
            null
        }
    }

    data class Prediction(
        var bbox: RectF,
        var label: String = "",
        var isSpoof: Boolean = false
    )

    inner class BoundingBoxOverlay(context: Context) :
        SurfaceView(context), SurfaceHolder.Callback {

        private val boxPaintFill =
            Paint().apply {
                color = Color.TRANSPARENT // Transparent background
                style = Paint.Style.FILL
            }
        private val boxPaintStroke =
            Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 4.0f // Border thickness
            }
        private val textPaint =
            Paint().apply {
                strokeWidth = 2.0f
                textSize = 48f // Increased text size
                textAlign = Paint.Align.RIGHT // Align text to the right
            }

        override fun surfaceCreated(holder: SurfaceHolder) {}

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder) {}

        override fun onDraw(canvas: Canvas) {
            predictions.forEach {
                // Set border color based on spoof status
                boxPaintStroke.color = if (it.isSpoof) Color.RED else Color.GREEN
                // Draw transparent fill
                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintFill)
                // Draw border
                canvas.drawRoundRect(it.bbox, 16f, 16f, boxPaintStroke)
                // Set text color based on spoof status
                textPaint.color = if (it.isSpoof) Color.RED else Color.GREEN
                // Draw label at top-right corner of the bounding box
                canvas.drawText(
                    it.label,
                    it.bbox.right - 8f, // Small offset from right edge
                    it.bbox.top + textPaint.textSize, // Align with top, account for text size
                    textPaint
                )
            }
        }
    }
}