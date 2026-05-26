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
import java.text.SimpleDateFormat
import java.util.Locale

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

    // Snackbar: 接单/取消等操作结果的轻提示
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

            // ── 志愿者端视图切换: 全部预约 ↔ 我的接单 ──
            if (!isMangRen) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    uiState.errorMessage != null -> {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.loadReservations() }) { Text("重试") }
                        }
                    }
                    uiState.reservations.isEmpty() -> Text(
                        text = "暂无预约", modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.reservations, key = { it.yuYueID }) { item ->
                            ReservationCard(
                                item = item,
                                isMangRen = isMangRen,
                                isMyReservations = !uiState.viewAll,   // "我的接单"模式下才能取消
                                currentUserId = userId,
                                actionLoading = uiState.actionLoading,
                                onCancelMangRen = { viewModel.cancelMangRen(item.yuYueID) },
                                onAccept = { viewModel.acceptYuYue(item.yuYueID) },
                                onCancelZhiYuan = { viewModel.cancelZhiYuan(item.yuYueID) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

/**
 * 单张预约卡片
 * @param isMyReservations 志愿者"我的接单"模式下才展示取消按钮，防止跨用户取消
 */
@Composable
private fun ReservationCard(
    item: YuYueItem,
    isMangRen: Boolean,
    isMyReservations: Boolean,
    currentUserId: Int,
    actionLoading: Boolean,
    onCancelMangRen: () -> Unit,
    onAccept: () -> Unit,
    onCancelZhiYuan: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 地点
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("地点", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.diDian, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(6.dp))
            // 时间 —— 后端返回 ISO 8601 UTC 格式，需转为本地时间展示
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("时间", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatReservationTime(item.createTime), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            // 配速 / 公里数
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("配速 / 公里数", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${item.suDu} min/km  |  ${item.gongLi} km", style = MaterialTheme.typography.bodyMedium)
            }

            if (isMangRen) {
                // ── 盲人端: 显示接单状态 + 取消按钮 ──
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("志愿者", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (item.zhiYuanId > 0) "已接单 (ID: ${item.zhiYuanId})" else "待接单",
                        color = if (item.zhiYuanId > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(onClick = onCancelMangRen, enabled = !actionLoading, modifier = Modifier.fillMaxWidth()) {
                    Text("取消预约")
                }
            } else {
                // ── 志愿者端: 全部预约可接单，"我的接单"可取消 ──
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("盲人ID", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${item.mangRenId}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(10.dp))
                // zhiYuanId <= 0 表示未被接单（含 0 初始值和 -1 取消后的值）
                if (item.zhiYuanId <= 0) {
                    Button(onClick = onAccept, enabled = !actionLoading, modifier = Modifier.fillMaxWidth()) {
                        Text("接单")
                    }
                } else if (isMyReservations) {
                    // 仅"我的接单"视图允许取消，全部预约中只显示"已接单"文字
                    OutlinedButton(onClick = onCancelZhiYuan, enabled = !actionLoading, modifier = Modifier.fillMaxWidth()) {
                        Text("取消接单")
                    }
                } else {
                    Text(
                        text = "已接单", color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

/**
 * 将后端返回的 ISO 8601 UTC 时间转为本地时区展示
 * 输入: "2026-05-22T08:20:00.000+00:00"
 * 输出: "2026年5月22日 16:20"（UTC+8 本地时间）
 *
 * 使用 SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX") 完整匹配:
 * - SSS  → 毫秒 (.000)
 * - X    → RFC822 时区偏移 (+00:00)
 * parse 时自动读取 +00:00 时区信息，format 时转为系统默认时区
 */
private fun formatReservationTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "未知"
    return try {
        val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
        val localDisplayer = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.getDefault())
        val date = isoParser.parse(raw)
        if (date != null) localDisplayer.format(date) else raw
    } catch (_: Exception) {
        raw
    }
}
