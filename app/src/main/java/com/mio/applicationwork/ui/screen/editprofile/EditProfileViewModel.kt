package com.mio.applicationwork.ui.screen.editprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val sex: Int = 1,
    val suDu: String = "",     // String 绑定 TextField
    val gongLi: String = "",
    val renZhen: String = "",  // 仅志愿者
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false
)

class EditProfileViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "EditProfileVM"
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    /**
     * 从 API 加载当前用户信息并回填表单
     */
    fun loadAndInit(userType: Int, userId: Int) {
        Log.i(TAG, "加载用户信息用于编辑 —— userType=$userType, userId=$userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, userType = userType)

            if (userType == UserRepository.USER_TYPE_MANGREN) {
                repository.getMangRenUser(userId).fold(
                    onSuccess = { info ->
                        Log.i(TAG, "✅ 加载成功")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            name = info.name,
                            sex = info.sex,
                            suDu = info.suDu.toString(),
                            gongLi = info.gongLi.toString()
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "❌ 加载失败: ${e.message}")
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
                )
            } else {
                repository.getZhiYuanUser(userId).fold(
                    onSuccess = { info ->
                        Log.i(TAG, "✅ 加载成功")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            name = info.name,
                            sex = info.sex,
                            suDu = info.suDu.toString(),
                            gongLi = info.gongLi.toString(),
                            renZhen = info.renZhen ?: ""
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "❌ 加载失败: ${e.message}")
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                    }
                )
            }
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }
    fun updateSex(sex: Int) {
        _uiState.value = _uiState.value.copy(sex = sex, errorMessage = null)
    }
    fun updateSuDu(value: String) {
        _uiState.value = _uiState.value.copy(suDu = value, errorMessage = null)
    }
    fun updateGongLi(value: String) {
        _uiState.value = _uiState.value.copy(gongLi = value, errorMessage = null)
    }
    fun updateRenZhen(value: String) {
        _uiState.value = _uiState.value.copy(renZhen = value, errorMessage = null)
    }

    /** 提交修改 */
    fun submit(userId: Int) {
        val state = _uiState.value
        Log.i(TAG, "→ 提交修改 —— userId=$userId")

        val suDu = state.suDu.toDoubleOrNull() ?: 0.0
        val gongLi = state.gongLi.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            val result = repository.updateUser(
                userType = state.userType,
                userId = userId,
                name = state.name,
                sex = state.sex,
                suDu = suDu,
                gongLi = gongLi,
                renZhen = state.renZhen
            )
            result.fold(
                onSuccess = {
                    Log.i(TAG, "✅ 修改成功")
                    _uiState.value = _uiState.value.copy(isSaving = false, updateSuccess = true)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 修改失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
                }
            )
        }
    }

    fun clearUpdateSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }
}
