package com.mio.applicationwork.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mio.applicationwork.data.repository.UserRepository

/**
 * 登录页面
 *
 * 功能:
 * - 盲人/志愿者身份切换（FilterChip 二选一）
 * - 账号、密码输入（OutlinedTextField）
 * - 表单校验（非空检查）
 * - 错误提示（MaterialTheme.colorScheme.error 红色文字）
 * - 加载状态（Button 内显示 CircularProgressIndicator）
 * - 注册入口（底部 TextButton 跳转到注册页）
 *
 * 导航:
 * - onLoginSuccess: 登录成功后跳转到主页，同时清除回退栈中的登录页
 * - onNavigateToRegister: 点击"去注册"跳转到注册页
 */
@OptIn(ExperimentalMaterial3Api::class) // TopAppBar / FilterChip 等 Material3 实验性 API
@Composable
fun LoginScreen(
    onLoginSuccess: (userType: Int, userId: Int) -> Unit, // 登录成功回调
    onNavigateToRegister: () -> Unit,                     // 跳转注册页回调
    viewModel: LoginViewModel = viewModel()              // ViewModel，支持预览时注入
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听登录成功状态，触发导航（LaunchedEffect 保证仅执行一次）
    LaunchedEffect(uiState.loginSuccess) {
        uiState.loginSuccess?.let {
            onLoginSuccess(it.userType, it.userId)
            viewModel.clearLoginSuccess() // 清除状态，防止重复导航
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("登录") })
        }
    ) { padding ->
        // 居中排列的表单区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---- 身份切换: 盲人 / 志愿者 ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.userType == UserRepository.USER_TYPE_MANGREN,
                    onClick = { viewModel.updateUserType(UserRepository.USER_TYPE_MANGREN) },
                    label = { Text("盲人") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = uiState.userType == UserRepository.USER_TYPE_ZHIYUAN,
                    onClick = { viewModel.updateUserType(UserRepository.USER_TYPE_ZHIYUAN) },
                    label = { Text("志愿者") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- 账号输入 ----
            OutlinedTextField(
                value = uiState.zhangHao,
                onValueChange = { viewModel.updateZhangHao(it) },
                label = { Text("账号") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---- 密码输入（密文显示） ----
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // 密码遮罩
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- 错误提示区域（仅在有错误时显示） ----
            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- 登录按钮（加载中时禁用并显示转圈） ----
            Button(
                onClick = { viewModel.login() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("登录")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---- 跳转注册 ----
            TextButton(onClick = onNavigateToRegister) {
                Text("还没有账号？去注册")
            }
        }
    }
}
