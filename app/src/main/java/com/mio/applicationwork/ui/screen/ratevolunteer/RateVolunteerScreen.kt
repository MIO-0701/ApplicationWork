package com.mio.applicationwork.ui.screen.ratevolunteer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateVolunteerScreen(
    userId: Int,
    onNavigateBack: () -> Unit
) {
    var volunteerId by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(3f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("评价志愿者") },
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
                value = volunteerId,
                onValueChange = { volunteerId = it },
                label = { Text("志愿者ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("评分: ${rating.roundToInt()} 分")
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { /* TODO: 调用评价接口 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("提交评价")
            }
        }
    }
}
