package com.ananta.faceapp.ApiRepo


import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.ananta.faceapp.data.attendance.BackendModel
import com.ananta.faceapp.data.employeeModel.EmployeeModel
import com.ml.shubham0204.facenet_android.domain.model.attendance.AttendanceModel
import com.ml.shubham0204.facenet_android.domain.model.deleteUser.DeleteUserModel
import com.ml.shubham0204.facenet_android.domain.model.loginModel.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.File

class AuthApi private constructor(private val context: Context) {
    companion object {
        private var instance: AuthApi? = null

        var spoofAttempts: Int by mutableStateOf(0)
        fun getInstance(context: Context): AuthApi {
            if (instance == null) {
                instance = AuthApi(context)
            }
            return instance!!
        }
    }

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val authService: AuthService = RetrofitClient.createAuthService(context)
    private val bgLoginService: AuthService = RetrofitClient.createBgLoginService()

    suspend fun login(
        email: String = "admin@admin.com",
        password: String = "admin@123456"
    ): UserResponse? {
        try {
            val request = LoginRequest(email, password)
            val response = authService.login(request)

            Log.d("AuthApi", "login response: $response")
            if (response.success) {
                response.data?.accessToken?.let { token ->
                    sharedPreferences.edit().putString("token", token).apply()
                }
                return response
            } else {
                Toast.makeText(
                    context,
                    response.error?.message ?: "Login failed",
                    Toast.LENGTH_SHORT
                ).show()
                return null
            }
        } catch (e: Exception) {

            Log.d("AuthApi", "login response: $e")
            Toast.makeText(context, "Login error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    suspend fun bgLogin(email: String, password: String): Response<LoginModel>? {
        try {
            val param = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            Log.d("AuthApi", "bgLogin request: $param")
            val response = bgLoginService.bgLogin(param)
            Log.d("AuthApi", "bgLogin response: ${response}")
            if (response.isSuccessful != null) {
                response.body()?.data?.company_name?.let { companyName ->
                    sharedPreferences.edit().putString("company_name", companyName).apply()
                }
                return response
            } else {
                Toast.makeText(context, "Background login failed", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Background login error: ${e.message}", Toast.LENGTH_SHORT)
                .show()
            return null
        }
    }


    suspend fun getAllUserAccounts(): EmployeeModel? {
        return withRetry {
            try {
                val companyId =
                    sharedPreferences.getString("company_id", "")

                Log.d("", "company_id :: ${companyId}")
                val response = companyId?.let { authService.getAllUserAccounts(companyId = it) }
                Log.d("AuthApi", "getAllUserAccounts response: $response")
                if (response!!.isSuccessful) {
                    response.body()
                } else {
                    Toast.makeText(context, "Failed to fetch user accounts", Toast.LENGTH_SHORT)
                        .show()
                    null
                }
            } catch (e: Exception) {
//                Toast.makeText(
//                    context,
//                    "Error fetching user accounts: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
                null
            }
        }
    }

    suspend fun getAttendance(): AttendanceModel? {
        return withRetry {
            try {
                val companyId = sharedPreferences.getString("company_id", "")
                val response = bgLoginService.getAttendance(companyId ?: "null")
                Log.d("AuthApi", "getAttendance response: $response")
                if (response.isSuccessful) {
                    response.body()
                } else {
                    val errorMessage =
                        response.errorBody()?.string() ?: "Failed to fetch attendance"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error fetching attendance: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("AuthApi", "Error fetching attendance", e)
                null
            }
        }
    }

//    suspend fun doAddAttendance(bio_id: String): BackendModel? {
//        return withRetry {
//            try {
//                val request = DoAddAttendance(bio_id)
//
//                val response = bgLoginService.doAddAttendance(request)
//                Log.d("AuthApi", "getAttendance response: $response")
//                if (response.isSuccessful) {
//                    response.body()
//                } else {
//                    val errorMessage =
//                        response.errorBody()?.string() ?: "Failed to fetch attendance"
//                    Log.e("AuthApi", "Error fetching attendance ${ response.errorBody()}", )
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
//                    }
//                    null
//                }
//            } catch (e: Exception) {
//
//                Log.d("AuthApi", "getAttendance response: $e")
//
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        context,
//                        "Error fetching attendance: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                Log.e("AuthApi", "Error fetching attendance", e)
//                null
//            }
//        }
//    }

//    suspend fun doAddAttendance(bio_id: String): BackendModel? {
//        return withRetry {
//            try {
//                val request = DoAddAttendance(bio_id)
//                val response = bgLoginService.doAddAttendance(request)
//                Log.d("AuthApi", "getAttendance response: $response")
//
//                if (response.isSuccessful) {
//                    response.body()
//                } else {
//                    // Parse error JSON properly
//                    val errorBody = response.errorBody()?.string()
//                    var errorMessage = "Failed to fetch attendance"
//
//                    errorBody?.let {
//                        try {
//                            val json = JSONObject(it)
//                            errorMessage = json.optString("message", errorMessage)
//                        } catch (ex: Exception) {
//                            Log.e("AuthApi", "Error parsing error body: ${ex.message}")
//                        }
//                    }
//
//                    Log.e("AuthApi", "Error fetching attendance: $errorMessage")
//
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
//                    }
//                    null
//                }
//            } catch (e: Exception) {
//                Log.e("AuthApi", "Exception fetching attendance", e)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        context,
//                        "Error fetching attendance: ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                null
//            }
//        }
//    }

    suspend fun doAddAttendance(bio_id: String): ApiResult<BackendModel> {
        return withRetry {
            try {
                val request = DoAddAttendance(bio_id)
                val response = bgLoginService.doAddAttendance(request)
                Log.d("AuthApi", "getAttendance response: $response")

                if (response.isSuccessful) {
                    ApiResult(data = response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    var errorMessage = "Unable to record attendance. Please try again later."

                    errorBody?.let {
                        try {
                            val json = JSONObject(it)
                            errorMessage = json.optString("message", errorMessage)
                        } catch (ex: Exception) {
                            Log.e("AuthApi", "Error parsing error body: ${ex.message}")
                        }
                    }

                    Log.e("AuthApi", "Error fetching attendance: $errorMessage")
                    ApiResult(errorMessage = errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthApi", "Exception fetching attendance", e)
                ApiResult(errorMessage = "Error fetching attendance: ${e.message}")
            }
        }!!
    }

    suspend fun doDeleteUser(publicId: String): Boolean? {
        return withRetry {
            try {
                val response = authService.deleteUser(publicId)
                Log.d("AuthApi", "delete user response: $response")
                if (response.isSuccessful) {
                    response.body()?.success
                } else {
                    false
                }
            } catch (e: Exception) {

                Log.d("AuthApi", "getAttendance response: $e")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        ": ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("AuthApi", "Error fetching attendance", e)
                null
            }
        }
    }


    //    suspend fun attendancePick(image: File): AttendanceModel? {
//        try {
//            val companyId = sharedPreferences.getString("company_id", "")?:""
//            Log.d("AuthApi", "companyId--${companyId}")
//
//            val formData = createImageMultipart(image, "file")
//
//
//            Log.d("AuthApi", "Attendance Request--${formData}")
//            val response = authService.attendancePick(formData, companyId.toPlainRequestBody())
//            Log.d("AuthApi", "attendancePick response: ${response}")
//            if (response.success == true) {
//                return response
//            } else {
//                return null
//            }
//        } catch (e: Exception) {
//
//            Log.d("AuthApi", "${e.message}")
//
//            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
//            return null
//        }
//    }


    suspend fun attendancePick(image: File): AttendanceModel? {
        try {
            if (!image.exists() || !image.canRead() || image.length() == 0L) {
                Log.e(
                    "AuthApi",
                    "Invalid image file: exists=${image.exists()}, readable=${image.canRead()}, size=${image.length()}"
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Invalid image file", Toast.LENGTH_SHORT).show()
                }
                return null
            }

            val companyId = sharedPreferences.getString("company_id", "") ?: ""
            if (companyId.isEmpty()) {
                Log.e("AuthApi", "companyId is empty")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Company ID not found", Toast.LENGTH_SHORT).show()
                }
                return null
            }
            Log.d("AuthApi", "companyId--$companyId")

            val formData = createImageMultipart(image, "file")
            Log.d("AuthApi", "Attendance Request--$formData")


            // Try API call with retry on 400 error
            val response = authService.attendancePick(formData, companyId.toPlainRequestBody())

            Log.d("AuthApi", "attendancePick response: $response")
            return if (response.success==true) {
                Log.d("AuthApi", "attendancePick isSuccessful: $response")

                response
            } else {

//                val errorJson = response.errorBody()?.string()
                Log.e("AuthApi", "Error BodyBodyBodyBody: ${response.error!!.message}")
                return  response
            }
//            if (response.success == true) {
//                return response
//            } else {
//                return null
//            }
        } catch (e: Exception) {
//            Log.e("AuthApi", "attendancePick error: ${e.message}", e)
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
            return null
        }
    }

    private val _errorData = MutableLiveData<String>()
    val errorData: LiveData<String> = _errorData
//    suspend fun addUser(
//        firstName: String,
//        lastName: String,
//        bioId: String,
//        department: String,
//        imagePaths: List<String?>
//    ): Boolean {
//
//        val companyId = sharedPreferences.getString("company_id", "")
//            ?: ""
//
//        return withRetry {
//            try {
//
//                // Create image parts
//                val imageParts = imagePaths.filter { it?.isNotEmpty() ?: false }.map { path ->
//                    createImageMultipart(File(path!!), "file")
//                }
//
//                Log.d("", "imagePartsimageParts ${imageParts}")
//                val response = authService.addUser(
//                    firstName = firstName.toPlainRequestBody(),
//                    lastName = lastName.toPlainRequestBody(),
//                    bioIdPart = bioId.toPlainRequestBody(),
//                    department = department.toPlainRequestBody(),
//                    companyId = companyId.toPlainRequestBody(),
//                    files = imageParts
//                )
//
//                Log.d("AuthApi", "addUser response: $response")
//                val errorBody = response.errorBody()?.string() ?: "No error body"
//                Log.d(
//                    "AuthApi",
//                    "addUser response: $response, body: ${response.body()}, errorBody: ${
//                        response.errorBody()?.string()
//                    }"
//                )
//                if (response.isSuccessful && response.body()?.success == true) {
//                    true
//                } else {
//                    val errorMessage = try {
//                        val error = Gson().fromJson(errorBody, ApiError::class.java)
//
//                        Log.d("AuthApi", "addUser error: $error")
//                        when (response.code()) {
//                            400 -> "${error.message}"
//                            500 -> "Server error: ${error.message}"
//                            else -> "Error ${response.code()}: ${error.message}"
//                        }
//                    } catch (e: Exception) {
//                        "Error ${response.code()}: $errorBody"
//                    }
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//                    }
//                    _errorData.postValue(errorMessage)
//                    false
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    val errorMessage = when (e) {
//                        is HttpException -> {
//                            val code = e.code()
//                            try {
//                                val errorBody = e.response()?.errorBody()?.string()
//                                Log.e("AuthApi", "Server error: $errorBody")
//
//                                // Parse message from the JSON response
//                                val json = JSONObject(errorBody ?: "")
//                                val message = json.optString("message", "Something went wrong")
//
//                                if (code == 400) {
//                                    // Custom handling for known error
//                                    "User already exists: $message"
//                                } else {
//                                    "Error $code: $message"
//                                }
//                            } catch (ex: Exception) {
//                                "HTTP $code: Failed to parse error body"
//                            }
//                        }
//
//                        else -> {
//                            Log.e("AuthApi", "Unexpected error", e)
//                            e.message ?: "Unknown error"
//                        }
//                    }
//
//                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
//                }
//                Log.e("AuthApi", "Error registering user", e)
//                false
//            }
//        } ?: false
//    }
data class ApiErrorResponse(
    val success: Boolean,
    val error: ApiError?,
    val requestId: String?
)

    data class ApiError(
        val code: String,
        val message: String
    )

    private fun parseApiError(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "Unknown error occurred"

        return try {
            val apiErrorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
            apiErrorResponse.error?.message ?: "Unknown error occurred"
        } catch (e: Exception) {
            Log.e("AuthApi", "Failed to parse error body: $errorBody", e)
            "Unable to parse error response"
        }
    }

    suspend fun addUser(
        firstName: String,
        lastName: String,
        bioId: String,
        department: String,
        imagePaths: List<String?>
    ): Boolean {
        val companyId = sharedPreferences.getString("company_id", "") ?: ""

        return withRetry {
            try {
                val imageParts = imagePaths.filter { it?.isNotEmpty() ?: false }.map { path ->
                    createImageMultipart(File(path!!), "file")
                }

                val response = authService.addUser(
                    firstName = firstName.toPlainRequestBody(),
                    lastName = lastName.toPlainRequestBody(),
                    bioIdPart = bioId.toPlainRequestBody(),
                    department = department.toPlainRequestBody(),
                    companyId = companyId.toPlainRequestBody(),
                    files = imageParts
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    true
                } else {
                    // ✅ Parse API error
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseApiError(errorBody)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    _errorData.postValue(errorMessage)
                    false
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        parseApiError(errorBody)
                    }
                    else -> e.message ?: "Unexpected error"
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
                _errorData.postValue(errorMessage)
                false
            }
        } ?: false
    }

    fun String.toPlainRequestBody(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun <T> withRetry(action: suspend () -> T): T? {
        var attempt = 0
        val maxRetries = 3
        while (attempt < maxRetries) {
            if (isNetworkAvailable()) {
                try {
                    return action()
                } catch (e: Exception) {
                    if (attempt == maxRetries - 1) {
                        Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                        return null
                    }
                }
            }
            attempt++
            delay(1000L * attempt)
        }
        Toast.makeText(context, "Network unavailable after $maxRetries retries", Toast.LENGTH_SHORT)
            .show()
        return null
    }
}

private fun createImageMultipart(file: File, paramName: String): MultipartBody.Part {
    val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
}

suspend fun showMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, message, duration).show()
}


data class LoginRequest(val email: String, val password: String)
data class DeleteRequest(val publicId: String)

data class DoAddAttendance(val bio_id: String)


data class UserResponse(
    val success: Boolean,
    val data: UserData? = null,
    val error: ErrorResponse? = null
)

data class UserData(
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("email") val email: String? = null
)

data class ErrorResponse(@SerializedName("message") val message: String? = null)


data class BgLoginData(val success: Boolean = false)


data class ApiError(
    val message: String // Adjust based on your API's error body structure
)

data class Data(
    val matched: Boolean?,
    val userDetails: UserDetails?,
    val isCheckIn: Boolean?
)

data class UserDetails(
    val firstName: String?,
    val lastName: String?,
    val department: String?,
    val designation: String?,
    val publicId: String?,
    val dob: String?
)

data class ApiResult<T>(
    val data: T? = null,
    val errorMessage: String? = null
)
