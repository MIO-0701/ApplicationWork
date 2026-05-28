package com.mio.applicationwork.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.local.SessionManager
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 登录页 UI 状态
 * @property rememberSevenDays 用户是否勾选"7天免登录"复选框
 */
//登录页 UI 状态 包含账号、密码、用户身份、是否勾选 7 天免登录、加载状态、错误信息和登录成功结果。
data class LoginUiState(
    val zhangHao: String = "",
    val password: String = "",
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val rememberSevenDays: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: LoginResult? = null   // 非 null 时触发 LaunchedEffect 导航
)

/**
 * 登录成功返回的数据，透传到 NavGraph → HomeScreen
 * @property userName 登录后从 getuser 接口拉取的姓名，用于首页头像和欢迎语
 */
data class LoginResult(
    val userId: Int,
    val token: String,
    val userType: Int,
    val userName: String = ""
)

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "LoginVM"

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun updateZhangHao(value: String) {
        Log.d(TAG, "输入账号: $value")
        _uiState.value = _uiState.value.copy(zhangHao = value, errorMessage = null)
    }

    fun updatePassword(value: String) {
        Log.d(TAG, "输入密码: ${if (value.isNotEmpty()) "***" else "(空)"}")
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun updateUserType(userType: Int) {
        val typeName = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.d(TAG, "切换用户类型: $typeName ($userType)")
        _uiState.value = _uiState.value.copy(userType = userType, errorMessage = null)
    }

    fun updateRememberSevenDays(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberSevenDays = value)
    }

    /**
     * 执行登录
     * 1. 表单校验（账号/密码非空）
     * 2. 调用对应身份的登录 API（盲人 /mangRen/login, 志愿者 /zhiYuan/login）
     * 3. 登录成功后拉取用户姓名（调用 getuser 接口）
     * 4. 根据"7天免登录"复选框决定是否持久化会话:
     *    - 勾选 → SessionManager.saveSession 写本地
     *    - 未勾选 → 仅设 RetrofitClient token，关 App 即失效
     * 5. 设置 loginSuccess 状态触发 UI 导航到主页
     */
    fun login() {
        val state = _uiState.value
        Log.i(TAG, "→ 点击登录按钮 (用户类型=${state.userType})")

        if (state.zhangHao.isBlank() || state.password.isBlank()) {
            Log.w(TAG, "表单校验失败: 账号或密码为空")
            _uiState.value = state.copy(errorMessage = "请输入账号和密码")
            return
        }

        Log.i(TAG, "开始异步登录请求...")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.login(state.userType, state.zhangHao, state.password)
            result.fold(
                onSuccess = { data ->
                    Log.i(TAG, "✅ 登录成功，正在获取用户信息...")
                    // 从服务端拉取姓名用于首页展示（登录接口不返回姓名）
                    val name = fetchUserName(state.userType, data.id)

                    // 根据复选框决定是否 7 天免登录
                    if (state.rememberSevenDays) {
                        SessionManager.saveSession(data.token, data.id, state.userType, name)
                    } else {
                        SessionManager.clearSession()                  // 清除旧的免登录数据
                        RetrofitClient.setToken(data.token)           // 仅本次会话有效
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = LoginResult(data.id, data.token, state.userType, userName = name)
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 登录流程失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "登录失败"
                    )
                }
            )
        }
    }

    /**
     * 启动时检查本地是否有有效会话（SessionManager.isSessionValid）
     * 如果有且未过期 → 直接设置 loginSuccess 触发导航，跳过登录页
     * 由 LoginScreen 的 LaunchedEffect(Unit) 调用
     */
    fun checkAutoLogin() {
        if (SessionManager.isSessionValid()) {
            Log.i(TAG, "检测到有效会话，自动登录 —— userId=${SessionManager.getUserId()}")
            _uiState.value = _uiState.value.copy(
                loginSuccess = LoginResult(
                    userId = SessionManager.getUserId(),
                    token = "",
                    userType = SessionManager.getUserType(),
                    userName = SessionManager.getUserName()
                )
            )
        }
    }

    /**
     * 登录成功后拉取用户真实姓名（login 接口只返回 id + token，不含姓名）
     * 根据 userType 调用不同的 getuser 接口
     */
    private suspend fun fetchUserName(userType: Int, userId: Int): String {
        return if (userType == UserRepository.USER_TYPE_MANGREN) {
            repository.getMangRenUser(userId).fold(
                onSuccess = { it.name },
                onFailure = { Log.w(TAG, "获取姓名失败: ${it.message}"); "" }
            )
        } else {
            repository.getZhiYuanUser(userId).fold(
                onSuccess = { it.name },
                onFailure = { Log.w(TAG, "获取姓名失败: ${it.message}"); "" }
            )
        }
    }

    /** 清除登录成功标记，防止 LaunchedEffect 重复触发导航 */
    fun clearLoginSuccess() {
        Log.d(TAG, "清除登录成功标记（防重复导航）")
        _uiState.value = _uiState.value.copy(loginSuccess = null)
    }
}
