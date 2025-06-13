package com.ml.shubham0204.facenet_android.ApiRepo


import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AuthApi private constructor(private val context: Context) {
    companion object {
        private var instance: AuthApi? = null

        fun getInstance(context: Context): AuthApi {
            if (instance == null) {
                instance = AuthApi(context)
            }
            return instance!!
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val authService: AuthService = RetrofitClient.createAuthService(context)
    private val bgLoginService: AuthService = RetrofitClient.createBgLoginService()

    suspend fun login(email: String, password: String): UserResponse {
        val request = LoginRequest(email, password)
        val response = authService.login(request)
        Log.d("AuthApi", "login response: $response")
        if (response.success) {
            // Save token if present
            response.data?.accessToken?.let { token ->
                sharedPreferences.edit().putString("token", token).apply()
            }
            return response
        } else {
            throw Exception(response.error?.message ?: "Login failed")
        }
    }

    suspend fun bgLogin(email: String = "DTHJ9567", password: String = "9988"): BgLoginResponse {
        val request = BgLoginRequest(email, password)
        val response = bgLoginService.bgLogin(request)
        Log.d("AuthApi", "bgLogin response: ${response.data}")
        if (response.data != null) {
            return response
        } else {
            throw Exception("Background login failed")
        }
    }

    private fun createImageMultipart(file: File, paramName: String): MultipartBody.Part {
        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }

    suspend fun attendancePick(image: File): UserFaceAuthModel {
        val formData = createImageMultipart(image, "file")
        val response = authService.attendancePick(formData)
        Log.d("AuthApi", "attendancePick response: ${response}")
        if (response.success == true) {
            return response
        } else {
            throw Exception("Attendance pick failed")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun <T> withRetry(action: suspend () -> T): T {
        var attempt = 0
        val maxRetries = 3
        while (attempt < maxRetries) {
            if (isNetworkAvailable()) {
                try {
                    return action()
                } catch (e: Exception) {
                    if (attempt == maxRetries - 1) throw e
                }
            }
            attempt++
            delay(1000L * attempt)
        }
        throw Exception("Network unavailable after $maxRetries retries")
    }
}

suspend fun showMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, message, duration).show()
}

class HttpResponseException(message: String) : Exception(message)

data class LoginRequest(val email: String, val password: String)

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




data class UserFaceAuthModel(
    val success: Boolean?,
    val data: Data?
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
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.formData
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import java.io.File
//
//class AuthApi(private val client: HttpClient) {
//
//    companion object {
//        val client = HttpClient {
//            install(ContentNegotiation) {
//                json(Json { ignoreUnknownKeys = true })
//            }
//        }
//    }
//
//    suspend fun login(email: String, password: String): UserResponse {
//        val response: UserResponse = client.post("https://imanageapi.intenics.in/api/auth.adminSignInWithEmailAndPassword") {
//            contentType(ContentType.Application.Json)
//            setBody(LoginRequest(email, password))
//        }.body()
//        if (response.success) {
//            return response
//        } else {
//            throw Exception(response.error?.message ?: "Login failed")
//        }
//    }
//
//    suspend fun bgLogin(email: String = "DTHJ9567", password: String = "9988"): BgLoginResponse {
//        val response: BgLoginResponse = client.post("https://imanageapi.intenics.in/api/admin-login") {
//            contentType(ContentType.Application.Json)
//            setBody(BgLoginRequest(email, password))
//        }.body()
//        if (response.data != null) {
//            return response
//        } else {
//            throw Exception("Background login failed")
//        }
//    }
//
//    suspend fun attendancePick(image: File): UserFaceAuthModel {
//        val formData = formData {
//            append("file", image.readBytes(), Headers.build {
//                append(HttpHeaders.ContentType, "image/jpeg")
//                append(HttpHeaders.ContentDisposition, "filename=${image.name}")
//            })
//        }
//        val response: UserFaceAuthModel = client.post("https://imanageapi.intenics.in/api/admin.userCheckInOrCheckOutWithFace") {
//            contentType(ContentType.MultiPart.FormData)
//            setBody(formData)
//        }.body()
//        if (response.success) {
//            return response
//        } else {
//            throw Exception(response.error?.message ?: "Attendance pick failed")
//        }
//    }
//}
//
//@Serializable
//data class LoginRequest(val email: String, val password: String)
//
//@Serializable
//data class UserResponse(
//    val success: Boolean,
//    val data: UserData? = null,
//    val error: ErrorResponse? = null
//)
//
//@Serializable
//data class UserData(
//    val accessToken: String? = null,
//    val email: String? = null
//)
//
//@Serializable
//data class ErrorResponse(val message: String? = null)
//
//@Serializable
//data class BgLoginRequest(val email: String, val password: String)
//
//@Serializable
//data class BgLoginResponse(val data: BgLoginData? = null)
//
//@Serializable
//data class BgLoginData(val success: Boolean = false)
//
//@Serializable
//data class UserFaceAuthModel(
//    val success: Boolean = false,
//    val error: ErrorResponse? = null
//)
//
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.formData
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.serialization.Serializable
//import java.io.File
//
//class AuthApi(private val client: HttpClient) {
//
//    suspend fun attendancePick(image: File): UserFaceAuthModel {
//        val formData = formData {
//            append("file", image.readBytes(), Headers.build {
//                append(HttpHeaders.ContentType, "image/jpeg")
//                append(HttpHeaders.ContentDisposition, "filename=${image.name}")
//            })
//        }
//        val response: UserFaceAuthModel = client.post("https://your-api.com/admin.userCheckInOrCheckOutWithFace") {
//            contentType(ContentType.MultiPart.FormData)
//            setBody(formData)
//        }.body()
//        if (response.success) {
//            return response
//        } else {
//            throw Exception(response.error?.message ?: "Attendance pick failed")
//        }
//    }
//
//    companion object {
//        val client = HttpClient {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//    }
//}
//
//
//data class UserFaceAuthModel(
//    val success: Boolean = false,
//    val error: ErrorResponse? = null
//)
//
//
//data class ErrorResponse(val message: String? = null)