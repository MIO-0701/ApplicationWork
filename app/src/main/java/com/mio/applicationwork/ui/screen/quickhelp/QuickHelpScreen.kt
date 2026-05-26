package com.mio.applicationwork.ui.screen.quickhelp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 一键求助 / 实时匹配页面 (盲人 & 志愿者通用)
 *
 * 状态机驱动的四阶段 UI:
 * - Idle:    地点输入 + "发起求助"按钮
 * - Matching: 转圈 + "正在为您匹配..." + "取消匹配"
 * - Matched:  对方信息卡片 + 跑步数据录入表单 + "重新匹配"
 * - Error:    错误信息 + "重试"
 * 返回按钮: 如果正在匹配中，先取消匹配再返回
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickHelpScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: QuickHelpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(userType, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("一键求助") },
                navigationIcon = {
                    TextButton(onClick = {
                        // 正在匹配时先发取消请求再返回，防止后端匹配池残留
                        if (uiState.phase == MatchPhase.Matching) {
                            viewModel.cancelMatching()
                        }
                        onNavigateBack()
                    }) { Text("返回") }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.phase) {

                // ── Idle: 输入地点 → 发起匹配 ──
                MatchPhase.Idle -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "输入跑步地点，匹配附近的跑友",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value = uiState.diDian,
                            onValueChange = { viewModel.updateDiDian(it) },
                            label = { Text("跑步地点") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        uiState.errorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.startMatching() }, modifier = Modifier.fillMaxWidth()) {
                            Text("发起求助")
                        }
                    }
                }

                // ── Matching: 3秒轮询中 → 转圈 + 取消按钮 ──
                MatchPhase.Matching -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("正在为您匹配附近的跑友...", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("地点: ${uiState.diDian}", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(onClick = { viewModel.cancelMatching() }, modifier = Modifier.fillMaxWidth()) {
                            Text("取消匹配")
                        }
                    }
                }

                // ── Matched: 匹配成功 → 对方信息 + 跑步数据录入 + 重新匹配 ──
                MatchPhase.Matched -> {
                    // verticalScroll: 防内容过长时溢出
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                    ) {
                        Text("匹配成功!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(20.dp))

                        // 匹配对象信息卡片
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("匹配对象信息", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(12.dp))
                                val m = uiState.matchedUser
                                InfoRow("姓名", m?.name ?: "-")
                                InfoRow("性别", if (m?.sex == 1) "男" else "女")
                                InfoRow("配速", if (m?.suDu != null) "${m.suDu} min/km" else "-")
                                InfoRow("公里数", if (m?.gongLi != null) "${m.gongLi} km" else "-")
                                // 匹配到盲人时显示额外信息（志愿者视角）
                                if (uiState.matchedUserType == 0) {
                                    InfoRow("在线时间", m?.zaiXian ?: "-")
                                    InfoRow("评分", if (m?.pingFen != null) "${m.pingFen} 分" else "-")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 跑步数据录入表单（提交后隐藏，显示"已提交"）
                        if (uiState.runSubmitted) {
                            Text("跑步数据已提交", color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                        } else {
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("记录本次跑步数据", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(value = uiState.suDu, onValueChange = { viewModel.updateSuDu(it) },
                                        label = { Text("配速") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(value = uiState.shiChang, onValueChange = { viewModel.updateShiChang(it) },
                                        label = { Text("时长 (min)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(value = uiState.juLi, onValueChange = { viewModel.updateJuLi(it) },
                                        label = { Text("公里数 (km)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(onClick = { viewModel.submitRunData() }, enabled = !uiState.runSubmitting,
                                        modifier = Modifier.fillMaxWidth()) {
                                        if (uiState.runSubmitting) CircularProgressIndicator(
                                            Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                        else Text("提交跑步数据")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Button(onClick = { viewModel.resetToIdle() }, modifier = Modifier.fillMaxWidth()) {
                            Text("重新匹配")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // ── Error: 匹配失败 / 网络错误 → 重试 ──
                MatchPhase.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.errorMessage ?: "匹配失败", color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { viewModel.resetToIdle() }) { Text("重试") }
                    }
                }
            }
        }
    }
}

/** 卡片内标签-值单行 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
