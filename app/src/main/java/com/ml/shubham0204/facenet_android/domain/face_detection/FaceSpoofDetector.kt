package com.ananta.faceapp.domain.face_detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import kotlin.math.exp
import kotlin.time.DurationUnit
import kotlin.time.measureTime

/*

Utility class for interacting with FaceSpoofDetector

- It uses the MiniFASNet model from https://github.com/minivision-ai/Silent-Face-Anti-Spoofing
- The preprocessing methods are derived from
https://github.com/serengil/deepface/blob/master/deepface/models/spoofing/FasNet.py
- The model weights are in the PyTorch format. To convert them to the TFLite format,
  check the notebook linked in the README of the projectdetectSpoof
- An instance of this class is injected in ImageVectorUseCase.kt

*/
@Single
class FaceSpoofDetector(context: Context, useGpu: Boolean = false, useXNNPack: Boolean = false, useNNAPI: Boolean = false) {

    data class FaceSpoofResult(val isSpoof: Boolean, val score: Float, val timeMillis: Long)

    private val scale1 = 2.7f
    private val scale2 = 4.0f
    private val inputImageDim = 80
    private val outputDim = 3

    private var firstModelInterpreter: Interpreter
    private var secondModelInterpreter: Interpreter
    private val imageTensorProcessor = ImageProcessor.Builder()
        .add(CastOp(DataType.FLOAT32))
        .build()

    init {
        // Initialize TFLiteInterpreter
        val interpreterOptions =
            Interpreter.Options().apply {
                // Add the GPU Delegate if supported.
                // See -> https://www.tensorflow.org/lite/performance/gpu#android
                if (useGpu) {
                    if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                        addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
                    }
                } else {
                    // Number of threads for computation
                    numThreads = 4
                }
                useXNNPACK = useXNNPack
                this.useNNAPI = useNNAPI
            }
        firstModelInterpreter =
            Interpreter(FileUtil.loadMappedFile(context, "spoof_model_scale_2_7.tflite"), interpreterOptions)
        secondModelInterpreter =
            Interpreter(FileUtil.loadMappedFile(context, "spoof_model_scale_4_0.tflite"), interpreterOptions)
    }

