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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

        OpengGLComposable(
            modifier = Modifier.
            fillMaxSize(),
            viewModel
        )
        Button(
            onClick = {
                navigationHelper.navigateToCamera()
            }
        ) {
            Text("Take Picture")
        }

//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            // Greeting Text
//            Text(
//                text = "Welcome, $Username!",
//                style = MaterialTheme.typography.headlineMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Additional Content (Optional)
//            Text(
//                text = "You are now logged in.",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//
//            OpengGLComposable(
//                modifier = Modifier.
//                fillMaxWidth().
//                height(300.dp)
//            )
//        }
    }
}