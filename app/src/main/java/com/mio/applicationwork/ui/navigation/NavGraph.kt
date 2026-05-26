package com.mio.applicationwork.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mio.applicationwork.ui.screen.Screen
import com.mio.applicationwork.ui.screen.createreservation.CreateReservationScreen
import com.mio.applicationwork.ui.screen.editprofile.EditProfileScreen
import com.mio.applicationwork.ui.screen.home.HomeScreen
import com.mio.applicationwork.ui.screen.login.LoginScreen
import com.mio.applicationwork.ui.screen.profile.ProfileScreen
import com.mio.applicationwork.ui.screen.quickhelp.QuickHelpScreen
import com.mio.applicationwork.ui.screen.ratevolunteer.RateVolunteerScreen
import com.mio.applicationwork.ui.screen.register.RegisterScreen
import com.mio.applicationwork.ui.screen.changepassword.ChangePasswordScreen
import com.mio.applicationwork.ui.screen.reservationlist.ReservationListScreen
import com.mio.applicationwork.ui.screen.rundata.RunDataScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // ==================== 登录页 ====================
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { userType, userId, _ ->
                    navController.navigate(Screen.Home.createRoute(userType, userId)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // ==================== 注册页 ====================
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 主页 ====================
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            HomeScreen(
                userType = userType,
                userId = userId,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.createRoute(userType, userId))
                },
                onNavigateToCreateReservation = {
                    navController.navigate(Screen.CreateReservation.createRoute(userId))
                },
                onNavigateToReservationList = {
                    navController.navigate(Screen.ReservationList.createRoute(userType, userId))
                },
                onNavigateToRunData = {
                    navController.navigate(Screen.RunData.createRoute(userType, userId))
                },
                onNavigateToRateVolunteer = {
                    navController.navigate(Screen.RateVolunteer.createRoute(userId))
                },
                onNavigateToQuickHelp = {
                    navController.navigate(Screen.QuickHelp.createRoute(userType, userId))
                }
            )
        }

        // ==================== 个人中心 ====================
        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ProfileScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate(Screen.EditProfile.createRoute(userType, userId))
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.createRoute(userType, userId))
                }
            )
        }

        // ==================== 修改信息 ====================
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            EditProfileScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 修改密码 ====================
        composable(
            route = Screen.ChangePassword.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ChangePasswordScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 创建预约（盲人） ====================
        composable(
            route = Screen.CreateReservation.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            CreateReservationScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 预约列表 ====================
        composable(
            route = Screen.ReservationList.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ReservationListScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 运动数据 ====================
        composable(
            route = Screen.RunData.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            RunDataScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 评价志愿者（盲人） ====================
        composable(
            route = Screen.RateVolunteer.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            RateVolunteerScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== 一键求助 / 实时匹配 ====================
        composable(
            route = Screen.QuickHelp.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getInt("userType") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            QuickHelpScreen(
                userType = userType,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