//    suspend fun detectSpoof(frameImage: Bitmap, faceRect: Rect): FaceSpoofResult =
//        withContext(Dispatchers.Default) {
//            // Crop the images and scale the bounding boxes
//            // with the given two constants
//            // and perform RGB -> BGR conversion
//            val croppedImage1 =
//                crop(
//                    origImage = frameImage,
//                    bbox = faceRect,
//                    bboxScale = scale1,
//                    targetWidth = inputImageDim,
//                    targetHeight = inputImageDim
//                )
//
//
//
//            Log.d("FaceSpoofDetector", "croppedImage1111: ${croppedImage1.height}x ${croppedImage1.width}")
//
//            for (i in 0 until croppedImage1.width) {
//                for (j in 0 until croppedImage1.height) {
//                    croppedImage1[i, j] = Color.rgb(
//                        Color.blue(croppedImage1[i, j]),
//                        Color.green(croppedImage1[i, j]),
//                        Color.red(croppedImage1[i, j])
//                    )
//                }
//            }
//            val croppedImage2 =
//                crop(
//                    origImage = frameImage,
//                    bbox = faceRect,
//                    bboxScale = scale2,
//                    targetWidth = inputImageDim,
//                    targetHeight = inputImageDim
//                )
//            for (i in 0 until croppedImage2.width) {
//                for (j in 0 until croppedImage2.height) {
//                    croppedImage2[i, j] = Color.rgb(
//                        Color.blue(croppedImage2[i, j]),
//                        Color.green(croppedImage2[i, j]),
//                        Color.red(croppedImage2[i, j])
//                    )
//                }
//            }
//
//            Log.d("FaceSpoofDetector", "croppedImage1: ${croppedImage1}")
//
//            val input1 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage1)).buffer
//
//
//            Log.d("FaceSpoofDetector", "input1: ${input1}")
//
//            val input2 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage2)).buffer
//            val output1 = arrayOf(FloatArray(outputDim))
//            val output2 = arrayOf(FloatArray(outputDim))
//            Log.d("FaceSpoofDetector", "input1: ${input1}")
//            Log.d("FaceSpoofDetector", "input2: ${input2}")
//
//            Log.d("FaceSpoofDetector", "output1: ${output1[0]}")
//
//
//            val time = measureTime {
//                firstModelInterpreter.run(input1, output1)
//                secondModelInterpreter.run(input2, output2)
//            }.toLong(DurationUnit.MILLISECONDS)
//
//            val output = softMax(output1[0]).zip(softMax(output2[0])).map {
//                (it.first + it.second)
//            }
//            Log.d("FaceSpoofDetector", "output1: ${output1[0]}")
////            val output = softMax(output1[0]).map { it }
//            Log.d("FaceSpoofDetector", "calculate output: ${output}")
//
//
//            val label = output.indexOf(output.max())
//
//            Log.d("FaceSpoofDetector", "calculate label: ${label}")
//
////            val iSpoof = label != 1
//
//            val score = output[label] / 2f
//            val iSpoof = label != 1 && score > 0.7f
//
//            Log.d("FaceSpoofDetector", "isSpoof: $iSpoof, score: $score, time: $time")
//
//            return@withContext FaceSpoofResult(isSpoof = iSpoof, score = score, timeMillis = time)
//        }
suspend fun detectSpoof(frameImage: Bitmap, faceRect: Rect): FaceSpoofResult =
    withContext(Dispatchers.Default) {
        // Check if face is too close based on bounding box size
        if (faceRect.width() > frameImage.width * 0.6f || faceRect.height() > frameImage.height * 0.6f) {
            Log.d("FaceSpoofDetector", "Face too close, skipping spoof detection")
            return@withContext FaceSpoofResult(isSpoof = false, score = 0f, timeMillis = 0)
        }

        // Crop images with dynamic scaling
        val croppedImage1 = crop(
            origImage = frameImage,
            bbox = faceRect,
            bboxScale = scale1,
            targetWidth = inputImageDim,
            targetHeight = inputImageDim
        )

        // RGB to BGR conversion
        for (i in 0 until croppedImage1.width) {
            for (j in 0 until croppedImage1.height) {
                croppedImage1[i, j] = Color.rgb(
                    Color.blue(croppedImage1[i, j]),
                    Color.green(croppedImage1[i, j]),
                    Color.red(croppedImage1[i, j])
                )
            }
        }

        val croppedImage2 = crop(
            origImage = frameImage,
            bbox = faceRect,
            bboxScale = scale2,
            targetWidth = inputImageDim,
            targetHeight = inputImageDim
        )

        for (i in 0 until croppedImage2.width) {
            for (j in 0 until croppedImage2.height) {
                croppedImage2[i, j] = Color.rgb(
                    Color.blue(croppedImage2[i, j]),
                    Color.green(croppedImage2[i, j]),
                    Color.red(croppedImage2[i, j])
                )
            }
        }

        // Check image brightness
        if (!isImageBrightEnough(croppedImage1) || !isImageBrightEnough(croppedImage2)) {
            Log.d("FaceSpoofDetector", "Image too dark, skipping spoof detection")
            return@withContext FaceSpoofResult(isSpoof = false, score = 0f, timeMillis = 0)
        }

        Log.d("FaceSpoofDetector", "croppedImage1: ${croppedImage1.height}x${croppedImage1.width}")
        Log.d("FaceSpoofDetector", "croppedImage2: ${croppedImage2.height}x${croppedImage2.width}")

        val input1 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage1)).buffer
        val input2 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage2)).buffer
        val output1 = arrayOf(FloatArray(outputDim))
        val output2 = arrayOf(FloatArray(outputDim))

        val time = measureTime {
            firstModelInterpreter.run(input1, output1)
            secondModelInterpreter.run(input2, output2)
        }.toLong(DurationUnit.MILLISECONDS)

        val softmaxOutput1 = softMax(output1[0])
        val softmaxOutput2 = softMax(output2[0])
        val output = softmaxOutput1.zip(softmaxOutput2).map { (it.first + it.second) / 2 }

        Log.d("FaceSpoofDetector", "Softmax output1: ${softmaxOutput1.contentToString()}")
        Log.d("FaceSpoofDetector", "Softmax output2: ${softmaxOutput2.contentToString()}")
        Log.d("FaceSpoofDetector", "Combined output: $output")

        val label = output.indexOf(output.max())
        val score = output[label]
        Log.d("FaceSpoofDetector", "Label: $label, FaceSpoofDetector Score: $score")
