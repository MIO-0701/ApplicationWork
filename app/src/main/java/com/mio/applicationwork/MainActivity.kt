package com.mio.applicationwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mio.applicationwork.ui.navigation.NavGraph
import com.mio.applicationwork.ui.theme.ApplicationWorkTheme

/**
 * 应用主入口 Activity
 *
 * 架构说明:
 * - 采用 Jetpack Compose 声明式 UI（非传统 XML 布局）
 * - enableEdgeToEdge() 实现全面屏/边缘到边缘显示
 * - setContent 内使用 ApplicationWorkTheme 主题包裹导航图
 * - NavGraph 管理所有页面路由与跳转逻辑
 *
 * 页面栈:
 * MainActivity → NavGraph → LoginScreen / RegisterScreen / HomeScreen
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用边到边显示（状态栏/导航栏透明叠加）
        enableEdgeToEdge()
        setContent {
            // 应用主题（颜色/字体/形状等 Material3 配置定义在 ui/theme/ 下）
            ApplicationWorkTheme {
                // 导航控制器，管理页面跳转和回退栈
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
