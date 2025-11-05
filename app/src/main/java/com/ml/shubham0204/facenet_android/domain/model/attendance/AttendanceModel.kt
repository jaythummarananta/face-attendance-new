//package com.ml.shubham0204.facenet_android.domain.model.attendance
//
//data class AttendanceModel(
//    val data: Data,
//    val success: Boolean
//)
package com.ml.shubham0204.facenet_android.domain.model.attendance

data class AttendanceModel(
    val data: Data?,       // Data will be null in case of error
    val success: Boolean,
    val error: Error?,   // Add error object
    val requestId: String?
)

data class Error(
    val code: String?,
    val message: String?
)
