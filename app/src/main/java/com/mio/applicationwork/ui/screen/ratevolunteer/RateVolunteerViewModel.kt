package com.mio.applicationwork.ui.screen.ratevolunteer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RateVolunteerUiState(
    val zhiYuanId: String = "",
    val pingFen: Float = 3f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class RateVolunteerViewModel : ViewModel() {

    private val repository = ReservationRepository()
    private val TAG = "RateVolunteerVM"
    private val _uiState = MutableStateFlow(RateVolunteerUiState())
    val uiState: StateFlow<RateVolunteerUiState> = _uiState

    fun updateZhiYuanId(value: String) {
        _uiState.value = _uiState.value.copy(zhiYuanId = value, errorMessage = null)
    }

    fun updatePingFen(value: Float) {
        _uiState.value = _uiState.value.copy(pingFen = value, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value
        val zhiYuanId = state.zhiYuanId.toIntOrNull() ?: 0
        if (zhiYuanId <= 0) {
            _uiState.value = state.copy(errorMessage = "请输入有效的志愿者ID")
            return
        }

        Log.i(TAG, "→ 提交评价 —— zhiYuanId=$zhiYuanId, pingFen=${state.pingFen.toInt()}")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.pingJiaZhiYuan(zhiYuanId, state.pingFen.toInt()).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 评价成功")
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 评价失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "评价失败"
                    )
                }
            )
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
