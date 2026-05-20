package com.mio.applicationwork.ui.screen.reservationlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mio.applicationwork.data.model.YuYueItem
import com.mio.applicationwork.data.repository.ReservationRepository
import com.mio.applicationwork.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReservationListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reservations: List<YuYueItem> = emptyList(),
    val actionLoading: Boolean = false,
    val actionMessage: String? = null,
    val viewAll: Boolean = true
)

class ReservationListViewModel : ViewModel() {

    private val repository = ReservationRepository()
    private val TAG = "ReservationListVM"
    private val _uiState = MutableStateFlow(ReservationListUiState())
    val uiState: StateFlow<ReservationListUiState> = _uiState

    private var userType: Int = UserRepository.USER_TYPE_MANGREN
    private var userId: Int = 0

    fun init(userType: Int, userId: Int) {
        this.userType = userType
        this.userId = userId
        loadReservations()
    }

    fun loadReservations() {
        val typeName = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"
        val viewAll = _uiState.value.viewAll
        val viewLabel = if (userType != UserRepository.USER_TYPE_MANGREN && !viewAll) "我的接单" else ""
        Log.i(TAG, "加载预约列表 —— $typeName $viewLabel, userId=$userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = if (userType == UserRepository.USER_TYPE_MANGREN) {
                repository.getMangRenYuYue(userId)
            } else if (viewAll) {
                repository.getAllYuYue()
            } else {
                repository.getZhiYuanYuYue(userId)
            }
            result.fold(
                onSuccess = { list ->
                    Log.i(TAG, "✅ 加载成功，共 ${list.size} 条")
                    _uiState.value = _uiState.value.copy(isLoading = false, reservations = list)
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 加载失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
            )
        }
    }

    fun cancelMangRen(yuYueId: Int) {
        Log.i(TAG, "盲人取消预约 —— yuYueId=$yuYueId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionLoading = true)
            repository.mangRenDelYuYue(yuYueId).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 取消成功")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = "已取消预约")
                    loadReservations()
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 取消失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = e.message)
                }
            )
        }
    }

    fun acceptYuYue(yuYueId: Int) {
        Log.i(TAG, "志愿者接单 —— volunteerId=$userId, yuYueId=$yuYueId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionLoading = true)
            repository.zhiYuanSelectYuYue(userId, yuYueId).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 接单成功")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = "接单成功")
                    loadReservations()
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 接单失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = e.message)
                }
            )
        }
    }

    fun cancelZhiYuan(yuYueId: Int) {
        Log.i(TAG, "志愿者取消已接预约 —— yuYueId=$yuYueId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionLoading = true)
            repository.zhiYuanDelYuYue(yuYueId).fold(
                onSuccess = {
                    Log.i(TAG, "✅ 取消成功")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = "已取消预约")
                    loadReservations()
                },
                onFailure = { e ->
                    Log.e(TAG, "❌ 取消失败: ${e.message}")
                    _uiState.value = _uiState.value.copy(actionLoading = false, actionMessage = e.message)
                }
            )
        }
    }

    fun toggleViewAll() {
        _uiState.value = _uiState.value.copy(viewAll = !_uiState.value.viewAll)
        loadReservations()
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
}
