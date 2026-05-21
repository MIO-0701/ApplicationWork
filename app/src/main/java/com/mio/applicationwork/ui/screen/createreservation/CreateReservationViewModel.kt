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
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatted = fmt.format(Date(millis))
        _uiState.value = _uiState.value.copy(
            selectedDateMillis = millis,
            time = formatted,
            errorMessage = null
        )
    }

    fun submit(userId: Int) {
        val state = _uiState.value
        Log.i(TAG, "→ 提交预约 —— userId=$userId, 地点=${state.diDian}, 时间=${state.time}")

        if (state.diDian.isBlank() || state.time.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请填写地点和时间")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.createYuYue(userId, state.diDian, state.time).fold(
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
