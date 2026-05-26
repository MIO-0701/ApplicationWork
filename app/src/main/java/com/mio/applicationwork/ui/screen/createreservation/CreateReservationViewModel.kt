package com.mio.applicationwork.ui.screen.createreservation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 创建预约 UI 状态
 * @property selectedDateMillis DatePicker + TimePicker 组合后的毫秒时间戳
 *         存储原始值而非格式化字符串，避免 UTC 往返转换导致时区偏移
 */
data class CreateReservationUiState(
    val diDian: String = "",
    val selectedDateMillis: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class CreateReservationViewModel : ViewModel() {

    private val repository = ReservationRepository()
    private val TAG = "CreateReservationVM"
    private val _uiState = MutableStateFlow(CreateReservationUiState())
    val uiState: StateFlow<CreateReservationUiState> = _uiState

    fun updateDiDian(value: String) {
        _uiState.value = _uiState.value.copy(diDian = value, errorMessage = null)
    }

    /**
     * 由 DatePicker + TimePicker 组合后调用
     * @param millis Calendar.timeInMillis，包含日期+时间的完整时间戳
     * 只存 millis 原始值，展示时用本地时区格式化，提交时才转字符串
     */
    fun updateDateTime(millis: Long) {
        _uiState.value = _uiState.value.copy(
            selectedDateMillis = millis,
            errorMessage = null
        )
    }

    /**
     * 提交预约
     * 1. 校验地点非空 + 日期时间已选
     * 2. 将 millis 格式化为 "yyyy-MM-dd HH:mm:ss" 发给后端
     * 3. 成功后 success=true → LaunchedEffect 触发自动返回上一页
     */
    fun submit(userId: Int) {
        val state = _uiState.value
        val dateMillis = state.selectedDateMillis
        if (state.diDian.isBlank() || dateMillis == null) {
            _uiState.value = state.copy(errorMessage = "请填写地点和时间")
            return
        }

        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStr = fmt.format(Date(dateMillis))
        Log.i(TAG, "→ 提交预约 —— userId=$userId, 地点=${state.diDian}, 时间=$timeStr")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.createYuYue(userId, state.diDian, timeStr).fold(
                onSuccess = { yuYueId ->
                    Log.i(TAG, "✅ 创建预约成功 —— yuYueId=$yuYueId")
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 创建预约失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "创建预约失败"
                    )
                }
            )
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
