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
import com.ananta.faceapp.data.attendance.AttendanceModel
import com.ananta.faceapp.data.attendance.BackendModel
import com.ananta.faceapp.data.employeeModel.EmployeeModel
import com.ananta.faceapp.data.loginModel.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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
            Toast.makeText(context, "Login error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    suspend fun bgLogin(email: String, password: String): Response<LoginModel>? {
        try {
//            val request = BgLoginRequest(email, password)
//            // Example login request
//            val loginRequest = BgLoginRequest(
//                email = email,
//                password = password
//            )

            val param = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            Log.d("AuthApi", "bgLogin request: $param")
            val response = bgLoginService.bgLogin(param)
            Log.d("AuthApi", "bgLogin response: ${response}")
            if (response.isSuccessful != null) {

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
                    sharedPreferences.getString("company_id", "663b510d3506f4bd299f6dd8")


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
                Toast.makeText(
                    context,
                    "Error fetching user accounts: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
    suspend fun doAddAttendance(bio_id :String): BackendModel? {
        return withRetry {
            try {
                val request = DoAddAttendance(bio_id)

                val response = bgLoginService.doAddAttendance(request)
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

                Log.d("AuthApi", "getAttendance response: $e")

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

    private fun createImageMultipart(file: File, paramName: String): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }

    suspend fun attendancePick(image: File): AttendanceModel? {
        try {
            val companyId = sharedPreferences.getString("company_id", "663b510d3506f4bd299f6dd8")?:""
            val companyIdPart = companyId.toRequestBody("text/plain".toMediaTypeOrNull())

            val formData = createImageMultipart(image, "file")
            val response = authService.attendancePick(formData, companyIdPart)
            Log.d("AuthApi", "attendancePick response: ${response}")
            if (response.success == true) {
                return response
            } else {
                Toast.makeText(context, "Attendance pick failed", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {

            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private val _errorData = MutableLiveData<String>()
    val errorData: LiveData<String> = _errorData
    suspend fun addUser(
        firstName: String,
        lastName: String,
        email: String,
        bioId: String,
        mobile: String,
        dob: String,
        bloodGroup: String,
        designation: String,
        department: String,
        imagePaths: List<String?>
    ): Boolean {
        // Validate inputs
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || mobile.isBlank() ||
            dob.isBlank() || bloodGroup.isBlank() || designation.isBlank() || department.isBlank()
        ) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        if (imagePaths.filterNotNull().isEmpty()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "At least one image is required", Toast.LENGTH_SHORT).show()
            }
            return false
        }
        val companyId = sharedPreferences.getString("company_id", "663b510d3506f4bd299f6dd8")
            ?: "663b510d3506f4bd299f6dd8"
        if (companyId.isBlank()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Company ID is required", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        return withRetry {
            try {
                // Create form data parts
                val firstNamePart = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
                val lastNamePart = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
                val bioIdPart = bioId.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val mobilePart = "+91$mobile".toRequestBody("text/plain".toMediaTypeOrNull())
                val dobPart = dob.toRequestBody("text/plain".toMediaTypeOrNull())
                val bloodGroupPart = bloodGroup.toRequestBody("text/plain".toMediaTypeOrNull())
                val designationPart = designation.toRequestBody("text/plain".toMediaTypeOrNull())
                val departmentPart = department.toRequestBody("text/plain".toMediaTypeOrNull())
                val companyIdPart = companyId.toRequestBody("text/plain".toMediaTypeOrNull())

                // Create image parts
                val imageParts = imagePaths.filter { it?.isNotEmpty() ?: false }.map { path ->
                    createImageMultipart(File(path!!), "file")
                }
                val response = authService.addUser(
                    firstName = firstNamePart,
                    lastName = lastNamePart,
                    bioIdPart = bioIdPart,
                    email = emailPart,
                    mobile = mobilePart,
                    dob = dobPart,
                    bloodGroup = bloodGroupPart,
                    designation = designationPart,
                    department = departmentPart,
                    companyId = companyIdPart,
                    files = imageParts
                )
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.d(
                    "AuthApi",
                    "addUser response: $response, body: ${response.body()}, errorBody: ${
                        response.errorBody()?.string()
                    }"
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    true
                } else {
                    val errorMessage = try {
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        when (response.code()) {
                            400 -> "${error.message}"
                            500 -> "Server error: ${error.message}"
                            else -> "Error ${response.code()}: ${error.message}"
                        }
                    } catch (e: Exception) {
                        "Error ${response.code()}: $errorBody"
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    _errorData.postValue(errorMessage)
                    false
                }
//                if (response.isSuccessful && response.body()?.success == true) {
//                    true
//                } else {
//                    if(response.code()== 400){
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(context, "User Already Exist", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                    else if(response.code()== 500){
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show()
//                        }
//                    }
////                    val errorMessage = response.errorBody().toString()
////
////                    Log.d("errorMessage","$errorMessage")
//////                        response.body()?.success.toString()
//////                        ?: response.errorBody()?.string() ?: "Failed to register user"
////                    withContext(Dispatchers.Main) {
////                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
////                    }
//                    false
//                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = when (e) {
                        is HttpException -> {
                            val code = e.code()
                            try {
                                val errorBody = e.response()?.errorBody()?.string()
                                Log.e("AuthApi", "Server error: $errorBody")

                                // Parse message from the JSON response
                                val json = JSONObject(errorBody ?: "")
                                val message = json.optString("message", "Something went wrong")

                                if (code == 400) {
                                    // Custom handling for known error
                                    "User already exists: $message"
                                } else {
                                    "Error $code: $message"
                                }
                            } catch (ex: Exception) {
                                "HTTP $code: Failed to parse error body"
                            }
                        }

                        else -> {
                            Log.e("AuthApi", "Unexpected error", e)
                            e.message ?: "Unknown error"
                        }
                    }

                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
                Log.e("AuthApi", "Error registering user", e)
                false
            }
        } ?: false
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

suspend fun showMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, message, duration).show()
}

class HttpResponseException(message: String) : Exception(message)

data class LoginRequest(val email: String, val password: String)

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

data class BgLoginRequest(val email: String, val password: String)

data class BgLoginResponse(val data: BgLoginData? = null)

data class BgLoginData(val success: Boolean = false)


//data class UserFaceAuthModel(
//    val success: Boolean?,
//    val data: Data?
//)

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