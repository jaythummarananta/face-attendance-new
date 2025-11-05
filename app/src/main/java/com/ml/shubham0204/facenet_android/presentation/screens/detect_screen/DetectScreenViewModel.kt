////package com.ananta.faceapp.presentation.screens.detect_screen
////
////import android.graphics.Bitmap
////import android.graphics.Color
////import androidx.lifecycle.ViewModel
////import com.ananta.faceapp.data.RecognitionMetrics
////import com.ananta.faceapp.domain.ImageVectorUseCase
////import com.ananta.faceapp.domain.PersonUseCase
////import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.StateFlow
////import kotlinx.coroutines.flow.asStateFlow
////import org.koin.android.annotation.KoinViewModel
////
////data class FaceDetectionMetrics(
////
////    val timeFaceDetection: Long,
////    val timeFaceSpoofDetection: Long
////)
////
////
////
////@KoinViewModel
////class DetectScreenViewModel(
////    val personUseCase: PersonUseCase,
////    val imageVectorUseCase: ImageVectorUseCase
////) : ViewModel() {
////    private val _attendanceResponseState = MutableStateFlow<AttendanceModel?>(null)
////    val attendanceResponseState: StateFlow<AttendanceModel?> = _attendanceResponseState.asStateFlow()
////
////    private val _faceDetectionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
////    val faceDetectionMetricsState: StateFlow<RecognitionMetrics?> = _faceDetectionMetricsState
////
////    private val _isFakeUserState = MutableStateFlow(false)
////    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()
////
////    private val _isLoadingState = MutableStateFlow(false)
////    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()
////
////    private val _isFaceCenteredState = MutableStateFlow(false)
////    val isFaceCenteredState: StateFlow<Boolean> = _isFaceCenteredState.asStateFlow()
////
////    private val _isShowDialogState = MutableStateFlow(false)
////    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()
////
////    private val _isFaceRealState = MutableStateFlow(false)
////    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState.asStateFlow()
////
////    // Add detection status text state
////    private val _detectionStatusText = MutableStateFlow("Looking for face...")
////    val detectionStatusText: StateFlow<String> = _detectionStatusText.asStateFlow()
////
////    // Add detection status color state
////    private val _detectionStatusColor = MutableStateFlow(Color.GRAY)
////    val detectionStatusColor: StateFlow<Int> = _detectionStatusColor.asStateFlow()
////
////    // Optional: Keep recognition metrics if still needed
////    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
////    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState.asStateFlow()
////
////    private var capturedFaceImage: Bitmap? = null
////
////    // Updated method to handle FaceDetectionMetrics with text updates
////    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, faceDetectionMetrics: RecognitionMetrics?) {
////        _isFaceRealState.value = isReal
////
////        if (isReal) {
////            capturedFaceImage = bitmap
////            _faceDetectionMetricsState.value = faceDetectionMetrics
////            _isFakeUserState.value = false
////
////            // Update status text and color for real face
////            _detectionStatusText.value = "✓ Real Face Detected"
////            _detectionStatusColor.value = Color.GREEN
////        } else {
////            capturedFaceImage = null
////            _faceDetectionMetricsState.value = null
////            _isFakeUserState.value = true
////
////            // Update status text and color for fake/spoof face
////            _detectionStatusText.value = "⚠ Spoof Face Detected"
////            _detectionStatusColor.value = Color.RED
////        }
////    }
////
////    // Method to update status when no face is detected
////    fun onNoFaceDetected() {
////        _isFaceRealState.value = false
////        _isFakeUserState.value = false
////        capturedFaceImage = null
////        _faceDetectionMetricsState.value = null
////
////        // Update status text for no face
////        _detectionStatusText.value = "Looking for face..."
////        _detectionStatusColor.value = Color.GRAY
////    }
////
////    // Optional: Keep this for backward compatibility if needed
////    fun updateRecognitionMetrics(metrics: RecognitionMetrics?) {
////        _recognitionMetricsState.value = metrics
////    }
////
////    fun setFakeUser(isFake: Boolean) {
////        _isFakeUserState.value = isFake
////
////        // Update text when manually setting fake user state
////        if (isFake) {
////            _detectionStatusText.value = "⚠ Fake User Detected"
////            _detectionStatusColor.value = Color.RED
////        }
////    }
////
////    fun getFakeUser(): Boolean = _isFakeUserState.value
////
////    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage
////
////    fun setAttendanceResponse(response: AttendanceModel) {
////        _attendanceResponseState.value = response
////    }
////
////    fun setLoading(isLoading: Boolean) {
////        _isLoadingState.value = isLoading
////
////        // Update status text during loading
////        if (isLoading) {
////            _detectionStatusText.value = "Processing..."
////            _detectionStatusColor.value = Color.BLUE
////        }
////    }
////
////    fun getLoading(): Boolean = _isLoadingState.value
////
////    fun setShowDialog(showDialog: Boolean) {
////        _isShowDialogState.value = showDialog
////    }
////
////    fun getNumPeople(): Long = personUseCase.getCount()
////
////    fun resetState() {
////        _attendanceResponseState.value = null
////        _isLoadingState.value = false
////        _isShowDialogState.value = false
////        _isFaceRealState.value = false
////        _faceDetectionMetricsState.value = null
////        _isFakeUserState.value = false
////        capturedFaceImage = null
////        _recognitionMetricsState.value = null
////
////        // Reset status text and color
////        _detectionStatusText.value = "Looking for face..."
////        _detectionStatusColor.value = Color.GRAY
////    }
////}
//////
//////@KoinViewModel
//////class DetectScreenViewModel(
//////    val personUseCase: PersonUseCase,
//////    val imageVectorUseCase: ImageVectorUseCase
//////) : ViewModel() {
//////    private val _attendanceResponseState = MutableStateFlow<UserFaceAuthModel?>(null)
//////    val attendanceResponseState: StateFlow<UserFaceAuthModel?> = _attendanceResponseState.asStateFlow()
//////    // StateFlow for face detection metrics (new approach)
//////    private val _faceDetectionMetricsState = MutableStateFlow<FaceDetectionMetrics?>(null)
//////    val faceDetectionMetricsState: StateFlow<FaceDetectionMetrics?> = _faceDetectionMetricsState
//////    private val _isFakeUserState = MutableStateFlow(false)
//////    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()
//////
//////    private val _isLoadingState = MutableStateFlow(false)
//////    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()
//////
//////    private val _isShowDialogState = MutableStateFlow(false)
//////    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()
//////
//////    // StateFlow for face real/spoof detection
//////    private val _isFaceRealState = MutableStateFlow(false)
//////    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState
//////
//////    // Keep the original recognition metrics for backward compatibility
//////    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
//////    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState
//////
//////    private var capturedFaceImage: Bitmap? = null
//////
//////    // Called by FaceDetectionOverlay when a face is detected
//////    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, metrics: RecognitionMetrics?) {
//////        _isFaceRealState.value = isReal
//////        capturedFaceImage = bitmap
//////        _recognitionMetricsState.value  = metrics
//////    }
//////
//////    // Method to update recognition metrics (if still needed for other parts of the app)
//////    fun updateRecognitionMetrics(metrics: RecognitionMetrics) {
//////        _recognitionMetricsState.value = metrics
//////    }
//////    fun setFakeUser(isFake: Boolean) {
//////        _isFakeUserState.value = isFake
//////    }
//////    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage
//////
//////    fun setAttendanceResponse(response: UserFaceAuthModel) {
//////        _attendanceResponseState.value = response
//////    }
//////
//////    fun setLoading(isLoading: Boolean) {
//////        _isLoadingState.value = isLoading
//////    }
//////
//////    fun setShowDialog(showDialog: Boolean) {
//////        _isShowDialogState.value = showDialog
//////    }
//////
//////
//////
//////    fun getNumPeople(): Long = personUseCase.getCount()
//////
//////    fun resetState() {
//////        _attendanceResponseState.value = null
//////        _isLoadingState.value = false
//////        _isShowDialogState.value = false
//////        _isFaceRealState.value = false
//////        // Reset face detection state if needed
//////        // e.g., clear captured image or reset metrics
//////    }
//////}
//
//package com.ananta.faceapp.presentation.screens.detect_screen
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import androidx.lifecycle.ViewModel
//import com.ananta.faceapp.data.RecognitionMetrics
//import com.ananta.faceapp.domain.ImageVectorUseCase
//import com.ananta.faceapp.domain.PersonUseCase
//import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import org.koin.android.annotation.KoinViewModel
//
//data class FaceDetectionMetrics(
//    val timeFaceDetection: Long,
//    val timeFaceSpoofDetection: Long
//)
//
//@KoinViewModel
//class DetectScreenViewModel(
//    val personUseCase: PersonUseCase,
//    val imageVectorUseCase: ImageVectorUseCase
//) : ViewModel() {
//    private val _attendanceResponseState = MutableStateFlow<AttendanceModel?>(null)
//    val attendanceResponseState: StateFlow<AttendanceModel?> = _attendanceResponseState.asStateFlow()
//
//    private val _faceDetectionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
//    val faceDetectionMetricsState: StateFlow<RecognitionMetrics?> = _faceDetectionMetricsState
//
//    private val _isFakeUserState = MutableStateFlow(false)
//    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()
//
//    private val _isLoadingState = MutableStateFlow(false)
//    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()
//
//    private val _isFaceCenteredState = MutableStateFlow(false)
//    val isFaceCenteredState: StateFlow<Boolean> = _isFaceCenteredState.asStateFlow()
//
//    private val _isShowDialogState = MutableStateFlow(false)
//    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()
//
//    private val _isFaceRealState = MutableStateFlow(false)
//    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState.asStateFlow()
//
//    private val _detectionStatusText = MutableStateFlow("Looking for face...")
//    val detectionStatusText: StateFlow<String> = _detectionStatusText.asStateFlow()
//
//    private val _detectionStatusColor = MutableStateFlow(Color.GRAY)
//    val detectionStatusColor: StateFlow<Int> = _detectionStatusColor.asStateFlow()
//
//    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
//    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState.asStateFlow()
//
//    private var capturedFaceImage: Bitmap? = null
//    private var faceCenteredCounter = 0
//    private val REQUIRED_CENTERED_FRAMES = 10 // Face must be centered for 10 consecutive frames
//
//    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, faceDetectionMetrics: RecognitionMetrics?, isCentered: Boolean) {
//        _isFaceRealState.value = isReal
//
//        if (isReal) {
//            if (isCentered) {
//                faceCenteredCounter++
//
//                if (faceCenteredCounter >= REQUIRED_CENTERED_FRAMES) {
//                    // Face is properly centered for required duration
//                    _isFaceCenteredState.value = true
//                    capturedFaceImage = bitmap
//                    _faceDetectionMetricsState.value = faceDetectionMetrics
//                    _isFakeUserState.value = false
//                    _detectionStatusText.value = "✓ Face Centered - Capturing..."
//                    _detectionStatusColor.value = Color.GREEN
//                } else {
//                    // Still waiting for face to stay centered
//                    _detectionStatusText.value = "Hold steady... (${faceCenteredCounter}/${REQUIRED_CENTERED_FRAMES})"
//                    _detectionStatusColor.value = Color.YELLOW
//                }
//            } else {
//                // Face detected but not centered
//                faceCenteredCounter = 0
//                _isFaceCenteredState.value = false
//                capturedFaceImage = null
//                _detectionStatusText.value = "Please center your face"
//                _detectionStatusColor.value = Color.rgb(255, 165, 0) // Orange
//            }
//        } else {
//            // Fake face detected
//            faceCenteredCounter = 0
//            _isFaceCenteredState.value = false
//            capturedFaceImage = null
//            _faceDetectionMetricsState.value = null
//            _isFakeUserState.value = true
//            _detectionStatusText.value = "⚠ Spoof Face Detected"
//            _detectionStatusColor.value = Color.RED
//        }
//    }
//
//    fun onNoFaceDetected() {
//        faceCenteredCounter = 0
//        _isFaceRealState.value = false
//        _isFakeUserState.value = false
//        _isFaceCenteredState.value = false
//        capturedFaceImage = null
//        _faceDetectionMetricsState.value = null
//        _detectionStatusText.value = "Looking for face..."
//        _detectionStatusColor.value = Color.GRAY
//    }
//
//    fun updateRecognitionMetrics(metrics: RecognitionMetrics?) {
//        _recognitionMetricsState.value = metrics
//    }
//
//    fun setFakeUser(isFake: Boolean) {
//        _isFakeUserState.value = isFake
//        if (isFake) {
//            faceCenteredCounter = 0
//            _detectionStatusText.value = "⚠ Fake User Detected"
//            _detectionStatusColor.value = Color.RED
//        }
//    }
//
//    fun getFakeUser(): Boolean = _isFakeUserState.value
//
//    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage
//
//    fun setAttendanceResponse(response: AttendanceModel) {
//        _attendanceResponseState.value = response
//    }
//
//    fun setLoading(isLoading: Boolean) {
//        _isLoadingState.value = isLoading
//        if (isLoading) {
//            _detectionStatusText.value = "Processing..."
//            _detectionStatusColor.value = Color.BLUE
//        }
//    }
//
//    fun getLoading(): Boolean = _isLoadingState.value
//
//    fun setShowDialog(showDialog: Boolean) {
//        _isShowDialogState.value = showDialog
//    }
//
//    fun getNumPeople(): Long = personUseCase.getCount()
//
//    fun resetFaceCenteredState() {
//        faceCenteredCounter = 0
//        _isFaceCenteredState.value = false
//    }
//
//    fun resetState() {
//        _attendanceResponseState.value = null
//        _isLoadingState.value = false
//        _isShowDialogState.value = false
//        _isFaceRealState.value = false
//        _isFaceCenteredState.value = false
//        _faceDetectionMetricsState.value = null
//        _isFakeUserState.value = false
//        capturedFaceImage = null
//        _recognitionMetricsState.value = null
//        faceCenteredCounter = 0
//        _detectionStatusText.value = "Looking for face..."
//        _detectionStatusColor.value = Color.GRAY
//    }
//}
package com.ananta.faceapp.presentation.screens.detect_screen

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.ananta.faceapp.data.RecognitionMetrics
import com.ananta.faceapp.domain.ImageVectorUseCase
import com.ananta.faceapp.domain.PersonUseCase
import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _attendanceResponseState = MutableStateFlow<AttendanceModel?>(null)
    val attendanceResponseState: StateFlow<AttendanceModel?> = _attendanceResponseState.asStateFlow()

    private val _faceDetectionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
    val faceDetectionMetricsState: StateFlow<RecognitionMetrics?> = _faceDetectionMetricsState

    private val _isFakeUserState = MutableStateFlow(false)
    val isFakeUserState: StateFlow<Boolean> = _isFakeUserState.asStateFlow()

    private val _isLoadingState = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()

    private val _isFaceCenteredState = MutableStateFlow(false)
    val isFaceCenteredState: StateFlow<Boolean> = _isFaceCenteredState.asStateFlow()

    private val _isShowDialogState = MutableStateFlow(false)
    val isShowDialogState: StateFlow<Boolean> = _isShowDialogState.asStateFlow()

    private val _isFaceRealState = MutableStateFlow(false)
    val isFaceRealState: StateFlow<Boolean> = _isFaceRealState.asStateFlow()

    private val _detectionStatusText = MutableStateFlow("Looking for face...")
    val detectionStatusText: StateFlow<String> = _detectionStatusText.asStateFlow()

    private val _detectionStatusColor = MutableStateFlow(Color.GRAY)
    val detectionStatusColor: StateFlow<Int> = _detectionStatusColor.asStateFlow()

    private val _recognitionMetricsState = MutableStateFlow<RecognitionMetrics?>(null)
    val recognitionMetricsState: StateFlow<RecognitionMetrics?> = _recognitionMetricsState.asStateFlow()

    private var capturedFaceImage: Bitmap? = null
    private var faceCenteredCounter = 0
    private val REQUIRED_CENTERED_FRAMES = 10 // Face must be centered for 10 consecutive frames

    fun onFaceDetected(isReal: Boolean, bitmap: Bitmap?, faceDetectionMetrics: RecognitionMetrics?, isCentered: Boolean, distanceMessage: String = "") {
        _isFaceRealState.value = isReal

        if (isReal) {
            if (isCentered) {
                faceCenteredCounter++

                if (faceCenteredCounter >= REQUIRED_CENTERED_FRAMES) {
                    // Face is properly centered for required duration
                    _isFaceCenteredState.value = true
                    capturedFaceImage = bitmap
                    _faceDetectionMetricsState.value = faceDetectionMetrics
                    _isFakeUserState.value = false
                    _detectionStatusText.value = "✓ Perfect - Capturing..."
                    _detectionStatusColor.value = Color.GREEN
                } else {
                    // Still waiting for face to stay centered
                    _detectionStatusText.value = "Hold Steady... (${faceCenteredCounter}/${REQUIRED_CENTERED_FRAMES})"
                    _detectionStatusColor.value = Color.rgb(50, 205, 50) // Lime green
                }
            } else {
                // Face detected but not in optimal position/distance
                faceCenteredCounter = 0
                _isFaceCenteredState.value = false
                capturedFaceImage = null
                _detectionStatusText.value = distanceMessage.ifEmpty { "Adjust Position" }
                _detectionStatusColor.value = when {
                    distanceMessage.contains("Too Far") -> Color.rgb(255, 140, 0) // Dark orange
                    distanceMessage.contains("Too Close") -> Color.rgb(255, 69, 0) // Red-orange
                    distanceMessage.contains("Slightly") -> Color.YELLOW
                    else -> Color.rgb(255, 165, 0) // Orange
                }
            }
        } else {
            // Fake face detected
            faceCenteredCounter = 0
            _isFaceCenteredState.value = false
            capturedFaceImage = null
            _faceDetectionMetricsState.value = null
            _isFakeUserState.value = true
            _detectionStatusText.value = "⚠ Spoof Face Detected"
            _detectionStatusColor.value = Color.RED
        }
    }

    fun onNoFaceDetected() {
        faceCenteredCounter = 0
        _isFaceRealState.value = false
        _isFakeUserState.value = false
        _isFaceCenteredState.value = false
        capturedFaceImage = null
        _faceDetectionMetricsState.value = null
        _detectionStatusText.value = "Looking for face..."
        _detectionStatusColor.value = Color.GRAY
    }

    fun updateRecognitionMetrics(metrics: RecognitionMetrics?) {
        _recognitionMetricsState.value = metrics
    }

    fun setFakeUser(isFake: Boolean) {
        _isFakeUserState.value = isFake
        if (isFake) {
            faceCenteredCounter = 0
            _detectionStatusText.value = "⚠ Fake User Detected"
            _detectionStatusColor.value = Color.RED
        }
    }

    fun getFakeUser(): Boolean = _isFakeUserState.value

    fun getCapturedFaceImage(): Bitmap? = capturedFaceImage

    fun setAttendanceResponse(response: AttendanceModel) {
        _attendanceResponseState.value = response
    }

    fun setLoading(isLoading: Boolean) {
        _isLoadingState.value = isLoading
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

    fun resetFaceCenteredState() {
        faceCenteredCounter = 0
        _isFaceCenteredState.value = false
    }

    fun resetState() {
        _attendanceResponseState.value = null
        _isLoadingState.value = false
        _isShowDialogState.value = false
        _isFaceRealState.value = false
        _isFaceCenteredState.value = false
        _faceDetectionMetricsState.value = null
        _isFakeUserState.value = false
        capturedFaceImage = null
        _recognitionMetricsState.value = null
        faceCenteredCounter = 0
        _detectionStatusText.value = "Looking for face..."
        _detectionStatusColor.value = Color.GRAY
    }
}