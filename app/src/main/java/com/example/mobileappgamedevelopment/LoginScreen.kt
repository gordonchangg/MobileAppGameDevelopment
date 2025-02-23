package com.example.mobileappgamedevelopment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigationHelper: NavigationHelper
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    val coinyFont = FontFamily(
        Font(R.font.coiny)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.loginpage),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Adjusts image scaling
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .width(IntrinsicSize.Min)
                .align(Alignment.Center) // Start from center
                .offset(y = 150.dp) // Move it downward
        ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = coinyFont,
                    ),
                    color = Color(0xFF895349)
                )


            // Username Input Field
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFEF9E5),
                    unfocusedContainerColor = Color(0xFFFEF9E5)
                )
            )

            // Password Input Field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFEF9E5),
                    unfocusedContainerColor = Color(0xFFFEF9E5)
                )
            )

            // Error Message (if any)
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = coinyFont),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Login Button
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "All fields are required"
                    } else {
                        // Authenticate user with Firebase
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Login successful, navigate to Main screen
                                    navigationHelper.navigateToMain(email)
                                } else {
                                    // Handle errors (e.g., invalid credentials)
                                    errorMessage = task.exception?.localizedMessage ?: "Login failed"
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                        colors = ButtonColors(
                            Color(0xFFFEF9E5),
                            Color(0xFF895349),
                            Color(0xFFFEF9E5),
                            Color(0xFFFEF9E5))
            ) {
                Text(text = "Login", style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = coinyFont
                ))
            }

            // Register Button
            Button(
                onClick = {
                    navigationHelper.navigateToRegister()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(
                    Color(0xFFFEF9E5),
                    Color(0xFF895349),
                    Color(0xFFFEF9E5),
                    Color(0xFFFEF9E5))
            ) {
                Text(text = "Register", style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = coinyFont
                ))
            }
        }
    }
}