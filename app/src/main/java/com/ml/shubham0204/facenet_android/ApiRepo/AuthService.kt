package com.ml.shubham0204.facenet_android.ApiRepo

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {
    @POST("auth.adminSignInWithEmailAndPassword")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("api/admin-login") // Relative to BG_LOGIN_URL
    suspend fun bgLogin(@Body request: BgLoginRequest): BgLoginResponse

    @Multipart
    @POST("admin.userCheckInOrCheckOutWithFace")
    suspend fun attendancePick(@Part file: MultipartBody.Part): UserFaceAuthModel
}