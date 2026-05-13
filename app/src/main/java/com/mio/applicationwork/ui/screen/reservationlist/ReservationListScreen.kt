package com.mio.applicationwork.ui.screen.reservationlist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit
) {
    val roleText = if (userType == 0) "盲人" else "志愿者"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预约列表 ($roleText)") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("返回") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: 调用 Repository 获取预约列表并展示
            Text(
                text = "预约列表（待接入）",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