//        val isSpoof = label != 1 && score > spoofConfidenceThreshold
        val isSpoof = label != 1 && score > 0.7f
        Log.d("FaceSpoofDetector", "isSpoof: $isSpoof, score: $score, time: $time")

        FaceSpoofResult(isSpoof = isSpoof, score = score, timeMillis = time)
    }

    private fun isImageBrightEnough(bitmap: Bitmap): Boolean {
        var totalBrightness = 0.0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in pixels) {
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            totalBrightness += (r + g + b) / 3.0
        }
        val avgBrightness = totalBrightness / pixels.size
        Log.d("FaceSpoofDetector", "Image brightness: $avgBrightness")
        return avgBrightness > 50 // Adjust threshold as needed
    }
    private fun softMax(x: FloatArray): FloatArray {
        val exp = x.map { exp(it) }
        val expSum = exp.sum()
        return exp.map { it / expSum }.toFloatArray()
    }

    private fun crop(
        origImage: Bitmap,
        bbox: Rect,
        bboxScale: Float,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        val srcWidth = origImage.width
        val srcHeight = origImage.height
        val scaledBox = getScaledBox(srcWidth, srcHeight, bbox, bboxScale)
        val croppedBitmap =
            Bitmap.createBitmap(
                origImage,
                scaledBox.left,
                scaledBox.top,
                scaledBox.width(),
                scaledBox.height()
            )
        return Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, true)
    }

//    private fun getScaledBox(srcWidth: Int, srcHeight: Int, box: Rect, bboxScale: Float): Rect {
//        val x = box.left
//        val y = box.top
//        val w = box.width()
//        val h = box.height()
//
//        val scale = floatArrayOf((srcHeight - 1f) / h, (srcWidth - 1f) / w, bboxScale).min()
//        val newWidth = w * scale
//        val newHeight = h * scale
//        val centerX = w / 2 + x
//        val centerY = h / 2 + y
//        var topLeftX = centerX - newWidth / 2
//        var topLeftY = centerY - newHeight / 2
//        var bottomRightX = centerX + newWidth / 2
//        var bottomRightY = centerY + newHeight / 2
//        if (topLeftX < 0) {
//            bottomRightX -= topLeftX
//            topLeftX = 0f
//        }
//        if (topLeftY < 0) {
//            bottomRightY -= topLeftY
//            topLeftY = 0f
//        }
//        if (bottomRightX > srcWidth - 1) {
//            topLeftX -= (bottomRightX - (srcWidth - 1))
//            bottomRightX = (srcWidth - 1).toFloat()
//        }
//        if (bottomRightY > srcHeight - 1) {
//            topLeftY -= (bottomRightY - (srcHeight - 1))
//            bottomRightY = (srcHeight - 1).toFloat()
//        }
//        return Rect(topLeftX.toInt(), topLeftY.toInt(), bottomRightX.toInt(), bottomRightY.toInt())
//    }
private fun getScaledBox(srcWidth: Int, srcHeight: Int, box: Rect, bboxScale: Float): Rect {
    val x = box.left
    val y = box.top
    val w = box.width()
    val h = box.height()
    // Dynamically adjust scale based on face size
    val dynamicScale = if (w > srcWidth * 0.5f || h > srcHeight * 0.5f) {
        bboxScale * 0.8f // Reduce scale for close-up faces
    } else {
        bboxScale
    }
    val scale = floatArrayOf((srcHeight - 1f) / h, (srcWidth - 1f) / w, dynamicScale).min()
    val newWidth = w * scale
    val newHeight = h * scale
    val centerX = w / 2 + x
    val centerY = h / 2 + y
    var topLeftX = centerX - newWidth / 2
    var topLeftY = centerY - newHeight / 2
    var bottomRightX = centerX + newWidth / 2
    var bottomRightY = centerY + newHeight / 2
    // Clamp coordinates
    if (topLeftX < 0) {
        bottomRightX -= topLeftX
        topLeftX = 0f
    }
    if (topLeftY < 0) {
        bottomRightY -= topLeftY
        topLeftY = 0f
    }
    if (bottomRightX > srcWidth - 1) {
        topLeftX -= (bottomRightX - (srcWidth - 1))
        bottomRightX = (srcWidth - 1).toFloat()
    }
    if (bottomRightY > srcHeight - 1) {
        topLeftY -= (bottomRightY - (srcHeight - 1))
        bottomRightY = (srcHeight - 1).toFloat()
    }
    return Rect(topLeftX.toInt(), topLeftY.toInt(), bottomRightX.toInt(), bottomRightY.toInt())
}
}



