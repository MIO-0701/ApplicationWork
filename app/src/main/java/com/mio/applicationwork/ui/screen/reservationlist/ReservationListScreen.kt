package com.mio.applicationwork.ui.screen.reservationlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mio.applicationwork.data.model.YuYueItem
import com.mio.applicationwork.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ReservationListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val roleText = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"
    val isMangRen = userType == UserRepository.USER_TYPE_MANGREN

    LaunchedEffect(Unit) {
        viewModel.init(userType, userId)
    }

    // Snackbar for action feedback
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预约列表 ($roleText)") },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("返回") } },
                actions = {
                    TextButton(
                        onClick = { viewModel.loadReservations() },
                        enabled = !uiState.isLoading
                    ) { Text("刷新") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Volunteer view toggle
            if (!isMangRen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = uiState.viewAll,
                        onClick = { if (!uiState.viewAll) viewModel.toggleViewAll() },
                        label = { Text("全部预约") }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FilterChip(
                        selected = !uiState.viewAll,
                        onClick = { if (uiState.viewAll) viewModel.toggleViewAll() },
                        label = { Text("我的接单") }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadReservations() }) {
                            Text("重试")
                        }
                    }
                }
                uiState.reservations.isEmpty() -> {
                    Text(
                        text = "暂无预约",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.reservations, key = { it.yuYueID }) { item ->
                            ReservationCard(
                                item = item,
                                isMangRen = isMangRen,
                                currentUserId = userId,
                                actionLoading = uiState.actionLoading,
                                onCancelMangRen = { viewModel.cancelMangRen(item.yuYueID) },
                                onAccept = { viewModel.acceptYuYue(item.yuYueID) },
                                onCancelZhiYuan = { viewModel.cancelZhiYuan(item.yuYueID) }
                            )
                        }
                        // Bottom spacer for FAB clearance
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ReservationCard(
    item: YuYueItem,
    isMangRen: Boolean,
    currentUserId: Int,
    actionLoading: Boolean,
    onCancelMangRen: () -> Unit,
    onAccept: () -> Unit,
    onCancelZhiYuan: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("地点", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.diDian, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("时间", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.createTime ?: "未知", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("配速 / 公里数", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${item.suDu} min/km  |  ${item.gongLi} km", style = MaterialTheme.typography.bodyMedium)
            }

            if (isMangRen) {
                // Blind person: show volunteer ID if accepted
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("志愿者", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (item.zhiYuanId > 0) "已接单 (ID: ${item.zhiYuanId})" else "待接单",
                        color = if (item.zhiYuanId > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onCancelMangRen,
                    enabled = !actionLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("取消预约") }
            } else {
                // Volunteer: show blind person ID, accept or cancel
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("盲人ID", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${item.mangRenId}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (item.zhiYuanId == 0) {
                    // Not yet accepted by anyone
                    Button(
                        onClick = onAccept,
                        enabled = !actionLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("接单") }
                } else {
                    // Accepted (by current volunteer)
                    OutlinedButton(
                        onClick = onCancelZhiYuan,
                        enabled = !actionLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("取消接单") }
                }
            }
        }
    }
}
