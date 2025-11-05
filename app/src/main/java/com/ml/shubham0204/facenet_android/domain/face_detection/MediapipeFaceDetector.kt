package com.ananta.faceapp.domain.face_detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.core.graphics.toRect
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

// Utility class for interacting with Mediapipe's Face Detector
// See https://ai.google.dev/edge/mediapipe/solutions/vision/face_detector/android
@Single
class MediapipeFaceDetector(private val context: Context) {

    // The model is stored in the assets folder
    private val modelName = "blaze_face_short_range.tflite"
    private val baseOptions = BaseOptions.builder().setModelAssetPath(modelName).build()
    private val faceDetectorOptions =
        FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .build()
    private val faceDetector = FaceDetector.createFromOptions(context, faceDetectorOptions)



    // Detects multiple faces from the `frameBitmap`
    // and returns pairs of (croppedFace , boundingBoxRect)
    // Used by ImageVectorUseCase.kt
    suspend fun getAllCroppedFaces(frameBitmap: Bitmap): List<Pair<Bitmap, Rect>> =
        withContext(Dispatchers.IO) {
            return@withContext faceDetector
                .detect(BitmapImageBuilder(frameBitmap).build())
                .detections()
                .filter { validateRect(frameBitmap, it.boundingBox().toRect()) }
                .map { detection -> detection.boundingBox().toRect() }
                .map { rect ->
                    val croppedBitmap =
                        Bitmap.createBitmap(
                            frameBitmap,
                            rect.left,
                            rect.top,
                            rect.width(),
                            rect.height()
                        )

                    Log.d("FaceDetector", "validateRect ${frameBitmap.width}x ${frameBitmap.height}")
                    Log.d("FaceDetector", "croppedImage: ${croppedBitmap.height}x ${croppedBitmap.width}")
                    Log.d("FaceDetector", " rect.left: ${ rect.left}x ${ rect.top}x ${ rect.width()}x ${ rect.height()}")
                    Pair(croppedBitmap, rect)
                }
        }

    // Check if the bounds of `boundingBox` fit within the
    // limits of `cameraFrameBitmap`
    private fun validateRect(cameraFrameBitmap: Bitmap, boundingBox: Rect): Boolean {
        Log.d("FaceDetector", "validateRect validateRect:========= ${boundingBox.left >= 0 &&
                boundingBox.top >= 0 &&
                (boundingBox.left + boundingBox.width()) < cameraFrameBitmap.width &&
                (boundingBox.top + boundingBox.height()) < cameraFrameBitmap.height}")
        return boundingBox.left >= 0 &&
            boundingBox.top >= 0 &&
            (boundingBox.left + boundingBox.width()) < cameraFrameBitmap.width &&
            (boundingBox.top + boundingBox.height()) < cameraFrameBitmap.height
    }
}

//package com.ananta.faceapp.domain.face_detection
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Rect
//import android.util.Log
//import androidx.core.graphics.toRect
//import com.google.mediapipe.framework.image.BitmapImageBuilder
//import com.google.mediapipe.tasks.core.BaseOptions
//import com.google.mediapipe.tasks.vision.core.RunningMode
//import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.koin.core.annotation.Single
//
//@Single
//class MediapipeFaceDetector(private val context: Context) {
//    private val modelName = "blaze_face_short_range.tflite"
//    private val baseOptions = BaseOptions.builder().setModelAssetPath(modelName).build()
//    private val faceDetectorOptions = FaceDetector.FaceDetectorOptions.builder()
//        .setBaseOptions(baseOptions)
//        .setRunningMode(RunningMode.IMAGE)
//        .build()
//    private val faceDetector = FaceDetector.createFromOptions(context, faceDetectorOptions)
//
//    suspend fun getAllCroppedFaces(frameBitmap: Bitmap): List<Pair<Bitmap, Rect>> =
//        withContext(Dispatchers.IO) {
//            Log.d("MediapipeFaceDetector", "Input bitmap: ${frameBitmap.width}x${frameBitmap.height}")
//            val detections = faceDetector.detect(BitmapImageBuilder(frameBitmap).build()).detections()
//            Log.d("MediapipeFaceDetector", "Detected ${detections.size} faces")
//            return@withContext detections
//                .filter { validateRect(frameBitmap, it.boundingBox().toRect()) }
//                .map { detection ->
//                    val rect =detection.boundingBox().toRect()
//                    Log.d("MediapipeFaceDetector", "Face rect: left=${rect.left}, top=${rect.top}, width=${rect.width()}, height=${rect.height()}")
//                    val croppedBitmap = try {
//                        Bitmap.createBitmap(
//                            frameBitmap,
//                            rect.left,
//                            rect.top,
//                            rect.width(),
//                            rect.height()
//                        )
//                    } catch (e: Exception) {
//                        Log.e("MediapipeFaceDetector", "Error cropping face: ${e.message}")
//                        null
//                    }
//                    Pair(croppedBitmap, rect)
//                }
//                .filter { it.first != null }
//                .map { Pair(it.first!!, it.second) }
//        }
//
//    private fun validateRect(cameraFrameBitmap: Bitmap, boundingBox: Rect): Boolean {
//        val isValid = boundingBox.left >= 0 &&
//                boundingBox.top >= 0 &&
//                (boundingBox.left + boundingBox.width()) <= cameraFrameBitmap.width &&
//                (boundingBox.top + boundingBox.height()) <= cameraFrameBitmap.height
//        Log.d("MediapipeFaceDetector", "Validate rect: $isValid, rect=$boundingBox, bitmap=${cameraFrameBitmap.width}x${cameraFrameBitmap.height}")
//        return isValid
//    }
//}