package com.example.epilepsytestapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import java.io.File
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Log.d("TestScreen", "Vérification des permissions")
    val hasPermissions = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    if (!hasPermissions) {
        Log.e("TestScreen", "Permissions manquantes : caméra et/ou microphone")
        Toast.makeText(context, "Permissions caméra et microphone requises", Toast.LENGTH_LONG).show()
        navController.navigate("home")
        return
    }

    val instructions = listOf(
        "Regardez l'écran pendant 10 secondes.",
        "Fermez les yeux et détendez-vous.",
        "Suivez les instructions affichées à l'écran.",
        "Levez votre bras droit.",
        "Levez votre bras gauche."
    )

    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val currentInstruction = instructions.getOrNull(currentInstructionIndex)
    var isRecording by remember { mutableStateOf(false) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val recording = remember { mutableStateOf<Recording?>(null) }
    val overlayView = remember { CustomOverlayView(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            videoCapture = videoCapture,
            modifier = Modifier.fillMaxSize()
        )

        AndroidView(
            factory = { overlayView },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(currentInstruction) {
            overlayView.setInstruction(currentInstruction ?: "")
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ImageClickable(
                imageResId = R.mipmap.ic_close_foreground,
                contentDescription = "Arrêter le test",
                onClick = {
                    if (isRecording) {
                        stopRecording(context, recording)
                        isRecording = false
                    }
                    navController.navigate("confirmation")
                }
            )

            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        currentInstructionIndex++
                        overlayView.setInstruction(instructions[currentInstructionIndex])
                    } else {
                        if (isRecording) {
                            stopRecording(context, recording)
                            isRecording = false
                        }
                        navController.navigate("confirmation")
                    }
                }
            )
        }
    }

    LaunchedEffect(videoCapture.value) {
        videoCapture.value?.let {
            Log.d("TestScreen", "Lancement de l'enregistrement vidéo")
            recording.value = startRecording(context, it)
            isRecording = true
        }
    }
}


class CustomOverlayView(context: Context) : View(context) {
    private var instructionText: String = ""
    private val paint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    fun setInstruction(text: String) {
        instructionText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(instructionText, width / 2f, height / 2f, paint)
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(this.surfaceProvider)
                        }

                        val recorder = Recorder.Builder()
                            .setQualitySelector(QualitySelector.from(Quality.HD))
                            .build()
                        val videoCaptureUseCase = VideoCapture.withOutput(recorder)

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, videoCaptureUseCase
                        )
                        videoCapture.value = videoCaptureUseCase
                        Log.d("CameraPreview", "Caméra initialisée avec succès")
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Erreur lors de l'initialisation de la caméra", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}

fun startRecording(context: Context, videoCapture: VideoCapture<Recorder>): Recording? {
    Log.d("TestScreen", "Démarrage de l'enregistrement")

    val hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    if (!hasPermissions) {
        Log.e("TestScreen", "Permissions insuffisantes pour enregistrer")
        Toast.makeText(context, "Permissions non accordées", Toast.LENGTH_LONG).show()
        return null
    }

    val videosDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    if (!videosDirectory.exists()) videosDirectory.mkdirs()

    val outputFile = File(videosDirectory, "test_screen_record_${System.currentTimeMillis()}.mp4")
    val outputOptions = FileOutputOptions.Builder(outputFile).build()

    return videoCapture.output.prepareRecording(context, outputOptions)
        .withAudioEnabled()
        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    Log.d("TestScreen", "Enregistrement démarré")
                    Toast.makeText(context, "Enregistrement en cours", Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    Log.d("TestScreen", "Enregistrement terminé : ${outputFile.absolutePath}")
                    Toast.makeText(context, "Vidéo enregistrée", Toast.LENGTH_SHORT).show()
                }
            }
        }
}



fun stopRecording(context: Context, recording: MutableState<Recording?>) {
    try {
        recording.value?.stop()
        recording.value = null
        Log.d("TestScreen", "Enregistrement arrêté avec succès")
        Toast.makeText(context, "Enregistrement arrêté", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("TestScreen", "Erreur lors de l'arrêt de l'enregistrement", e)
        Toast.makeText(context, "Erreur lors de l'arrêt de l'enregistrement", Toast.LENGTH_SHORT).show()
    }
}
