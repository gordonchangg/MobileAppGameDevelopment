package com.example.mobileappgamedevelopment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileappgamedevelopment.ui.theme.MobileAppGameDevelopmentTheme
import com.google.firebase.FirebaseApp
import kotlinx.serialization.Serializable



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val firebaseApps = FirebaseApp.getApps(this)
        if (firebaseApps.isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        setContent {
            val navController = rememberNavController()

            MobileAppGameDevelopmentTheme {
                Surface(
                    modifier =Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = Screen.Login.route) {
                        composable(Screen.Login.route) {
                            val navigationHelper = remember { NavigationHelper(navController) }
                            LoginScreen(
                                navigationHelper = navigationHelper
                            )
                        }
                        composable(Screen.Register.route) {
                            val navigationHelper = remember { NavigationHelper(navController) }
                            RegisterScreen(
                                navigationHelper = navigationHelper
                            )
                        }
                        composable(
                            Screen.Main.routePattern,
                            arguments = Screen.Main.arguments
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            MainScreen(Username = username)
                        }
                    }
                }
            }
        }
    }
}
