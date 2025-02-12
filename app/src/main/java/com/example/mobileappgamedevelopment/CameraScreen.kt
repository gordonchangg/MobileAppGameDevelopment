package com.example.mobileappgamedevelopment

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

                // Set up the image capture use case
                imageCapture = ImageCapture.Builder().build()

                // Select the back camera as default
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to the camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner as LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }

        // Camera preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    previewView = this
                }
            },
            modifier = modifier.fillMaxSize()
        )

//        // Capture button
//        Button(
//            onClick = {
//                imageCapture?.let { capture ->
//                    val photoFile = File.createTempFile("capture", ".jpg")
//                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//                    capture.takePicture(
//                        outputOptions,
//                        ContextCompat.getMainExecutor(context),
//                        object : ImageCapture.OnImageSavedCallback {
//                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                                onImageCaptured(photoFile)
//                            }
//
//                            override fun onError(exception: ImageCaptureException) {
//                                exception.printStackTrace()
//                            }
//                        }
//                    )
//                }
//            }
//        ) {
//            Text("Take Picture")
//        }
    } else {
        // Permission not granted, show a message and request permission
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
