package com.mio.applicationwork.ui.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.model.MangRenUserInfo
import com.mio.applicationwork.data.model.ZhiYuanUserInfo
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // 通用字段
    val id: Int = 0,
    val name: String = "",
    val sex: Int = 1,
    val suDu: Double = 0.0,
    val gongLi: Double = 0.0,
    val createTime: String = "",
    // 志愿者专有
    val pingFen: Double = 0.0,
    val renZhen: String = ""
)

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "ProfileVM"
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    /**
     * 加载用户信息
     * @param userType 0=盲人, 1=志愿者
     * @param userId 用户ID
     */
    fun loadUser(userType: Int, userId: Int) {
        Log.i(TAG, "加载用户信息 —— userType=$userType, userId=$userId")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            if (userType == UserRepository.USER_TYPE_MANGREN) {
                repository.getMangRenUser(userId).fold(
                    onSuccess = { info ->
                        Log.i(TAG, "✅ 盲人信息加载成功")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            id = info.id, name = info.name, sex = info.sex,
                            suDu = info.suDu, gongLi = info.gongLi,
                            createTime = info.createTime ?: "未知"
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "❌ 加载失败: ${e.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "加载失败"
                        )
                    }
                )
            } else {
                repository.getZhiYuanUser(userId).fold(
                    onSuccess = { info ->
                        Log.i(TAG, "✅ 志愿者信息加载成功")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            id = info.id, name = info.name, sex = info.sex,
                            suDu = info.suDu, gongLi = info.gongLi,
                            pingFen = info.pingFen, renZhen = info.renZhen,
                            createTime = info.createTime ?: "未知"
                        )
                    },
                    onFailure = { e ->
                        Log.e(TAG, "❌ 加载失败: ${e.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "加载失败"
                        )
                    }
                )
            }
        }
    }
}
