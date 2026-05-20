package com.mio.applicationwork.data.repository

import android.util.Log
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.model.*

class ReservationRepository {

    private val api = RetrofitClient.apiService
    private val TAG = "ReservationRepo"

    suspend fun createYuYue(userId: Int, diDian: String, createTime: String): Result<Int> {
        Log.i(TAG, "══════ 创建预约 ══════")
        Log.i(TAG, "盲人ID=$userId, 地点=$diDian, 时间=$createTime")

        return try {
            val request = CreateYuYueRequest(userId, diDian, createTime)
            val response = api.mangRenCreateYuYue(request)
            val body = response.body()
            Log.d(TAG, "HTTP ${response.code()}, body=$body")

            if (response.isSuccessful && body?.code == 200) {
                val yuYueId = body.data ?: -1
                if (yuYueId < 0) {
                    Log.e(TAG, "❌ 创建预约失败 —— yuYueId=$yuYueId (负数)")
                    Result.failure(Exception("创建预约失败 (yuYueId=$yuYueId)"))
                } else {
                    Log.i(TAG, "✅ 创建预约成功 —— yuYueId=$yuYueId")
                    Result.success(yuYueId)
                }
            } else {
                Log.e(TAG, "❌ 创建预约失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "创建预约失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 创建预约异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMangRenYuYue(userId: Int): Result<List<YuYueItem>> {
        Log.i(TAG, "盲人获取预约列表 —— userId=$userId")
        return try {
            val response = api.mangRenGetYuYue(userId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                val list = body.data ?: emptyList()
                Log.i(TAG, "✅ 获取盲人预约列表成功 —— 共 ${list.size} 条")
                Result.success(list)
            } else {
                Log.e(TAG, "❌ 获取盲人预约列表失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "获取预约列表失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取盲人预约列表异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getZhiYuanYuYue(userId: Int): Result<List<YuYueItem>> {
        Log.i(TAG, "志愿者获取预约列表 —— userId=$userId")
        return try {
            val response = api.zhiYuanGetYuYue(userId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                val list = body.data ?: emptyList()
                Log.i(TAG, "✅ 获取志愿者预约列表成功 —— 共 ${list.size} 条")
                Result.success(list)
            } else {
                Log.e(TAG, "❌ 获取志愿者预约列表失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "获取预约列表失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取志愿者预约列表异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getAllYuYue(): Result<List<YuYueItem>> {
        Log.i(TAG, "志愿者获取所有盲人预约请求")
        return try {
            val response = api.zhiYuanGetAllYuYue()
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                val list = body.data ?: emptyList()
                Log.i(TAG, "✅ 获取所有预约成功 —— 共 ${list.size} 条")
                Result.success(list)
            } else {
                Log.e(TAG, "❌ 获取所有预约失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "获取预约列表失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取所有预约异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun mangRenDelYuYue(yuYueId: Int): Result<Boolean> {
        Log.i(TAG, "盲人取消预约 —— yuYueId=$yuYueId")
        return try {
            val response = api.mangRenDelYuYue(yuYueId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 取消预约成功")
                Result.success(body.data ?: false)
            } else {
                Log.e(TAG, "❌ 取消预约失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "取消预约失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 取消预约异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun zhiYuanSelectYuYue(volunteerId: Int, yuYueId: Int): Result<Boolean> {
        Log.i(TAG, "志愿者接单 —— volunteerId=$volunteerId, yuYueId=$yuYueId")
        return try {
            val request = YuYueSelectRequest(volunteerId, yuYueId)
            val response = api.zhiYuanYuYue(request)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 接单成功")
                Result.success(body.data ?: false)
            } else {
                Log.e(TAG, "❌ 接单失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "预约接单失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 接单异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun zhiYuanDelYuYue(yuYueId: Int): Result<Boolean> {
        Log.i(TAG, "志愿者取消预约 —— yuYueId=$yuYueId")
        return try {
            val response = api.zhiYuanDelYuYue(yuYueId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 取消预约成功")
                Result.success(body.data ?: false)
            } else {
                Log.e(TAG, "❌ 取消预约失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "取消预约失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 取消预约异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun pingJiaZhiYuan(zhiYuanId: Int, pingFen: Int): Result<Boolean> {
        Log.i(TAG, "评价志愿者 —— zhiYuanId=$zhiYuanId, pingFen=$pingFen")
        return try {
            val request = PingJiaRequest(zhiYuanId, pingFen)
            val response = api.mangRenPingJia(request)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 评价成功")
                Result.success(body.data ?: false)
            } else {
                Log.e(TAG, "❌ 评价失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "评价失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 评价异常: ${e.message}", e)
            Result.failure(e)
        }
    }
}
