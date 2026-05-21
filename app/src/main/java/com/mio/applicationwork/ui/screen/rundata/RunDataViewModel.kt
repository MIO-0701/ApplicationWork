package com.mio.applicationwork.ui.screen.rundata

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.model.RunData
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RunDataUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val records: List<RunData> = emptyList()
)

class RunDataViewModel : ViewModel() {

    private val repository = UserRepository()
    private val TAG = "RunDataVM"
    private val _uiState = MutableStateFlow(RunDataUiState())
    val uiState: StateFlow<RunDataUiState> = _uiState

    fun loadData(userType: Int, userId: Int) {
        val typeName = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"
        Log.i(TAG, "加载运动数据 —— $typeName, userId=$userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getRunData(userType, userId).fold(
                onSuccess = { list ->
                    Log.i(TAG, "✅ 加载成功，共 ${list.size} 条记录")
                    _uiState.value = _uiState.value.copy(isLoading = false, records = list)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 加载失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
            )
        }
    }
}
