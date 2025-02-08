package com.example.epilepsytestapp.ui

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import java.io.File

@Composable
fun TestScreen(navController: NavHostController, mediaRecorder: MediaRecorder) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Liste des consignes
    val instructions = listOf(
        "Regardez l'écran pendant 10 secondes.",
        "Fermez les yeux et détendez-vous.",
        "Suivez les instructions affichées à l'écran.",
        "Levez votre bras droit.",
        "Levez votre bras gauche."
    )

    // État pour suivre la consigne actuelle
    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val currentInstruction = instructions.getOrNull(currentInstructionIndex)

    // État pour suivre l'enregistrement
    var isRecording by remember { mutableStateOf(false) }

    // Lancement de l'enregistrement au démarrage de l'écran
    LaunchedEffect(Unit) {
        isRecording = startRecording(context, mediaRecorder)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            modifier = Modifier.fillMaxSize()
        )

        // Consignes affichées au centre de l'écran
        currentInstruction?.let {
            Text(
                text = it,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                maxLines = 2
            )
        }

        // Images cliquables en bas de l'écran
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image de croix pour arrêter le test
            ImageClickable(
                imageResId = R.mipmap.ic_close_foreground,
                contentDescription = "Arrêter le test",
                onClick = {
                    if (isRecording) {
                        isRecording = !stopRecordingWithoutStop(context, mediaRecorder)
                    }
                    navController.navigate("confirmation")
                }
            )

            // Image de flèche pour avancer
            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        currentInstructionIndex++
                    } else {
                        if (isRecording) {
                            isRecording = !stopRecordingWithoutStop(context, mediaRecorder)
                        }
                        navController.navigate("confirmation")
                    }
                }
            )
        }
    }
}

@Composable
fun ImageClickable(
    imageResId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        modifier = modifier
            .size(180.dp)
            .padding(6.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        val preview = androidx.camera.core.Preview.Builder().build()
                        preview.setSurfaceProvider(this.surfaceProvider)

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Erreur lors de l'initialisation de la caméra", e)
                    }
                }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}

fun startRecording(context: Context, mediaRecorder: MediaRecorder): Boolean {
    Log.d("TestScreen", "startRecording: Initializing recording")
    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) videosDirectory.mkdirs()

    val outputFile = File(videosDirectory, "test_screen_record_${System.currentTimeMillis()}.mp4")
    return try {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(outputFile.absolutePath)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoSize(1280, 720)
            setVideoFrameRate(30)
            setVideoEncodingBitRate(5 * 1024 * 1024) // ✅ Qualité améliorée
            prepare()
            start()
            Log.d("TestScreen", "Recording started at ${outputFile.absolutePath}")
        }
        true
    } catch (e: Exception) {
        Log.e("TestScreen", "startRecording: Exception caught", e)
        false
    }
}



fun stopRecordingWithoutStop(context: Context, mediaRecorder: MediaRecorder?): Boolean {
    return try {
        mediaRecorder?.apply {
            reset() // Réinitialise le MediaRecorder
            release() // Libère les ressources
            Log.d("TestScreen", "stopRecordingWithoutStop: MediaRecorder resources released successfully")
            Toast.makeText(context, "Recording stopped successfully", Toast.LENGTH_SHORT).show()
        }
        true
    } catch (e: Exception) {
        Log.e("TestScreen", "stopRecordingWithoutStop: Exception caught", e)
        Toast.makeText(context, "Failed to stop recording: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}
