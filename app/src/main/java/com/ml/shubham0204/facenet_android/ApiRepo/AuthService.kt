package com.ml.shubham0204.facenet_android.ApiRepo

import com.google.gson.JsonObject
import com.ml.shubham0204.facenet_android.data.addUser.AddUserResponse
import com.ml.shubham0204.facenet_android.data.employeeModel.EmployeeModel
import com.ml.shubham0204.facenet_android.data.loginModel.LoginModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {
    @POST("auth.adminSignInWithEmailAndPassword")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("api/admin-login") // Relative to BG_LOGIN_URL
    suspend fun bgLogin(@Body request: JsonObject): Response<LoginModel>

    @Multipart
    @POST("admin.userCheckInOrCheckOutWithFace")
    suspend fun attendancePick(@Part file: MultipartBody.Part): UserFaceAuthModel

    @GET("admin.listAllUserAccount")
    suspend fun getAllUserAccounts(): Response<EmployeeModel>

    @Multipart
    @POST("admin.addUserWithFaceImages")
    suspend fun addUser(
        @Part("firstName") firstName: MultipartBody.Part,
        @Part("lastName") lastName: MultipartBody.Part,
        @Part("email") email: MultipartBody.Part,
        @Part("mobile") mobile: MultipartBody.Part,
        @Part("dob") dob: MultipartBody.Part,
        @Part("bloodGroup") bloodGroup: MultipartBody.Part,
        @Part("designation") designation: MultipartBody.Part,
        @Part("department") department: MultipartBody.Part,
        @Part files: List<MultipartBody.Part>
    ): Response<AddUserResponse>
}