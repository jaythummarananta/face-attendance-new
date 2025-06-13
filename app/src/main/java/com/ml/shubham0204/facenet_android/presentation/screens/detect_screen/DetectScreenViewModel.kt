//package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
//import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
//import com.ml.shubham0204.facenet_android.domain.PersonUseCase
//import org.koin.android.annotation.KoinViewModel
//
//@KoinViewModel
//class DetectScreenViewModel(
//    val personUseCase: PersonUseCase,
//    val imageVectorUseCase: ImageVectorUseCase
//) : ViewModel() {
//
//    val faceDetectionMetricsState = mutableStateOf<RecognitionMetrics?>(null)
//
//    fun getNumPeople(): Long = personUseCase.getCount()
//}
package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.ApiRepo.UserFaceAuthModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.annotation.KoinViewModel

data class FaceDetectionMetrics(

    val timeFaceDetection: Long,
    val timeFaceSpoofDetection: Long
)



@KoinViewModel
class DetectScreenViewModel(
    val personUseCase: PersonUseCase,
    val imageVectorUseCase: ImageVectorUseCase
) : ViewModel() {
    private val _attendanceResponseState = MutableStateFlow<UserFaceAuthModel?>(null)
    val attendanceResponseState: StateFlow<UserFaceAuthModel?> = _attendanceResponseState.asStateFlow()

    private val _faceDetectionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
    val faceDetectionMetricsState: StateFlow<RecognitionMetrics?> = _faceDetectionMetricsState

    private val _isFakeUserState = MutableStateFlow(false)
    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()

    private val _isLoadingState = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()

    private val _isShowDialogState = MutableStateFlow(false)
    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()

    private val _isFaceRealState = MutableStateFlow(false)
    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState.asStateFlow()

    // Add detection status text state
    private val _detectionStatusText = MutableStateFlow("Looking for face...")
    val detectionStatusText: StateFlow<String> = _detectionStatusText.asStateFlow()

    // Add detection status color state
    private val _detectionStatusColor = MutableStateFlow(Color.GRAY)
    val detectionStatusColor: StateFlow<Int> = _detectionStatusColor.asStateFlow()

    // Optional: Keep recognition metrics if still needed
    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState.asStateFlow()

    private var capturedFaceImage: Bitmap? = null

    // Updated method to handle FaceDetectionMetrics with text updates
    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, faceDetectionMetrics: RecognitionMetrics?) {
        _isFaceRealState.value = isReal

        if (isReal) {
            capturedFaceImage = bitmap
            _faceDetectionMetricsState.value = faceDetectionMetrics
            _isFakeUserState.value = false

            // Update status text and color for real face
            _detectionStatusText.value = "✓ Real Face Detected"
            _detectionStatusColor.value = Color.GREEN
        } else {
            capturedFaceImage = null
            _faceDetectionMetricsState.value = null
            _isFakeUserState.value = true

            // Update status text and color for fake/spoof face
            _detectionStatusText.value = "⚠ Spoof Face Detected"
            _detectionStatusColor.value = Color.RED
        }
    }

    // Method to update status when no face is detected
    fun onNoFaceDetected() {
        _isFaceRealState.value = false
        _isFakeUserState.value = false
        capturedFaceImage = null
        _faceDetectionMetricsState.value = null

        // Update status text for no face
        _detectionStatusText.value = "Looking for face..."
        _detectionStatusColor.value = Color.GRAY
    }

    // Optional: Keep this for backward compatibility if needed
    fun updateRecognitionMetrics(metrics: RecognitionMetrics?) {
        _recognitionMetricsState.value = metrics
    }

    fun setFakeUser(isFake: Boolean) {
        _isFakeUserState.value = isFake

        // Update text when manually setting fake user state
        if (isFake) {
            _detectionStatusText.value = "⚠ Fake User Detected"
            _detectionStatusColor.value = Color.RED
        }
    }

    fun getFakeUser(): Boolean = _isFakeUserState.value

    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage

    fun setAttendanceResponse(response: UserFaceAuthModel) {
        _attendanceResponseState.value = response
    }

    fun setLoading(isLoading: Boolean) {
        _isLoadingState.value = isLoading

        // Update status text during loading
        if (isLoading) {
            _detectionStatusText.value = "Processing..."
            _detectionStatusColor.value = Color.BLUE
        }
    }

    fun getLoading(): Boolean = _isLoadingState.value

    fun setShowDialog(showDialog: Boolean) {
        _isShowDialogState.value = showDialog
    }

    fun getNumPeople(): Long = personUseCase.getCount()

    fun resetState() {
        _attendanceResponseState.value = null
        _isLoadingState.value = false
        _isShowDialogState.value = false
        _isFaceRealState.value = false
        _faceDetectionMetricsState.value = null
        _isFakeUserState.value = false
        capturedFaceImage = null
        _recognitionMetricsState.value = null

        // Reset status text and color
        _detectionStatusText.value = "Looking for face..."
        _detectionStatusColor.value = Color.GRAY
    }
}
//
//@KoinViewModel
//class DetectScreenViewModel(
//    val personUseCase: PersonUseCase,
//    val imageVectorUseCase: ImageVectorUseCase
//) : ViewModel() {
//    private val _attendanceResponseState = MutableStateFlow<UserFaceAuthModel?>(null)
//    val attendanceResponseState: StateFlow<UserFaceAuthModel?> = _attendanceResponseState.asStateFlow()
//    // StateFlow for face detection metrics (new approach)
//    private val _faceDetectionMetricsState = MutableStateFlow<FaceDetectionMetrics?>(null)
//    val faceDetectionMetricsState: StateFlow<FaceDetectionMetrics?> = _faceDetectionMetricsState
//    private val _isFakeUserState = MutableStateFlow(false)
//    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()
//
//    private val _isLoadingState = MutableStateFlow(false)
//    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()
//
//    private val _isShowDialogState = MutableStateFlow(false)
//    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()
//
//    // StateFlow for face real/spoof detection
//    private val _isFaceRealState = MutableStateFlow(false)
//    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState
//
//    // Keep the original recognition metrics for backward compatibility
//    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
//    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState
//
//    private var capturedFaceImage: Bitmap? = null
//
//    // Called by FaceDetectionOverlay when a face is detected
//    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, metrics: RecognitionMetrics?) {
//        _isFaceRealState.value = isReal
//        capturedFaceImage = bitmap
//        _recognitionMetricsState.value  = metrics
//    }
//
//    // Method to update recognition metrics (if still needed for other parts of the app)
//    fun updateRecognitionMetrics(metrics: RecognitionMetrics) {
//        _recognitionMetricsState.value = metrics
//    }
//    fun setFakeUser(isFake: Boolean) {
//        _isFakeUserState.value = isFake
//    }
//    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage
//
//    fun setAttendanceResponse(response: UserFaceAuthModel) {
//        _attendanceResponseState.value = response
//    }
//
//    fun setLoading(isLoading: Boolean) {
//        _isLoadingState.value = isLoading
//    }
//
//    fun setShowDialog(showDialog: Boolean) {
//        _isShowDialogState.value = showDialog
//    }
//
//
//
//    fun getNumPeople(): Long = personUseCase.getCount()
//
//    fun resetState() {
//        _attendanceResponseState.value = null
//        _isLoadingState.value = false
//        _isShowDialogState.value = false
//        _isFaceRealState.value = false
//        // Reset face detection state if needed
//        // e.g., clear captured image or reset metrics
//    }
//}