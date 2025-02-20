package com.example.mobileappgamedevelopment

import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun UIOverlay(viewModel: MainViewModel) {
    var dynamicText by remember { mutableStateOf("Hello") }
    var showText by remember { mutableStateOf(true) }
    var textPosition by remember { mutableStateOf(Offset.Zero) }

    if (showText) {
        Text(
            text = dynamicText,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier
                .offset { IntOffset(textPosition.x.toInt(), textPosition.y.toInt()) }
                .background(Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        )
    }
}