package com.example.marsphotos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
    return image
}


@Composable
fun CameraCompose(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    var imageCapture: ImageCapture? = remember { null }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {

        Column {
            Box(modifier = Modifier.weight(0.25f)) {
                // Visualização da câmera
                CameraPreviewView(
                    imageCapture = { imageCapture = it },
                    modifier = Modifier // Ajusta a altura da visualização da câmera
                )

                // Botão alinhado no topo da imagem, centralizado
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    Button(
                        onClick = {
                            imageCapture?.let { capture ->
                                val outputOptions =
                                    ImageCapture.OutputFileOptions.Builder(file).build()

                                capture.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            capturedImageUri = uri
                                            Toast.makeText(
                                                context,
                                                "Photo captured!",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            Log.d(
                                                "Gravado",
                                                "Foto gravada: $capturedImageUri e no $outputFileResults"
                                            )
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e(
                                                "CameraCompose",
                                                "Image capture failed: ${exception.message}",
                                                exception
                                            )
                                        }
                                    }
                                )
                            }
                        },
                    ) {
                        Text("Capture")  // Texto dentro do botão
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier.size(80.dp),
    imageCapture: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = androidx.camera.view.PreviewView(ctx)

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageCaptureInstance = ImageCapture.Builder().build()
            imageCapture(imageCaptureInstance)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    ctx as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCaptureInstance
                )
            } catch (e: Exception) {
                Log.e("CameraPreviewView", "Failed to bind camera use cases", e)
            }

            previewView
        },
        modifier = modifier
    )
}
