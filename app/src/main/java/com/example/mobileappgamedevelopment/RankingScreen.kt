package com.example.mobileappgamedevelopment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.ranking),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Adjusts image scaling
        )

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .offset(0.dp, 200.dp)
                .height(580.dp)

        ) {
            itemsIndexed(users.sortedByDescending { (it["coins"] as? Long) ?: 0 }) { index, user ->
                UserRow(user = user, index = index,  viewModel = viewModel)
            }
        }
    }
}

@Composable
fun UserRow(user: Map<String, Any?>, index: Int, viewModel: MainViewModel) {
    val email = user["email"] as? String ?: "Unknown Email"
    val userId = email.substringBefore("@", "Unknown ID")
    val coins = user["coins"] as? Long ?: 0
    val userImageUrl = user["imageUrl"] as? String

    val imageSize = when (index) {
        0 -> 80.dp  // Biggest for 1st place
        1 -> 70.dp  // Bigger for 2nd place
        2 -> 60.dp  // Big for 3rd place
      else -> 50.dp // Default for the rest
    }
    val textSize = when (index) {
        0 -> 22.sp  // Biggest text for 1st place
        1 -> 20.sp  // Bigger text for 2nd place
        2 -> 18.sp  // Big text for 3rd place
        else -> 16.sp // Default text size
    }

    val numberColor = when (index) {
        0 -> Color(0xFFD4AF37) // Gold for 1st place
        1 -> Color(0xFFC0C0C0) // Silver for 2nd place
        2 -> Color(0xFFCD7F32) // Bronze for 3rd place
        else -> Color.Black // Default color for others
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(imageSize + 10.dp),
            //.clip(RoundedCornerShape(20.dp))
            //.border(3.dp, Color(0xFFF4D698), RoundedCornerShape(50.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "${index + 1}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = numberColor,
            modifier = Modifier.padding(end = 16.dp)

        )
        if (userImageUrl != null) {
            AsyncImage(
                model = userImageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape), // Circular shape for the profile image
                contentScale = ContentScale.Crop // Crop the image to fit the circle
            )
        }else {
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .background(Color.Gray, CircleShape), // Placeholder for missing image
                contentAlignment = Alignment.Center
            ) {
                Text("?", fontSize = textSize, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "$userId", fontSize = textSize, fontWeight = FontWeight.Bold,
                    color = Color.Black,
            )
        }
        Text(
            text = "$coins",
            fontSize = textSize,
            color = Color(0xFF895349),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "Coin",
            modifier = Modifier.size(25.dp)
        )    }
}