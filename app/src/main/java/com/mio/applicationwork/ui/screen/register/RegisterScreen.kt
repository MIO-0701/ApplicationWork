package com.mio.applicationwork.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
 * 注册页面
 *
 * 表单字段（盲人）:
 * - 姓名、账号、密码、性别、配速、公里数
 *
 * 志愿者额外字段:
 * - 认证信息（renZhen）
 *
 * 布局: 使用 verticalScroll 包裹，防止小屏设备时内容溢出
 *
 * 导航:
 * - onRegisterSuccess: 注册成功后返回登录页
 * - onNavigateBack: 点击返回按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,      // 注册成功回调（返回登录页）
    onNavigateBack: () -> Unit,         // 返回按钮回调
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 注册成功时自动返回
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onRegisterSuccess()
            viewModel.clearRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("注册") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("返回") }
                }
            )
        }
    ) { padding ->
        // 可滚动的表单区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---- 身份选择（盲人 / 志愿者） ----
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

            Spacer(modifier = Modifier.height(20.dp))

            // ---- 姓名 ----
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("姓名") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ---- 账号 ----
            OutlinedTextField(
                value = uiState.zhangHao,
                onValueChange = { viewModel.updateZhangHao(it) },
                label = { Text("账号") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ---- 密码 ----
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- 志愿者专有字段: 认证信息 ----
            if (uiState.userType == UserRepository.USER_TYPE_ZHIYUAN) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = uiState.renZhen,
                    onValueChange = { viewModel.updateRenZhen(it) },
                    label = { Text("认证信息") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---- 性别选择 ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = uiState.sex == 1,
                    onClick = { viewModel.updateSex(1) },
                    label = { Text("男") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = uiState.sex == 0,
                    onClick = { viewModel.updateSex(0) },
                    label = { Text("女") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---- 配速（数字键盘） ----
            OutlinedTextField(
                value = uiState.suDu,
                onValueChange = { viewModel.updateSuDu(it) },
                label = { Text("配速 (min/km)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ---- 公里数（数字键盘） ----
            OutlinedTextField(
                value = uiState.gongLi,
                onValueChange = { viewModel.updateGongLi(it) },
                label = { Text("公里数") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- 错误提示 ----
            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---- 注册按钮 ----
            Button(
                onClick = { viewModel.register() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("注册")
                }
            }

            // 底部留白，确保键盘弹起时内容不被遮挡
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
