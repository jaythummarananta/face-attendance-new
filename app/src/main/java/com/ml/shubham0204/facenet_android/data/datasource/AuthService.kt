package com.ananta.faceapp.ApiRepo

import com.google.gson.JsonObject
import com.ananta.faceapp.data.addUser.AddUserModel
import com.ananta.faceapp.data.attendance.AttendanceModel
import com.ananta.faceapp.data.attendance.BackendModel
import com.ananta.faceapp.data.employeeModel.EmployeeModel
import com.ananta.faceapp.data.loginModel.LoginModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {
    @POST("auth.adminSignInWithEmailAndPassword")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("api/admin-login") // Relative to BG_LOGIN_URL
    suspend fun bgLogin(@Body request: JsonObject): Response<LoginModel>

    @Multipart
    @POST("admin.userCheckInOrCheckOutWithFace")
    suspend fun attendancePick(
        @Part file: MultipartBody.Part,
        @Part("companyId") companyId: RequestBody
    ): AttendanceModel

    @GET("admin.listAllUserAccount")
    suspend fun getAllUserAccounts(
        @Query("companyId") companyId: String

    ): Response<EmployeeModel>

    @GET("api/attendance-end-point/{companyId}")
    suspend fun getAttendance(
        @Path("companyId") companyId: String
    ): Response<AttendanceModel>
   @POST("api/face-attendance")
    suspend fun doAddAttendance(
       @Body bio_id: DoAddAttendance
    ): Response<BackendModel>

    @Multipart
    @POST("admin.addUserWithFaceImages")
    suspend fun addUser(
        @Part("firstName") firstName: RequestBody,
        @Part("bioId") bioIdPart: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("companyId") companyId: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("bloodGroup") bloodGroup: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part("department") department: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<AddUserModel>
}