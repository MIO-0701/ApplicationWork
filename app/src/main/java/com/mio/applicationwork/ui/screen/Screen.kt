package com.mio.applicationwork.ui.screen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home/{userType}/{userId}") {
        fun createRoute(userType: Int, userId: Int) = "home/$userType/$userId"
    }
    data object Profile : Screen("profile/{userType}/{userId}") {
        fun createRoute(userType: Int, userId: Int) = "profile/$userType/$userId"
    }
    data object EditProfile : Screen("edit_profile/{userType}/{userId}") {
        fun createRoute(userType: Int, userId: Int) = "edit_profile/$userType/$userId"
    }
    data object CreateReservation : Screen("create_reservation/{userId}") {
        fun createRoute(userId: Int) = "create_reservation/$userId"
    }
    data object ReservationList : Screen("reservation_list/{userType}/{userId}") {
        fun createRoute(userType: Int, userId: Int) = "reservation_list/$userType/$userId"
    }
    data object RunData : Screen("run_data/{userType}/{userId}") {
        fun createRoute(userType: Int, userId: Int) = "run_data/$userType/$userId"
    }
    data object RateVolunteer : Screen("rate_volunteer/{userId}") {
        fun createRoute(userId: Int) = "rate_volunteer/$userId"
    }
    data object ChangePassword : Screen("change_password")
    data object QuickHelp : Screen("quick_help/{userId}") {
        fun createRoute(userId: Int) = "quick_help/$userId"
    }
}
