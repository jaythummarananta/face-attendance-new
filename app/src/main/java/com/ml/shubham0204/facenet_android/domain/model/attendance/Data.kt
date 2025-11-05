package com.ml.shubham0204.facenet_android.domain.model.attendance

data class Data(
    val companyId: String,
    val faceMatch: FaceMatch,
    val isCheckIn: Boolean,
    val matched: Boolean,
    val userDetails: UserDetails
)