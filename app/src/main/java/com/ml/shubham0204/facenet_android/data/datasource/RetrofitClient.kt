package com.ananta.faceapp.ApiRepo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.GsonBuilder
import com.ananta.faceapp.ApiRepo.AuthService
import com.ananta.faceapp.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // live
    private const val BASE_URL = "http://139.59.69.40:1500/v1/"

    // local
//    private const val BASE_URL = "http://192.168.1.128:1500/v1/"
//    private const val BASE_URL = "https://face-attendv2.anantalabs.in/v1/"


    private const val BG_LOGIN_URL = "https://imanageapi.intenics.in/"

//    public  val BASE_URL1 = "https://face-attendv2.anantalabs.in"
    public  val BASE_URL1 = "http://139.59.69.40:1500/public/"

    fun createAuthService(context: Context): AuthService {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(sharedPreferences))
//            .addInterceptor(ResponseValidationInterceptor(context, sharedPreferences))
//            .build()
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // server connect timeout
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)    // response read timeout
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)   // request write timeout
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .addInterceptor(ResponseValidationInterceptor(context, sharedPreferences))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)

            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        return retrofit.create(AuthService::class.java)
    }

    fun createBgLoginService(): AuthService {
        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BG_LOGIN_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        return retrofit.create(AuthService::class.java)
    }

    private class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Accept", "application/json")


            Log.d("AuthInterceptor", "Token: ${sharedPreferences.getString("token", null)}")
            sharedPreferences.getString("token", null)?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private class ResponseValidationInterceptor(
        private val context: Context,
        private val sharedPreferences: SharedPreferences
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            when (response.code) {
                401,403 -> { // Handle unauthorized or forbidden responses
                    with(sharedPreferences.edit()) {
                        clear() // Clear all SharedPreferences data
                        apply()
                    }
                    restartApp()
                }
                500 -> {
                    // No action needed, just return response
                }
            }
            return response
        }

        private fun restartApp() {
            val intent =
                Intent(context, MainActivity::class.java) // Replace with your launcher activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
//            (context as? Activity)?.finishAffinity() // Gracefully close all activities
        }
    }
}