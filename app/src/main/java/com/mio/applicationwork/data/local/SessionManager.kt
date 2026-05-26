package com.mio.applicationwork.data.local

import android.content.Context
import android.content.SharedPreferences
import com.mio.applicationwork.data.api.RetrofitClient

/**
 * 7天免登录会话管理器
 *
 * 使用 SharedPreferences 本地持久化登录态:
 * - 登录成功 + 用户勾选"7天免登录" → [saveSession] 写入 token / userId / userType / 姓名 / 登录时间
 * - App 下次启动 → [isSessionValid] 校验 token 非空 且 登录未超过 7 天 → 自动跳过登录页
 * - 用户点击"退出" 或 超过 7 天 → [clearSession] 清除数据 + 置空 Retrofit token
 * - 用户未勾选"7天免登录" → 不调用 saveSession，仅运行时设 token，关 App 后失效
 */
object SessionManager {

    private const val PREFS_NAME = "app_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USER_TYPE = "userType"
    private const val KEY_USER_NAME = "userName"
    private const val KEY_LOGIN_TIME = "loginTime"

    /** 免登录有效期: 7天（毫秒） */
    private const val EXPIRE_MILLIS = 7 * 24 * 60 * 60 * 1000L

    private lateinit var prefs: SharedPreferences

    /** 必须在 Application.onCreate 中调用一次，初始化 SharedPreferences */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 持久化登录会话到本地，供下次启动自动登录
     * @param token  JWT token，同时设置到 RetrofitClient 全局请求头
     * @param userId 登录用户的 ID
     * @param userType 0=盲人, 1=志愿者
     * @param name   用户真实姓名（登录成功后从 getuser 接口获取）
     */
    fun saveSession(token: String, userId: Int, userType: Int, name: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putInt(KEY_USER_TYPE, userType)
            .putString(KEY_USER_NAME, name)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            .apply() // 异步写磁盘，但内存立即生效（读取时能拿到）
        RetrofitClient.setToken(token)
    }

    /**
     * 检查本地是否有有效会话
     * 条件: token 非空 且 距离上次登录不超过 7 天
     * 过期自动清理，有效则恢复 RetrofitClient 的 token
     */
    fun isSessionValid(): Boolean {
        val token = prefs.getString(KEY_TOKEN, null) ?: return false
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)

        // EXPIRE_MILLIS = 7 * 24 * 60 * 60 * 1000L
        if (System.currentTimeMillis() - loginTime > EXPIRE_MILLIS) {
            clearSession()
            return false
        }
        RetrofitClient.setToken(token)
        return true
    }

    /** 读取已保存的用户 ID（未登录/过期时返回 0） */
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)

    /** 读取已保存的用户类型（0=盲人, 1=志愿者） */
    fun getUserType(): Int = prefs.getInt(KEY_USER_TYPE, 0)

    /** 读取已保存的用户姓名（可能为空字符串） */
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    /** 清除本地会话数据，同时置空 Retrofit 全局 token */
    fun clearSession() {
        prefs.edit().clear().apply()
        RetrofitClient.setToken(null)
    }
}
