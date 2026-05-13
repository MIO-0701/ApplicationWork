package com.mio.applicationwork.ui.screen.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val zhangHao: String = "",
    val password: String = "",
    val sex: Int = 1,
    val suDu: String = "0",
    val gongLi: String = "0",
    val renZhen: String = "",
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "RegisterVM"

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun updateName(value: String) {
        Log.d(TAG, "输入姓名: $value")
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun updateZhangHao(value: String) {
        Log.d(TAG, "输入账号: $value")
        _uiState.value = _uiState.value.copy(zhangHao = value, errorMessage = null)
    }

    fun updatePassword(value: String) {
        Log.d(TAG, "输入密码: ${if (value.isNotEmpty()) "***" else "(空)"}")
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun updateSex(sex: Int) {
        Log.d(TAG, "选择性别: ${if (sex == 1) "男" else "女"}")
        _uiState.value = _uiState.value.copy(sex = sex, errorMessage = null)
    }

    fun updateSuDu(value: String) {
        Log.d(TAG, "输入配速: $value")
        _uiState.value = _uiState.value.copy(suDu = value, errorMessage = null)
    }

    fun updateGongLi(value: String) {
        Log.d(TAG, "输入公里数: $value")
        _uiState.value = _uiState.value.copy(gongLi = value, errorMessage = null)
    }

    fun updateRenZhen(value: String) {
        Log.d(TAG, "输入认证信息: $value")
        _uiState.value = _uiState.value.copy(renZhen = value, errorMessage = null)
    }

    fun updateUserType(userType: Int) {
        val typeName = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.d(TAG, "切换用户类型: $typeName ($userType)")
        _uiState.value = _uiState.value.copy(userType = userType, errorMessage = null)
    }

    fun register() {
        val state = _uiState.value
        Log.i(TAG, "→ 点击注册按钮 (用户类型=${state.userType}, 姓名=${state.name}, 账号=${state.zhangHao})")

        if (state.name.isBlank() || state.zhangHao.isBlank() || state.password.isBlank()) {
            Log.w(TAG, "表单校验失败: 有必填字段为空")
            _uiState.value = state.copy(errorMessage = "请填写所有必填信息")
            return
        }

        val suDu = state.suDu.toDoubleOrNull() ?: 0.0
        val gongLi = state.gongLi.toDoubleOrNull() ?: 0.0
        Log.d(TAG, "转换后参数: suDu=$suDu, gongLi=$gongLi, sex=${state.sex}")

        Log.i(TAG, "开始异步注册请求 (${if (state.userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"})...")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = if (state.userType == UserRepository.USER_TYPE_MANGREN) {
                repository.registerMangRen(
                    state.name, state.zhangHao, state.password,
                    state.sex, suDu, gongLi
                )
            } else {
                repository.registerZhiYuan(
                    state.name, state.zhangHao, state.password, state.renZhen,
                    state.sex, suDu, gongLi
                )
            }
            result.fold(
                onSuccess = {
                    Log.i(TAG, "✅ 注册流程完成 —— 即将返回登录页")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, registerSuccess = true
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 注册流程失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "注册失败"
                    )
                }
            )
        }
    }

    fun clearRegisterSuccess() {
        Log.d(TAG, "清除注册成功标记（防重复导航）")
        _uiState.value = _uiState.value.copy(registerSuccess = false)
    }
}
