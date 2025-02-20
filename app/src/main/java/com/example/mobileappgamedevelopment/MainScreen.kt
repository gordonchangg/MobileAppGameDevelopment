package com.example.mobileappgamedevelopment

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun MainScreen(navigationHelper: NavigationHelper, Username: String, viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter // Center the content vertically and horizontally
    ) {
        val userId = "user1"
        val email = Username
        var coins = 100
        LaunchedEffect(email) {
            viewModel.getUser(
                email,
                onSuccess = { userData ->
                    if (userData != null) {
                        coins = (userData["coins"] as? Int) ?: 100 // Update coins if data exists
                    } else {
                        viewModel.addUser(userId, email, coins) // Add user if not found
                    }
                },
                onFailure = { exception ->
                    println("Error retrieving user: ${exception.message}")
                }
            )
        }

        OpengGLComposable(
            modifier = Modifier.
            fillMaxSize(),
            viewModel
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between buttons
        ) {
            Button(
                onClick = {
                    navigationHelper.navigateToRanking()
                },
                modifier = Modifier.fillMaxWidth() // Make the button fill the width
            ) {
                Text("To Ranking Screen")
            }

            Button(
                onClick = {
                    navigationHelper.navigateToCamera()
                },
                modifier = Modifier.fillMaxWidth() // Make the button fill the width
            ) {
                Text("Take Picture")
            }
        }
    }
}