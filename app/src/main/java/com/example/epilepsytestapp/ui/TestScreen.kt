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
import com.example.epilepsytestapp.savefiles.mergeVideos
import com.example.epilepsytestapp.savefiles.saveTestInstructionsAsPDF
import com.example.epilepsytestapp.savefiles.startRecording
import com.example.epilepsytestapp.savefiles.stopRecording
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TestScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val instructionsA = remember { mutableStateListOf<String>() }
    val instructionsH = remember { mutableStateListOf<String>() }
    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val currentInstruction = remember { mutableStateOf<String>("") }

    var isRecording by remember { mutableStateOf(false) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val recording = remember { mutableStateOf<Recording?>(null) }

    val recordedVideos = remember { mutableStateListOf<String>() } // ✅ Liste des vidéos enregistrées
    val instructionsLog = remember { mutableListOf<Pair<String, Int>>() }
    var elapsedTime by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    var isFrontCamera by remember { mutableStateOf(true) }

    val currentConsigne = if (isFrontCamera) {
        instructionsA.getOrNull(currentInstructionIndex) ?: "Consigne A"
    } else {
        instructionsH.getOrNull(currentInstructionIndex) ?: "Consigne H"
    }

    // 📂 Charger les tests et instructions
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            Log.d("TestScreen", "📂 Chargement des tests depuis le fichier local...")
            val localTests = LocalCatManager.loadLocalTests(context)
            localTests.values.flatten().forEach { test ->
                test.consigneA.let { instructionsA.add(it) }
                test.consigneH.let { instructionsH.add(it) }
            }
            currentInstruction.value = currentConsigne
        }
    }

    // ⏳ Gestion du timer
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
            isFrontCamera = isFrontCamera,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(1 / 3f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = currentInstruction.value,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // 🔹 Bouton pour arrêter le test et fusionner les vidéos
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
                        val videoPath = stopRecording(context, recording)
                        videoPath.let { recordedVideos.add(it.toString()) }
                        isRecording = false
                    }
                    instructionsLog.add(Pair(currentInstruction.value, elapsedTime))

                    // ✅ Fusionner les vidéos enregistrées
                    if (recordedVideos.isNotEmpty()) {
                        val mergedVideoPath = mergeVideos(context, recordedVideos)
                        Log.d("TestScreen", "Vidéo fusionnée : $mergedVideoPath")
                    }

                    saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                    navController.navigate("confirmation")
                }
            )

            // 🔹 Bouton "Instruction suivante"
            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < (if (isFrontCamera) instructionsA.size else instructionsH.size) - 1) {
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                        currentInstructionIndex++
                        currentInstruction.value = if (isFrontCamera) {
                            instructionsA.getOrNull(currentInstructionIndex) ?: "Consigne A"
                        } else {
                            instructionsH.getOrNull(currentInstructionIndex) ?: "Consigne H"
                        }
                    } else {
                        if (isRecording) {
                            stopRecording(context, recording)
                            isRecording = false
                        }
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                        saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                        navController.navigate("confirmation")
                    }
                }
            )
        }

        // 🔹 Bouton "Switch caméra" en haut à droite
        ImageClickable(
            imageResId = R.mipmap.ic_switch_camera_foreground,
            contentDescription = "Changer de caméra",
            onClick = {
                if (isRecording) {
                    val videoPath = stopRecording(context, recording)
                    videoPath.let { recordedVideos.add(it.toString()) }
                    isRecording = false
                }
                isFrontCamera = !isFrontCamera
                currentInstruction.value = if (isFrontCamera) {
                    instructionsA.getOrNull(currentInstructionIndex) ?: "Consigne A"
                } else {
                    instructionsH.getOrNull(currentInstructionIndex) ?: "Consigne H"
                }
                Log.d("TestScreen", "Changement de caméra : isFrontCamera = $isFrontCamera")
                videoCapture.value = null
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        )
    }

    // 🔹 Démarrer l'enregistrement après le changement de caméra
    LaunchedEffect(videoCapture.value, isFrontCamera) {
        videoCapture.value?.let {
            recording.value = startRecording(context, it, recording)
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
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val currentCameraSelector = rememberUpdatedState(newValue = isFrontCamera)

    AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                bindCamera(this, context, lifecycleOwner, videoCapture, currentCameraSelector.value, cameraProviderFuture)
            }
        },
        modifier = modifier,
        update = { previewView ->
            // Seule condition où on met à jour la caméra
            if (currentCameraSelector.value != isFrontCamera || videoCapture.value == null) {
                bindCamera(previewView, context, lifecycleOwner, videoCapture, isFrontCamera, cameraProviderFuture)
                Log.d("CameraPreview", "Changement de caméra ok")
            }
        }
    )
}

fun bindCamera(
    previewView: androidx.camera.view.PreviewView,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    isFrontCamera: Boolean,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            val videoCaptureUseCase = VideoCapture.withOutput(recorder)

            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, videoCaptureUseCase
            )

            videoCapture.value = videoCaptureUseCase
        } catch (e: Exception) {
            Log.e("CameraPreview", "Error initializing camera: ", e)
        }
    }, ContextCompat.getMainExecutor(context))
}