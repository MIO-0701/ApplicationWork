package com.mio.applicationwork.ui.screen.editprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mio.applicationwork.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userType: Int,
    userId: Int,
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 首次进入时从 API 加载当前信息并回填表单
    LaunchedEffect(Unit) {
        viewModel.loadAndInit(userType, userId)
    }

    // 保存成功后返回个人中心
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            onNavigateBack()
            viewModel.clearUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("修改信息") },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("返回") } }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ---- 姓名 ----
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("姓名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ---- 性别 ----
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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

                Spacer(modifier = Modifier.height(12.dp))

                // ---- 配速 ----
                OutlinedTextField(
                    value = uiState.suDu,
                    onValueChange = { viewModel.updateSuDu(it) },
                    label = { Text("配速 (min/km)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ---- 公里数 ----
                OutlinedTextField(
                    value = uiState.gongLi,
                    onValueChange = { viewModel.updateGongLi(it) },
                    label = { Text("公里数") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- 志愿者认证号 ----
                if (userType == UserRepository.USER_TYPE_ZHIYUAN) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.renZhen,
                        onValueChange = { viewModel.updateRenZhen(it) },
                        label = { Text("认证号") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ---- 错误提示 ----
                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ---- 保存按钮 ----
                Button(
                    onClick = { viewModel.submit(userId) },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("保存修改")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