//package com.ananta.faceapp.domain.face_detection
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Color
//import android.graphics.Rect
//import android.util.Log
//import androidx.core.graphics.get
//import androidx.core.graphics.set
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.koin.core.annotation.Single
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.gpu.CompatibilityList
//import org.tensorflow.lite.gpu.GpuDelegate
//import org.tensorflow.lite.support.common.FileUtil
//import org.tensorflow.lite.support.common.ops.CastOp
//import org.tensorflow.lite.support.image.ImageProcessor
//import org.tensorflow.lite.support.image.TensorImage
//import kotlin.math.exp
//import kotlin.time.DurationUnit
//import kotlin.time.measureTime
//
//@Single
//class FaceSpoofDetector(context: Context, useGpu: Boolean = false, useXNNPack: Boolean = false, useNNAPI: Boolean = false) {
//    data class FaceSpoofResult(val isSpoof: Boolean, val score: Float, val timeMillis: Long)
//
//    private val scale1 = 2.7f
//    private val scale2 = 4.0f
//    private val inputImageDim = 80
//    private val outputDim = 3
//
//    private var firstModelInterpreter: Interpreter
//    private var secondModelInterpreter: Interpreter
//    private val imageTensorProcessor = ImageProcessor.Builder()
//        .add(CastOp(DataType.FLOAT32))
//        .build()
//
//    init {
//        val interpreterOptions = Interpreter.Options().apply {
//            if (useGpu && CompatibilityList().isDelegateSupportedOnThisDevice) {
//                addDelegate(GpuDelegate(CompatibilityList().bestOptionsForThisDevice))
//            } else {
//                numThreads = 4
//            }
//            useXNNPACK = useXNNPack
//            this.useNNAPI = useNNAPI
//        }
//        firstModelInterpreter = Interpreter(FileUtil.loadMappedFile(context, "spoof_model_scale_2_7.tflite"), interpreterOptions)
//        secondModelInterpreter = Interpreter(FileUtil.loadMappedFile(context, "spoof_model_scale_4_0.tflite"), interpreterOptions)
//    }
//
//    suspend fun detectSpoof(frameImage: Bitmap, faceRect: Rect): FaceSpoofResult =
//        withContext(Dispatchers.Default) {
//            Log.d("FaceSpoofDetector", "Input frame: ${frameImage.width}x${frameImage.height}, rect=$faceRect")
//            val croppedImage1 = crop(frameImage, faceRect, scale1, inputImageDim, inputImageDim)
//            Log.d("FaceSpoofDetector", "Cropped image1: ${croppedImage1.width}x${croppedImage1.height}")
//
//            for (i in 0 until croppedImage1.width) {
//                for (j in 0 until croppedImage1.height) {
//                    croppedImage1[i, j] = Color.rgb(
//                        Color.blue(croppedImage1[i, j]),
//                        Color.green(croppedImage1[i, j]),
//                        Color.red(croppedImage1[i, j])
//                    )
//                }
//            }
//
//            val croppedImage2 = crop(frameImage, faceRect, scale2, inputImageDim, inputImageDim)
//            Log.d("FaceSpoofDetector", "Cropped image2: ${croppedImage2.width}x${croppedImage2.height}")
//
//            for (i in 0 until croppedImage2.width) {
//                for (j in 0 until croppedImage2.height) {
//                    croppedImage2[i, j] = Color.rgb(
//                        Color.blue(croppedImage2[i, j]),
//                        Color.green(croppedImage2[i, j]),
//                        Color.red(croppedImage2[i, j])
//                    )
//                }
//            }
//
//            val input1 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage1)).buffer
//            val input2 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage2)).buffer
//            val output1 = arrayOf(FloatArray(outputDim))
//            val output2 = arrayOf(FloatArray(outputDim))
//
//            val time = measureTime {
//                firstModelInterpreter.run(input1, output1)
//                secondModelInterpreter.run(input2, output2)
//            }.toLong(DurationUnit.MILLISECONDS)
//
//            val output = softMax(output1[0]).zip(softMax(output2[0])).map { (it.first + it.second) / 2 }
//            Log.d("FaceSpoofDetector", "Output: $output")
//
//            val label = output.indexOf(output.max())
//            val isSpoof = label != 1
//            val score = output[label]
//
//            Log.d("FaceSpoofDetector", "isSpoof: $isSpoof, score: $score, time: $time")
//            FaceSpoofResult(isSpoof = isSpoof, score = score, timeMillis = time)
//        }
//
//    private fun softMax(x: FloatArray): FloatArray {
//        val exp = x.map { exp(it) }
//        val expSum = exp.sum()
//        return exp.map { it / expSum }.toFloatArray()
//    }
//
//    private fun crop(origImage: Bitmap, bbox: Rect, bboxScale: Float, targetWidth: Int, targetHeight: Int): Bitmap {
//        val scaledBox = getScaledBox(origImage.width, origImage.height, bbox, bboxScale)
//        Log.d("FaceSpoofDetector", "Scaled box: $scaledBox")
//        val croppedBitmap = try {
//            Bitmap.createBitmap(
//                origImage,
//                scaledBox.left,
//                scaledBox.top,
//                scaledBox.width(),
//                scaledBox.height()
//            )
//        } catch (e: Exception) {
//            Log.e("FaceSpoofDetector", "Error cropping bitmap: ${e.message}")
//            Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
//        }
//        return Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, true)
//    }
//
//    private fun getScaledBox(srcWidth: Int, srcHeight: Int, box: Rect, bboxScale: Float): Rect {
//        val x = box.left
//        val y = box.top
//        val w = box.width()
//        val h = box.height()
//        val scale = floatArrayOf((srcHeight - 1f) / h, (srcWidth - 1f) / w, bboxScale).min()
//        val newWidth = w * scale
//        val newHeight = h * scale
//        val centerX = w / 2 + x
//        val centerY = h / 2 + y
//        var topLeftX = centerX - newWidth / 2
//        var topLeftY = centerY - newHeight / 2
//        var bottomRightX = centerX + newWidth / 2
//        var bottomRightY = centerY + newHeight / 2
//        if (topLeftX < 0) {
//            bottomRightX -= topLeftX
//            topLeftX = 0f
//        }
//        if (topLeftY < 0) {
//            bottomRightY -= topLeftY
//            topLeftY = 0f
//        }
//        if (bottomRightX > srcWidth - 1) {
//            topLeftX -= (bottomRightX - (srcWidth - 1))
//            bottomRightX = (srcWidth - 1).toFloat()
//        }
//        if (bottomRightY > srcHeight - 1) {
//            topLeftY -= (bottomRightY - (srcHeight - 1))
//            bottomRightY = (srcHeight - 1).toFloat()
//        }
//        return Rect(topLeftX.toInt(), topLeftY.toInt(), bottomRightX.toInt(), bottomRightY.toInt())
//    }
//}