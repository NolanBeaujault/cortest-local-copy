package com.example.epilepsytestapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.savefiles.saveTestInstructionsAsPDF
import com.example.epilepsytestapp.savefiles.startRecording
import com.example.epilepsytestapp.savefiles.stopRecording
import kotlinx.coroutines.delay

@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    // Liste pour sauvegarder les consignes et le temps écoulé
    val instructionsLog = remember { mutableListOf<Pair<String, Int>>() }

    // Timer pour suivre le temps écoulé, initialisé à 0 au début
    var elapsedTime by remember { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000L) // Mise à jour toutes les secondes
            elapsedTime++  // Le temps s'incrémente chaque seconde
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            videoCapture = videoCapture,
            modifier = Modifier.fillMaxSize()
        )

        // Instructions affichées à l'écran
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Instructions affichées aux 2/3 de l'écran
            Box(
                modifier = Modifier
                    .fillMaxHeight(1 / 3f) // Place le texte à 2/3 de la hauteur de l'écran
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp) // Ajuste l'écart si nécessaire
            ) {
                Text(
                    text = currentInstruction ?: "",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp), // Utilise la police CandaraBold
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(horizontal = 16.dp) // Ajuste le padding horizontal si besoin
                )
            }
        }

        // Boutons en bas
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
                    // Ajout de la dernière consigne et du temps avant de quitter
                    instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))

                    // Sauvegarde du PDF des consignes avant de quitter
                    saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                    navController.navigate("confirmation")
                }
            )

            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        // Ajout de la consigne et du temps à la liste
                        instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))

                        currentInstructionIndex++
                    } else {
                        if (isRecording) {
                            stopRecording(context, recording)
                            isRecording = false
                        }
                        // Ajout de la dernière consigne et du temps avant de quitter
                        instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))

                        // Sauvegarde du PDF des consignes avant de quitter
                        saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                        navController.navigate("confirmation")
                    }
                }
            )
        }
    }

    LaunchedEffect(videoCapture.value) {
        videoCapture.value?.let {
            recording.value = startRecording(context, it)
            isRecording = true
            elapsedTime = 0 // Réinitialise le timer au début de l'enregistrement
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

