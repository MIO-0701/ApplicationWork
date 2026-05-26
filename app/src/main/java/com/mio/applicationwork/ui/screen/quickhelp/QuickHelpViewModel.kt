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

/**
 * 匹配页 UI 状态
 * @property phase 匹配阶段状态机: Idle → Matching → Matched / Error
 * @property matchedUser 匹配成功后返回的对方用户信息（MatchUserInfo）
 * @property suDu/shiChang/juLi 匹配成功后录入跑步数据的表单字段（String 绑定 TextField 避免输入法问题）
 */
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

/**
 * 匹配状态机:
 * - Idle: 初始状态，显示地点输入框
 * - Matching: 正在轮询匹配，显示加载动画
 * - Matched: 匹配成功，显示对方信息 + 跑步数据录入
 * - Error: 匹配失败/网络错误，显示重试按钮
 */
enum class MatchPhase { Idle, Matching, Matched, Error }

class QuickHelpViewModel : ViewModel() {

    private val matchRepo = MatchRepository()
    private val userRepo = UserRepository()
    private val TAG = "QuickHelpVM"
    private val _uiState = MutableStateFlow(MatchingUiState())
    val uiState: StateFlow<MatchingUiState> = _uiState

    /** 轮询协程 Job，用于取消时停止循环 */
    private var pollingJob: Job? = null

    fun init(userType: Int, userId: Int) {
        _uiState.value = _uiState.value.copy(userType = userType, userId = userId)
    }

    fun updateDiDian(value: String) {
        _uiState.value = _uiState.value.copy(diDian = value, errorMessage = null)
    }

    /**
     * 开始匹配 —— 启动协程，每 3 秒轮询 POST /piPei/piPei
     *
     * 轮询机制:
     * - while(isActive) + delay(3000) 循环发请求
     * - response.data.data == null → 继续轮询（日志打 "匹配中..."）
     * - response.data.data != null → 匹配成功 → 更新 phase=Matched → return@launch 停止轮询
     * - 请求失败 → 更新 phase=Error → return@launch 停止轮询
     * - 用户点"取消匹配" → cancelMatching() 中 cancel Job → isActive 变 false → 循环退出
     */
    fun startMatching() {
        val state = _uiState.value
        if (state.diDian.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请输入地点")
            return
        }

        Log.i(TAG, "开始匹配 —— userType=${state.userType}, userId=${state.userId}, 地点=${state.diDian}")
        _uiState.value = state.copy(phase = MatchPhase.Matching, errorMessage = null)

        pollingJob = viewModelScope.launch {
            while (isActive) {                               // 协程被 cancel 后自动退出
                val result = matchRepo.piPei(state.userType, state.userId, state.diDian)
                result.fold(
                    onSuccess = { response ->
                        if (response?.data != null) {        // 匹配成功: inner data 非 null
                            Log.i(TAG, "✅ 匹配成功!")
                            _uiState.value = _uiState.value.copy(
                                phase = MatchPhase.Matched,
                                matchedUser = response.data,
                                matchedUserId = response.userId,
                                matchedUserType = response.usertype,
                                matchedDiDian = response.diDian
                            )
                            return@launch                    // 退出协程，停止轮询
                        }
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
                delay(3000)                                  // 每 3 秒一次请求
            }
        }
    }

    /**
     * 取消匹配
     * 1. 取消轮询 Job（停止循环）
     * 2. 调用 POST /piPei/delPiPei 通知后端从匹配池移除
     * 3. 回到 Idle 状态
     */
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

    // ── 跑步数据录入 ──────────────────────────────────────────

    fun updateSuDu(value: String) { _uiState.value = _uiState.value.copy(suDu = value, runSubmitted = false) }
    fun updateShiChang(value: String) { _uiState.value = _uiState.value.copy(shiChang = value, runSubmitted = false) }
    fun updateJuLi(value: String) { _uiState.value = _uiState.value.copy(juLi = value, runSubmitted = false) }

    /** 提交本次跑步数据到 POST /user/addRun */
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
                    _uiState.value = _uiState.value.copy(runSubmitting = false, errorMessage = e.message)
                }
            )
        }
    }

    /** 回到 Idle 状态，清空所有匹配结果和跑步数据 */
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
