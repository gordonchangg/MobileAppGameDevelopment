package com.example.mobileappgamedevelopment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

import androidx.compose.ui.unit.dp

data class TextInfo(
    var text: String,
    var fontSize: TextUnit = 24.sp,
    var color: Color = Color.Black,
    var offsetX: Dp = 0.dp,
    var offsetY: Dp = 0.dp

)