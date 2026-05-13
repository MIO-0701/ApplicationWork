package com.mio.applicationwork.ui.screen.rundata

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunDataScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("运动数据") },
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
            // TODO: 调用 /user/getRun 获取并展示跑步数据
            Text(
                text = "运动数据（待接入）",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
