package com.example.mobileappgamedevelopment

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.datastore.core.IOException
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navigationHelper: NavigationHelper,
    modifier: Modifier = Modifier,
    onImageCaptured: (File) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Request camera permission
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        // Permission is granted, set up the camera
        LaunchedEffect(Unit) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                // Set up the preview use case
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView?.surfaceProvider)
                }
                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        previewView = this
                    }
                },
                modifier = modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Circular red "Take Picture" button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .clickable {
                            imageCapture?.let { capture ->
                                val photoFile = File.createTempFile("capture", ".jpg")
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                                capture.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                            // Provide feedback to the user
                                            Toast.makeText(context, "Picture saved!", Toast.LENGTH_SHORT).show()
                                            onImageCaptured(photoFile)
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            exception.printStackTrace()
                                            // Optionally show an error message to the user
                                            Toast.makeText(context, "Failed to take picture.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Take Picture",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    } else {
        // Permission not granted, show a prompt to request permission
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission is required to use this feature.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

@Throws(IOException::class)
private fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_", ".jpg", storageDir)
}
