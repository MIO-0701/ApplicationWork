package com.mio.applicationwork.ui.screen.changepassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val userId: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class ChangePasswordViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "ChangePwdVM"
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun updateOldPassword(value: String) {
        _uiState.value = _uiState.value.copy(oldPassword = value, errorMessage = null)
    }
    fun updateNewPassword(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, errorMessage = null)
    }
    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun setUserType(userType: Int) {
        _uiState.value = _uiState.value.copy(userType = userType)
    }

    fun setUserId(userId: Int) {
        _uiState.value = _uiState.value.copy(userId = userId)
    }

    fun submit() {
        val state = _uiState.value

        if (state.oldPassword.isBlank() || state.newPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请填写所有密码字段")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.value = state.copy(errorMessage = "两次输入的新密码不一致")
            return
        }

        Log.i(TAG, "→ 提交修改密码")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.updatePassword(state.userType, state.userId, state.newPassword)
            result.fold(
                onSuccess = {
                    Log.i(TAG, "✅ 修改密码成功")
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 修改密码失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "修改密码失败"
                    )
                }
            )
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
