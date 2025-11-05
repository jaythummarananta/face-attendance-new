package com.ml.shubham0204.facenet_android.domain.model.addUser

data class UserData(
    val bioId: String,
    val companyId: String,
    val department: String,
    val faces: List<Face>,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val publicId: String,
    val role: String
)