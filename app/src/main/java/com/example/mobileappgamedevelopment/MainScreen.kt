package com.example.mobileappgamedevelopment

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
        val userId = Username.substringBefore("@")
        val email = Username
        var coins = 100
        LaunchedEffect(email) {
            viewModel.getUser(
                email,
                onSuccess = { userData ->
                    if (userData != null) {
                        coins = (userData["coins"] as? Int) ?: 100 // Update coins if data exists
                    } else {
                        viewModel.addUser(userId, email, coins, 0, 0 ,0) // Add user if not found
                    }
                },
                onFailure = { exception ->
                    println("Error retrieving user: ${exception.message}")
                }
            )
        }


        viewModel.currentUserId = Username

        OpengGLComposable(
            modifier = Modifier.
            fillMaxSize(),
            viewModel
        )


        val textInfoList by viewModel.textInfoList.observeAsState(mutableListOf())


        textInfoList.forEach { textInfo ->
            Column(modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)) {
                val density = LocalDensity.current
                val offsetX = with(density) { textInfo.offsetX }
                val offsetY = with(density) { textInfo.offsetY }

                Text(
                    text = textInfo.text,
                    fontSize = textInfo.fontSize,
                    color = textInfo.color,
                    modifier = Modifier
                        .absoluteOffset(x = offsetX, y = offsetY)
                        .padding(8.dp)
                )

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Increase space between columns
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomImageButton(
                    onClick = { navigationHelper.navigateToRanking() },
                    imageResId = R.drawable.rankingicon
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomImageButton(
                    onClick = { navigationHelper.navigateToCamera() },
                    imageResId = R.drawable.camera
                )
            }


        }
    }
}

@Composable
fun CustomImageButton(
    onClick: () -> Unit,
    imageResId: Int, // Pass the drawable resource ID
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp) // Square shape
            .shadow(8.dp, RoundedCornerShape(16.dp)) // Adds shadow
            .border(8.dp, Color(0xFFBC7A59), RoundedCornerShape(16.dp)), // Outline
        shape = RoundedCornerShape(16.dp), // Rounded corners
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEDC8)), // Optional: Set background color
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp) // Adds button elevation for shadow
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Button Icon",
            modifier = Modifier.size(64.dp)
        )
    }
}
