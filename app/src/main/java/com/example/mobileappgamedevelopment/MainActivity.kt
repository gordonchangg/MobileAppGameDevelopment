package com.example.mobileappgamedevelopment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileappgamedevelopment.ui.theme.MobileAppGameDevelopmentTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val firebaseApps = FirebaseApp.getApps(this)
        if (firebaseApps.isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        val viewModelFactory = MainViewModelFactory()
        setContent {
            MobileAppGameDevelopmentTheme {
                Surface(
                    modifier =Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel(factory = viewModelFactory)

                    viewModel.audioManager.setContext(this)

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Navigation host
                        NavHost(navController = navController, startDestination = Screen.Login.route) {
                            composable(Screen.Login.route) {
                                val navigationHelper = remember { NavigationHelper(navController) }
                                LoginScreen(navigationHelper = navigationHelper)
                            }
                            composable(Screen.Register.route) {
                                val navigationHelper = remember { NavigationHelper(navController) }
                                RegisterScreen(navigationHelper = navigationHelper)
                            }
                            composable(
                                Screen.Main.routePattern,
                                arguments = Screen.Main.arguments
                            ) { backStackEntry ->
                                val navigationHelper = remember { NavigationHelper(navController) }
                                val username = backStackEntry.arguments?.getString("username") ?: ""
                                MainScreen(
                                    navigationHelper = navigationHelper,
                                    Username = username,
                                    viewModel = viewModel
                                )
                            }
                            composable(Screen.Camera.route) {
                                val navigationHelper = remember { NavigationHelper(navController) }
                                CameraScreen(
                                    navigationHelper = navigationHelper,
                                    onImageCaptured = { photoFile ->
                                        viewModel.uploadImageToFirebase(photoFile) { downloadUrl ->
                                            if (downloadUrl != null) {
                                                println("Image uploaded successfully: $downloadUrl")
                                                // Use the download URL as needed (e.g., save it to Firestore or display it)
                                            } else {
                                                println("Failed to upload image")
                                            }
                                        }
                                    }
                                )
                            }
                            composable(Screen.Ranking.route){
                                val navigationHelper = remember { NavigationHelper(navController) }
                                RankingScreen(
                                    navigationHelper = navigationHelper,
                                    viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
