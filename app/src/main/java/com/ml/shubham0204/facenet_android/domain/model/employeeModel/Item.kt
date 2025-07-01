package com.ananta.faceapp.data.employeeModel

data class Item(
    val bloodGroup: String,
    val createdAt: Long,
    val department: String,
    val designation: String,
    val dob: String,
    val email: String,
    val faces: List<Face>,
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val publicId: String
)