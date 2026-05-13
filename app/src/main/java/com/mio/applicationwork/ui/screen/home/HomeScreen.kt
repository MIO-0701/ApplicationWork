package com.mio.applicationwork.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mio.applicationwork.data.api.RetrofitClient
import com.mio.applicationwork.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userType: Int,
    userId: Int,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreateReservation: () -> Unit,
    onNavigateToReservationList: () -> Unit,
    onNavigateToRunData: () -> Unit,
    onNavigateToRateVolunteer: () -> Unit,
    onNavigateToQuickHelp: () -> Unit
) {
    val roleText = if (userType == UserRepository.USER_TYPE_MANGREN) "盲人" else "志愿者"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("助盲跑 - $roleText") },
                actions = {
                    // 个人中心入口
                    TextButton(onClick = onNavigateToProfile) {
                        Text("个人中心")
                    }
                    TextButton(onClick = {
                        RetrofitClient.setToken(null)
                        onNavigateToLogin()
                    }) {
                        Text("退出")
                    }
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
            Text(text = "欢迎回来", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "用户ID: $userId | 身份: $roleText",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("功能列表", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            if (userType == UserRepository.USER_TYPE_MANGREN) {
                // ---- 盲人功能入口 ----
                FeatureItem(title = "一键求助", subtitle = "实时匹配附近志愿者", onClick = onNavigateToQuickHelp)
                FeatureItem(title = "创建预约", subtitle = "提前预约陪跑时间地点", onClick = onNavigateToCreateReservation)
                FeatureItem(title = "查看预约", subtitle = "管理已有预约", onClick = onNavigateToReservationList)
                FeatureItem(title = "查看运动数据", subtitle = "跑步距离、配速、时长", onClick = onNavigateToRunData)
                FeatureItem(title = "评价志愿者", subtitle = "给陪跑志愿者打分", onClick = onNavigateToRateVolunteer)
            } else {
                // ---- 志愿者功能入口 ----
                FeatureItem(title = "查看预约列表", subtitle = "浏览盲人发出的预约请求", onClick = onNavigateToReservationList)
                FeatureItem(title = "查看运动数据", subtitle = "跑步距离、配速、时长", onClick = onNavigateToRunData)
            }
        }
    }
}

@Composable
private fun FeatureItem(title: String, subtitle: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
