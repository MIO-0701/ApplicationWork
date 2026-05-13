package com.mio.applicationwork.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mio.applicationwork.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 进入页面时自动加载
    LaunchedEffect(Unit) {
        viewModel.loadUser(userType, userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人中心") },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("返回") } }
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            } else {
                // ---- 基本信息 ----
                InfoSection(title = "基本信息") {
                    InfoRow("姓名", uiState.name)
                    InfoRow("性别", if (uiState.sex == 1) "男" else "女")
                    InfoRow("注册时间", uiState.createTime)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---- 跑步参数 ----
                InfoSection(title = "跑步参数") {
                    InfoRow("配速", "${uiState.suDu} min/km")
                    InfoRow("公里数", "${uiState.gongLi} km")
                }

                // ---- 志愿者专有信息 ----
                if (userType == UserRepository.USER_TYPE_ZHIYUAN) {
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoSection(title = "志愿者信息") {
                        InfoRow("评分", "${uiState.pingFen} 分")
                        InfoRow("认证号", uiState.renZhen.ifBlank { "未填写" })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- 修改信息按钮 ----
                Button(
                    onClick = onNavigateToEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("修改信息")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---- 修改密码按钮 ----
                OutlinedButton(
                    onClick = onNavigateToChangePassword,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("修改密码")
                }
            }
        }
    }
}

/** 信息分组卡片 */
@Composable
private fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

/** 单行信息：标签 + 值 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}
