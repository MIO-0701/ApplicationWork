package com.mio.applicationwork.data.repository

import android.util.Log
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.model.PiPeiRequest
import com.mio.applicationwork.data.model.PiPeiResponse

class MatchRepository {

    private val api = RetrofitClient.apiService
    private val TAG = "MatchRepo"

    suspend fun piPei(userType: Int, userId: Int, diDian: String): Result<PiPeiResponse?> {
        Log.i(TAG, "发起匹配 —— userType=$userType, userId=$userId, 地点=$diDian")
        return try {
            val request = PiPeiRequest(userType, userId, diDian)
            val response = api.piPei(request)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                val data = body.data
                if (data?.data != null) {
                    Log.i(TAG, "✅ 匹配成功 —— 对方userId=${data.userId}, usertype=${data.usertype}")
                } else {
                    Log.d(TAG, "匹配中...（data为null）")
                }
                Result.success(data)
            } else {
                Log.e(TAG, "❌ 匹配请求失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "匹配请求失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 匹配请求异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun delPiPei(userType: Int, userId: Int, diDian: String): Result<Boolean> {
        Log.i(TAG, "取消匹配 —— userType=$userType, userId=$userId, 地点=$diDian")
        return try {
            val request = PiPeiRequest(userType, userId, diDian)
            val response = api.delPiPei(request)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200 && body?.data == true) {
                Log.i(TAG, "✅ 取消匹配成功")
                Result.success(true)
            } else {
                Log.e(TAG, "❌ 取消匹配失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "取消匹配失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 取消匹配异常: ${e.message}", e)
            Result.failure(e)
        }
    }
}
