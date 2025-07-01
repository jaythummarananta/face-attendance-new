package com.ananta.faceapp.data.addUser

data class UserData(
    val bioId: String,
    val bloodGroup: String,
    val companyId: String,
    val department: String,
    val designation: String,
    val dob: String,
    val email: String,
    val faces: List<Face>,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val mobile: String,
    val publicId: String,
    val role: String
)