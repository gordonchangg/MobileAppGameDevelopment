package com.example.mobileappgamedevelopment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun RankingScreen(
    navigationHelper: NavigationHelper,
    viewModel: MainViewModel
) {
    var users by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers(
            onSuccess = { userList ->
                users = userList
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = "Failed to load users: ${exception.message}"
                isLoading = false
            }
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserRow(user = user)
            }
        }
    }
}

@Composable
fun UserRow(user: Map<String, Any?>) {
    val userId = user["id"] as? String ?: "Unknown ID"
    val email = user["email"] as? String ?: "Unknown Email"
    val coins = user["coins"] as? Long ?: 0
    val userImageUrl = user["imageUrl"] as? String

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userImageUrl != null) {
            AsyncImage(
                model = userImageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape), // Circular shape for the profile image
                contentScale = ContentScale.Crop // Crop the image to fit the circle
            )
        }else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray, CircleShape), // Placeholder for missing image
                contentAlignment = Alignment.Center
            ) {
                Text("?", fontSize = 24.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "User ID: $userId", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Email: $email", fontSize = 14.sp)
        }
        Text(
            text = "$coins Coins",
            fontSize = 16.sp,
            color = Color.Green,
            fontWeight = FontWeight.Bold
        )
    }
}