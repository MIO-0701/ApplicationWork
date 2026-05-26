package com.mio.applicationwork.data.repository

import android.util.Log
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.model.PiPeiRequest
import com.mio.applicationwork.data.model.PiPeiResponse

/**
 * 实时匹配数据仓库
 *
 * 匹配流程:
 * 1. 盲人/志愿者输入地点 → 调用 [piPei] 发送 POST /piPei/piPei
 * 2. 后端返回 PiPeiResponse，其中 data.data 为 null 表示暂未匹配到
 * 3. ViewModel 层每 3 秒轮询一次，直到 data.data != null 表示匹配成功
 * 4. 用户取消或退出时调用 [delPiPei] 发送 POST /piPei/delPiPei 结束匹配
 */
class MatchRepository {

    private val api = RetrofitClient.apiService
    private val TAG = "MatchRepo"

    /**
     * 发起一次匹配请求（由 ViewModel 层循环调用实现轮询）
     * @return Result.success(PiPeiResponse) — 其中 inner data 为 null 表示未匹配，非 null 表示匹配成功
     */
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

    /**
     * 取消匹配，后端从匹配池中移除此用户
     * @return Result.success(true) 表示取消成功
     */
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
