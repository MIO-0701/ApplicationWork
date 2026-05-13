package com.mio.applicationwork.ui.screen.createreservation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    userId: Int,
    onNavigateBack: () -> Unit
) {
    var diDian by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建预约") },
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
            OutlinedTextField(
                value = diDian,
                onValueChange = { diDian = it },
                label = { Text("跑步地点") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("预约时间 (yyyy-MM-dd HH:mm:ss)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { /* TODO: 调用创建预约接口 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("提交预约")
            }
        }
    }
}
