package com.mio.applicationwork.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 网络客户端单例
 */
object RetrofitClient {

    private const val TAG = "RetrofitClient"

    private const val BASE_URL = "http://10.62.174.190:8080/"

    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
        if (newToken != null) {
            Log.i(TAG, "Token 已设置: ${newToken.take(8)}...") // 只打印前8位防泄漏
            Log.d(TAG, "Token 完整值: $newToken")              // debug 时看完整值
        } else {
            Log.i(TAG, "Token 已清除（退出登录）")
        }
    }

    /**
     * 认证拦截器——每次请求前自动附加 Authorization 头
     */
    private val authInterceptor = Interceptor { chain ->
        val originalUrl = chain.request().url.toString()
        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("Authorization", "Bearer $it")
            Log.d(TAG, "→ 请求 $originalUrl [已携带Token]")
        } ?: Log.d(TAG, "→ 请求 $originalUrl [未携带Token]")
        chain.proceed(request.build())
    }

    /**
     * HTTP 日志拦截器——在 Logcat 中打印完整的请求/响应
     * 过滤 TAG: OkHttp
     */
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Log.i(TAG, "Retrofit 初始化, BASE_URL = $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
