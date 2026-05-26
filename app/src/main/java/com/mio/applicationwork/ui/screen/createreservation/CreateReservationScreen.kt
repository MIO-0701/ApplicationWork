package com.mio.applicationwork.ui.screen.createreservation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CreateReservationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 控制两个系统弹窗的显示状态
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNavigateBack()
            viewModel.clearSuccess()
        }
    }

    // ── DatePickerDialog: 先选日期 ──
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.updateDateTime(millis)      // 只存 millis，等时间也选好后一起格式化
                    }
                    showDatePicker = false
                    showTimePicker = true                    // 选完日期接着选时间
                }) { Text("下一步") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("取消") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── TimePickerDialog: 再选时间 ──
    if (showTimePicker) {
        // 从上次存的 millis 初始化 Calendar，保持日期不变
        val calendar = Calendar.getInstance().apply {
            uiState.selectedDateMillis?.let { timeInMillis = it }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    calendar.set(Calendar.MINUTE, timePickerState.minute)
                    viewModel.updateDateTime(calendar.timeInMillis)   // 合并日期+时间，更新 millis
                    showTimePicker = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("取消") } },
            title = { Text("选择时间") }      // Material3 新版必传参数
        ) {
            TimeInput(state = timePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建预约") },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("返回") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 跑步地点
            OutlinedTextField(
                value = uiState.diDian,
                onValueChange = { viewModel.updateDiDian(it) },
                label = { Text("跑步地点") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── 日期时间: 只读框 + 点击弹出滑轮选择器 ──
            // 用 Box 包裹 + clickable 在外层实现点击拦截，TextField 设 enabled=false 防键盘弹出
            Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                OutlinedTextField(
                    value = uiState.selectedDateMillis?.let { formatMillis(it) } ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("预约时间") },
                    placeholder = { Text("点击选择日期和时间") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.submit(userId) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) CircularProgressIndicator(
                    Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("提交预约")
            }
        }
    }
}

/** 毫秒时间戳 → "2026年5月22日 16:10"（系统默认时区） */
private fun formatMillis(millis: Long): String {
    val fmt = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}
