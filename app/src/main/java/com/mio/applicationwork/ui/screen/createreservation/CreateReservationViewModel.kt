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
import java.util.TimeZone

data class CreateReservationUiState(
    val diDian: String = "",
    val time: String = "",
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

    fun updateTime(value: String) {
        _uiState.value = _uiState.value.copy(time = value, errorMessage = null)
    }

    fun updateDateTime(millis: Long) {
        _uiState.value = _uiState.value.copy(
            selectedDateMillis = millis,
            errorMessage = null
        )
    }

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
