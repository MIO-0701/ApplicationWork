package com.mio.applicationwork.data.repository

import android.util.Log
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.model.*

class UserRepository {

    private val api = RetrofitClient.apiService
    private val TAG = "UserRepo"

    suspend fun login(userType: Int, zhangHao: String, password: String): Result<LoginData> {
        val typeName = if (userType == USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.i(TAG, "══════ 登录开始 ══════")
        Log.i(TAG, "用户类型: $typeName ($userType), 账号: $zhangHao")

        return try {
            val request = LoginRequest(zhangHao, password)
            Log.d(TAG, "请求体: {\"zhangHao\":\"$zhangHao\",\"password\":\"***\"}")

            val response = if (userType == USER_TYPE_MANGREN) {
                api.mangRenLogin(request)
            } else {
                api.zhiYuanLogin(request)
            }

            val httpCode = response.code()
            val body = response.body()
            Log.d(TAG, "HTTP 状态码: $httpCode")
            Log.d(TAG, "响应体: $body")

            if (response.isSuccessful && body?.code == 200) {
                val data = body.data!!
                RetrofitClient.setToken(data.token)
                Log.i(TAG, "✅ 登录成功 —— userId=${data.id}, token=${data.token.take(8)}...")
                Result.success(data)
            } else {
                val errMsg = body?.message ?: "登录失败"
                Log.e(TAG, "❌ 登录失败 —— code=${body?.code}, message=$errMsg")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 登录异常: ${e.javaClass.simpleName} —— ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun registerMangRen(
        name: String, zhangHao: String, password: String,
        sex: Int, suDu: Double, gongLi: Double
    ): Result<Boolean> {
        Log.i(TAG, "══════ 盲人注册开始 ══════")
        Log.i(TAG, "姓名=$name, 账号=$zhangHao, 性别=${if (sex == 1) "男" else "女"}, 配速=$suDu, 公里=$gongLi")

        return try {
            val request = MangRenRegisterRequest(name, zhangHao, password, sex, suDu, gongLi)
            Log.d(TAG, "请求体: {\"name\":\"$name\",\"zhangHao\":\"$zhangHao\",\"password\":\"***\",\"sex\":$sex,\"suDu\":$suDu,\"gongLi\":$gongLi}")

            val response = api.mangRenRegister(request)
            val httpCode = response.code()
            val body = response.body()
            Log.d(TAG, "HTTP 状态码: $httpCode")
            Log.d(TAG, "响应体: $body")

            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 盲人注册成功 —— data=${body.data}")
                Result.success(body.data ?: false)
            } else {
                val errMsg = body?.message ?: "注册失败"
                Log.e(TAG, "❌ 盲人注册失败 —— code=${body?.code}, message=$errMsg")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 盲人注册异常: ${e.javaClass.simpleName} —— ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun registerZhiYuan(
        name: String, zhangHao: String, password: String, renZhen: String,
        sex: Int, suDu: Double, gongLi: Double
    ): Result<Boolean> {
        Log.i(TAG, "══════ 志愿者注册开始 ══════")
        Log.i(TAG, "姓名=$name, 账号=$zhangHao, 认证=$renZhen, 性别=${if (sex == 1) "男" else "女"}, 配速=$suDu, 公里=$gongLi")

        return try {
            val request = ZhiYuanRegisterRequest(name, zhangHao, password, renZhen, sex, suDu, gongLi)
            Log.d(TAG, "请求体: {\"name\":\"$name\",\"zhangHao\":\"$zhangHao\",\"password\":\"***\",\"renZhen\":\"$renZhen\",\"sex\":$sex,\"suDu\":$suDu,\"gongLi\":$gongLi}")

            val response = api.zhiYuanRegister(request)
            val httpCode = response.code()
            val body = response.body()
            Log.d(TAG, "HTTP 状态码: $httpCode")
            Log.d(TAG, "响应体: $body")

            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 志愿者注册成功 —— data=${body.data}")
                Result.success(body.data ?: false)
            } else {
                val errMsg = body?.message ?: "注册失败"
                Log.e(TAG, "❌ 志愿者注册失败 —— code=${body?.code}, message=$errMsg")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 志愿者注册异常: ${e.javaClass.simpleName} —— ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMangRenUser(userId: Int): Result<MangRenUserInfo> {
        Log.i(TAG, "获取盲人信息 —— userId=$userId")
        return try {
            val response = api.mangRenGetUser(userId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 获取盲人信息成功: ${body.data}")
                Result.success(body.data!!)
            } else {
                Log.e(TAG, "❌ 获取盲人信息失败 —— code=${body?.code}, message=${body?.message}")
                Result.failure(Exception(body?.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取盲人信息异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getZhiYuanUser(userId: Int): Result<ZhiYuanUserInfo> {
        Log.i(TAG, "获取志愿者信息 —— userId=$userId")
        return try {
            val response = api.zhiYuanGetUser(userId)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                Log.i(TAG, "✅ 获取志愿者信息成功: ${body.data}")
                Result.success(body.data!!)
            } else {
                Log.e(TAG, "❌ 获取志愿者信息失败 —— code=${body?.code}, message=${body?.message}")
                Result.failure(Exception(body?.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取志愿者信息异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updatePassword(userType: Int, userId: Int, password: String): Result<Boolean> {
        val typeName = if (userType == USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.i(TAG, "修改密码 —— 用户类型: $typeName, userId=$userId")
        return try {
            val body = PasswordUpdateRequest(userId, password)
            val response = if (userType == USER_TYPE_MANGREN) {
                api.mangRenUpdatePassword(body)
            } else {
                api.zhiYuanUpdatePassword(body)
            }
            if (response.isSuccessful && response.body()?.code == 200 && response.body()?.data == true) {
                Log.i(TAG, "✅ 修改密码成功")
                Result.success(true)
            } else {
                val errMsg = response.body()?.message ?: "修改密码失败"
                Log.e(TAG, "❌ 修改密码失败 —— code=${response.body()?.code}, data=${response.body()?.data}, message=$errMsg")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 修改密码异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    /** 修改用户信息 */
    suspend fun updateUser(
        userType: Int, userId: Int, name: String, sex: Int, suDu: Double, gongLi: Double, renZhen: String = ""
    ): Result<Boolean> {
        val typeName = if (userType == USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.i(TAG, "修改用户信息 —— 用户类型: $typeName, userId=$userId, name=$name, sex=$sex, suDu=$suDu, gongLi=$gongLi, renZhen=$renZhen")
        return try {
            val request = UpdateUserRequest(userId, name, sex, suDu, gongLi, renZhen)
            val response = if (userType == USER_TYPE_MANGREN) {
                api.mangRenUpdateUser(request)
            } else {
                api.zhiYuanUpdateUser(request)
            }
            val body = response.body()
            if (response.isSuccessful && response.body()?.code == 200 && response.body()?.data == true) {
                Log.i(TAG, "✅ 修改用户信息成功")
                Result.success(true)
            } else {
                Log.e(TAG, "❌ 修改用户信息失败 —— code=${body?.code}, data=${body?.data}, message=${body?.message}")
                Result.failure(Exception(body?.message ?: "修改信息失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 修改用户信息异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getRunData(userType: Int, userId: Int): Result<List<RunData>> {
        val typeName = if (userType == USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.i(TAG, "获取跑步数据 —— 用户类型: $typeName, userId=$userId")
        return try {
            val request = RunDataRequest(userType, userId)
            val response = api.getRunData(request)
            val body = response.body()
            if (response.isSuccessful && body?.code == 200) {
                val list = body.data ?: emptyList()
                Log.i(TAG, "✅ 获取跑步数据成功 —— 共 ${list.size} 条")
                Result.success(list)
            } else {
                Log.e(TAG, "❌ 获取跑步数据失败 —— ${body?.message}")
                Result.failure(Exception(body?.message ?: "获取跑步数据失败"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取跑步数据异常: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        const val USER_TYPE_MANGREN = 0
        const val USER_TYPE_ZHIYUAN = 1
    }
}
