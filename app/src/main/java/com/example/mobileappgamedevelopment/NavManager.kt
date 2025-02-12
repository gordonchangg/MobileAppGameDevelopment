package com.example.mobileappgamedevelopment

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login : Screen("Login")
    object Register : Screen("Register")
    data class Main(val username: String) : Screen("Main/${username}") {
        companion object {
            const val routePattern = "Main/{username}"
            val arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        }
    }
    object Camera : Screen("Camera")
}

class NavigationHelper(private val navController: NavController) {
    fun navigateToLogin() {
        navController.navigate(Screen.Login.route)
    }

    fun navigateToRegister() {
        navController.navigate(Screen.Register.route)
    }

    fun navigateToMain(username: String) {
        navController.navigate(Screen.Main(username).route)
    }

    fun navigateToCamera(){
        navController.navigate(Screen.Camera.route)
    }
}