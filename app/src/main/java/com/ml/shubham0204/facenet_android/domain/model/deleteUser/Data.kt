package com.ml.shubham0204.facenet_android.domain.model.deleteUser

data class Data(
    val deletedFiles: List<String>,
    val deletedUser: DeletedUser,
    val failedFiles: List<Any>
)