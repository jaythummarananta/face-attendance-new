package com.ml.shubham0204.facenet_android.ApiRepo

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://face-attedance.anantalabs.in/v1/"
    private const val BG_LOGIN_URL = "https://imanageapi.intenics.in/"

    fun createAuthService(context: Context): AuthService {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .addInterceptor(ResponseValidationInterceptor(sharedPreferences))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        return retrofit.create(AuthService::class.java)
    }

    // For bgLogin, which uses a different base URL
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

            sharedPreferences.getString("token", null)?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private class ResponseValidationInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            when (response.code) {
                403 -> {
                    with(sharedPreferences.edit()) {
                        putBoolean("is_user_login", false)
                        apply()
                    }
                    throw HttpResponseException("403 Forbidden")
                }
                500 -> {
                    throw HttpResponseException("500 Internal Server Error")
                }
            }
            return response
        }
    }
}