package com.ananta.faceapp.data.attendance

data class Data(
    val companyId: String,
    val faceMatch: FaceMatch,
    val isCheckIn: Boolean,
    val matched: Boolean,
    val userDetails: UserDetails
)