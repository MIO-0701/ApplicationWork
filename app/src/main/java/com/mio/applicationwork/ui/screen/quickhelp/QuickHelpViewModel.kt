package com.mio.applicationwork.ui.screen.quickhelp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.model.MatchUserInfo
import com.mio.applicationwork.data.repository.MatchRepository
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MatchingUiState(
    val diDian: String = "",
    val userType: Int = UserRepository.USER_TYPE_MANGREN,
    val userId: Int = 0,
    val phase: MatchPhase = MatchPhase.Idle,
    val errorMessage: String? = null,
    val matchedUser: MatchUserInfo? = null,
    val matchedUserId: Int = 0,
    val matchedUserType: Int = 0,
    val matchedDiDian: String = "",
    // 跑步数据录入
    val suDu: String = "",
    val shiChang: String = "",
    val juLi: String = "",
    val runSubmitting: Boolean = false,
    val runSubmitted: Boolean = false
)

enum class MatchPhase { Idle, Matching, Matched, Error }

class QuickHelpViewModel : ViewModel() {

    private val matchRepo = MatchRepository()
    private val userRepo = UserRepository()
    private val TAG = "QuickHelpVM"
    private val _uiState = MutableStateFlow(MatchingUiState())
    val uiState: StateFlow<MatchingUiState> = _uiState

    private var pollingJob: Job? = null

    fun init(userType: Int, userId: Int) {
        _uiState.value = _uiState.value.copy(userType = userType, userId = userId)
    }

    fun updateDiDian(value: String) {
        _uiState.value = _uiState.value.copy(diDian = value, errorMessage = null)
    }

    fun startMatching() {
        val state = _uiState.value
        if (state.diDian.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请输入地点")
            return
        }

        Log.i(TAG, "开始匹配 —— userType=${state.userType}, userId=${state.userId}, 地点=${state.diDian}")
        _uiState.value = state.copy(phase = MatchPhase.Matching, errorMessage = null)

        pollingJob = viewModelScope.launch {
            while (isActive) {
                val result = matchRepo.piPei(state.userType, state.userId, state.diDian)
                result.fold(
                    onSuccess = { response ->
                        if (response?.data != null) {
                            Log.i(TAG, "✅ 匹配成功!")
                            _uiState.value = _uiState.value.copy(
                                phase = MatchPhase.Matched,
                                matchedUser = response.data,
                                matchedUserId = response.userId,
                                matchedUserType = response.usertype,
                                matchedDiDian = response.diDian
                            )
                            return@launch
                        }
                        // data is null, continue polling
                        Log.d(TAG, "未匹配到，3秒后重试...")
                    },
                    onFailure = { e ->
                        Log.e(TAG, "❌ 匹配轮询异常: ${e.message}")
                        _uiState.value = _uiState.value.copy(
                            phase = MatchPhase.Error,
                            errorMessage = e.message ?: "匹配失败"
                        )
                        return@launch
                    }
                )
                delay(3000)
            }
        }
    }

    fun cancelMatching() {
        val state = _uiState.value
        Log.i(TAG, "取消匹配")
        pollingJob?.cancel()
        pollingJob = null

        viewModelScope.launch {
            matchRepo.delPiPei(state.userType, state.userId, state.diDian).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 已取消匹配")
                    _uiState.value = _uiState.value.copy(phase = MatchPhase.Idle, errorMessage = null)
                },
                onFailure = { e ->
                    Log.e(TAG, "取消匹配失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(phase = MatchPhase.Idle, errorMessage = null)
                }
            )
        }
    }

    fun updateSuDu(value: String) {
        _uiState.value = _uiState.value.copy(suDu = value, runSubmitted = false)
    }

    fun updateShiChang(value: String) {
        _uiState.value = _uiState.value.copy(shiChang = value, runSubmitted = false)
    }

    fun updateJuLi(value: String) {
        _uiState.value = _uiState.value.copy(juLi = value, runSubmitted = false)
    }

    fun submitRunData() {
        val state = _uiState.value
        val suDu = state.suDu.toIntOrNull() ?: 0
        val shiChang = state.shiChang.toIntOrNull() ?: 0
        val juLi = state.juLi.toIntOrNull() ?: 0

        if (suDu <= 0 || shiChang <= 0 || juLi <= 0) {
            _uiState.value = state.copy(errorMessage = "请填写完整的跑步数据")
            return
        }

        Log.i(TAG, "提交跑步数据 —— userType=${state.userType}, userId=${state.userId}, suDu=$suDu, shiChang=$shiChang, juLi=$juLi")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(runSubmitting = true, errorMessage = null)
            userRepo.addRunData(state.userType, state.userId, suDu, shiChang, juLi).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 跑步数据提交成功")
                    _uiState.value = _uiState.value.copy(runSubmitting = false, runSubmitted = true)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 跑步数据提交失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        runSubmitting = false,
                        errorMessage = e.message ?: "提交跑步数据失败"
                    )
                }
            )
        }
    }

    fun resetToIdle() {
        pollingJob?.cancel()
        pollingJob = null
        _uiState.value = MatchingUiState(
            userType = _uiState.value.userType,
            userId = _uiState.value.userId
        )
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
