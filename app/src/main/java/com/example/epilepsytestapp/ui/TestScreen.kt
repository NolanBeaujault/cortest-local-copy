package com.example.epilepsytestapp.ui

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.example.epilepsytestapp.category.LocalCatManager
import com.example.epilepsytestapp.savefiles.saveTestInstructionsAsPDF
import com.example.epilepsytestapp.savefiles.startRecording
import com.example.epilepsytestapp.savefiles.stopRecording
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val instructions = remember { mutableStateListOf<String>() } // ✅ Liste mutable des instructions chargées
    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val currentInstruction = instructions.getOrNull(currentInstructionIndex)

    var isRecording by remember { mutableStateOf(false) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val recording = remember { mutableStateOf<Recording?>(null) }

    val instructionsLog = remember { mutableListOf<Pair<String, Int>>() }
    var elapsedTime by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            Log.d("TestScreen", "📂 Chargement des tests depuis le fichier local...")
            val localTests = LocalCatManager.loadLocalTests(context)
            val extractedInstructions = localTests.values.flatten().map { it.consigne }
            instructions.addAll(extractedInstructions)
            Log.d("TestScreen", "✅ Instructions chargées : $instructions")
        }
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000L)
            elapsedTime++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            videoCapture = videoCapture,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(1 / 3f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = currentInstruction ?: "Chargement...",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
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
                    instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))
                    saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                    navController.navigate("confirmation")
                }
            )

            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))
                        currentInstructionIndex++
                    } else {
                        if (isRecording) {
                            stopRecording(context, recording)
                            isRecording = false
                        }
                        instructionsLog.add(Pair(currentInstruction ?: "", elapsedTime))
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
            elapsedTime = 0
        }
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
                        Log.d("CameraPreview", "📷 Caméra initialisée avec succès")
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "❌ Erreur d'initialisation caméra", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}
