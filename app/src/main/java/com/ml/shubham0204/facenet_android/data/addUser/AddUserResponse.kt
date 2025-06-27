package com.ml.shubham0204.facenet_android.data.addUser
data class AddUserResponse(
    val success: Boolean,
    val error: AddUserError? = null
)

data class AddUserError(
    val message: String? = null
)