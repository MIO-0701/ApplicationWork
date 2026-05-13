package com.mio.applicationwork.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val zhangHao: String = "",
    val password: String = "",
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: LoginResult? = null
)

data class LoginResult(
    val userId: Int,
    val token: String,
    val userType: Int
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
                    Log.i(TAG, "✅ 登录流程完成 —— 即将跳转 Home(userId=${data.id}, userType=${state.userType})")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = LoginResult(data.id, data.token, state.userType)
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

    fun clearLoginSuccess() {
        Log.d(TAG, "清除登录成功标记（防重复导航）")
        _uiState.value = _uiState.value.copy(loginSuccess = null)
    }
}
