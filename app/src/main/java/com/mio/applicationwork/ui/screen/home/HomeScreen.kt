package com.mio.applicationwork.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mio.applicationwork.data.local.SessionManager
import com.mio.applicationwork.data.repository.UserRepository

/**
 * 应用主页
 *
 * 布局（从上到下）:
 * - TopAppBar: 标题 "助盲跑 - 盲人/志愿者" + "个人中心" + "退出"
 * - 圆形头像（姓名首字，蓝色底白色字，纯前端实现无需后端）
 * - "欢迎回来" + "姓名 | 身份: 盲人/志愿者"
 * - 功能列表（根据 userType 展示不同入口）
 *
 * 免登录:
 * - 姓名从 SessionManager 读取，空时兜底 "用户ID"
 * - 退出时调用 SessionManager.clearSession() 清除持久化数据
 */
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
    // 从本地免登录会话读取姓名，空时兜底显示 "用户ID"
    val userName = SessionManager.getUserName().ifBlank { "用户$userId" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("助盲跑 - $roleText") },
                actions = {
                    TextButton(onClick = onNavigateToProfile) { Text("个人中心") }
                    TextButton(onClick = {
                        SessionManager.clearSession()   // 清除免登录会话
                        onNavigateToLogin()
                    }) { Text("退出") }
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
            Spacer(modifier = Modifier.height(16.dp))

            // ── 头像: 圆形 + 首字，纯前端实现 ──
            // 如需支持自定义图片头像，需后端提供 avatarUrl 字段 + Coil 图片加载
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1),                // 取姓名第一个字
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "欢迎回来", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$userName | 身份: $roleText",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("功能列表", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // ── 按角色展示不同功能入口 ──
            if (userType == UserRepository.USER_TYPE_MANGREN) {
                FeatureItem(title = "一键求助", subtitle = "实时匹配附近志愿者", onClick = onNavigateToQuickHelp)
                FeatureItem(title = "创建预约", subtitle = "提前预约陪跑时间地点", onClick = onNavigateToCreateReservation)
                FeatureItem(title = "查看预约", subtitle = "管理已有预约", onClick = onNavigateToReservationList)
                FeatureItem(title = "查看运动数据", subtitle = "跑步距离、配速、时长", onClick = onNavigateToRunData)
                FeatureItem(title = "评价志愿者", subtitle = "给陪跑志愿者打分", onClick = onNavigateToRateVolunteer)
            } else {
                FeatureItem(title = "一键接单", subtitle = "实时匹配附近的盲人跑友", onClick = onNavigateToQuickHelp)
                FeatureItem(title = "查看预约列表", subtitle = "浏览盲人发出的预约请求", onClick = onNavigateToReservationList)
                FeatureItem(title = "查看运动数据", subtitle = "跑步距离、配速、时长", onClick = onNavigateToRunData)
            }
        }
    }
}

/** 首页功能卡片: 标题 + 副标题 + 点击跳转 */
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
