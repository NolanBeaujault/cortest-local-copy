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
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TestScreen(navController: NavHostController, recordedVideos: MutableList<String>, cameraViewModel: CameraViewModel) {

    val isFrontCamera by cameraViewModel.isFrontCamera

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val instructionsA = remember { mutableStateListOf<String>() }
    val instructionsH = remember { mutableStateListOf<String>() }
    var currentInstructionIndex by remember { mutableIntStateOf(0) }
    val currentInstruction = remember { mutableStateOf("Chargement...") }

    var isRecording by remember { mutableStateOf(false) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val recording = remember { mutableStateOf<Recording?>(null) }
    val videoFilePath = remember { mutableStateOf<String?>(null) }

    val instructionsLog = remember { mutableListOf<Pair<String, Int>>() }
    var elapsedTime by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val hasLoadedInstructions = remember { mutableStateOf(false) }

    var currentConsigne by remember {
        mutableStateOf("Aucune consigne")
    }

    // 📂 Chargement des consignes (une seule fois)
    LaunchedEffect(Unit) {
        if (!hasLoadedInstructions.value) {
            coroutineScope.launch {
                Log.d("TestScreen", "📂 Chargement des tests depuis le fichier local...")
                val localTests = LocalCatManager.loadLocalTests(context)

                localTests.values.flatten().forEach { test ->
                    test.consigneA?.let { instructionsA.add(it) }
                    test.consigneH?.let { instructionsH.add(it) }
                }

                currentConsigne = if (isFrontCamera) {
                    instructionsA.getOrNull(currentInstructionIndex) ?: "Aucune consigne"
                } else {
                    instructionsH.getOrNull(currentInstructionIndex) ?: "Aucune consigne"
                }
                currentInstruction.value = currentConsigne
                hasLoadedInstructions.value = true
                Log.d("TestScreen", "✅ Consigne initiale : ${currentInstruction.value}")
            }
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
            modifier = Modifier.fillMaxSize(),
            cameraViewModel = cameraViewModel
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

        // 🔹 Boutons de contrôle
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
                        stopRecording(context, recording, videoFilePath)
                        isRecording = false
                    }
                    instructionsLog.add(Pair(currentInstruction.value, elapsedTime))

                    val pdfFile = saveTestInstructionsAsPDF(context, instructionsLog, elapsedTime)
                    pdfFile?.let {
                        Log.d("TestScreen", "PDF généré avec succès : ${it.absolutePath}")
                    }

                    navController.navigate("confirmation")
                }
            )

            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < (if (isFrontCamera) instructionsA.size else instructionsH.size) - 1) {
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                        currentInstructionIndex++
                        currentConsigne = if (isFrontCamera) {
                            instructionsA.getOrNull(currentInstructionIndex) ?: "Consigne A"
                        } else {
                            instructionsH.getOrNull(currentInstructionIndex) ?: "Consigne H"
                        }
                        currentInstruction.value = currentConsigne
                    } else {
                        if (isRecording) {
                            stopRecording(context, recording, videoFilePath)
                            isRecording = false
                        }
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                        navController.navigate("confirmation")
                    }
                }
            )
        }

        // 🔄 Changer de caméra
        ImageClickable(
            imageResId = R.mipmap.ic_switch_camera_foreground,
            contentDescription = "Changer de caméra",
            onClick = {
                if (isRecording) {
                    stopRecording(context, recording, videoFilePath)
                    isRecording = false
                }

                val cameraLabel =
                    if (!cameraViewModel.isFrontCamera.value) "Caméra frontale" else "Caméra arrière"
                instructionsLog.add(Pair("Changement de caméra : $cameraLabel", elapsedTime))

                cameraViewModel.isFrontCamera.value = !cameraViewModel.isFrontCamera.value

                currentInstruction.value = if (cameraViewModel.isFrontCamera.value) {
                    instructionsA.getOrNull(currentInstructionIndex) ?: "Consigne A"
                } else {
                    instructionsH.getOrNull(currentInstructionIndex) ?: "Consigne H"
                }

                Log.d(
                    "TestScreen",
                    "Changement de caméra : isFrontCamera = ${cameraViewModel.isFrontCamera.value}"
                )

                videoCapture.value = null

                // 💡 Redémarre le chrono uniquement après la nouvelle caméra
                coroutineScope.launch {
                    delay(500) // attendre que la nouvelle vidéo démarre
                    elapsedTime = 0
                }
            },
                    modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopEnd)
                .size(80.dp)
        )

        // 🎥 Démarrer l'enregistrement automatiquement après changement de caméra
        LaunchedEffect(videoCapture.value, isFrontCamera) {
            if (!isRecording) {
                delay(300L)
                videoCapture.value?.let {
                    recording.value = startRecording(context, it, recording, videoFilePath, recordedVideos)
                    isRecording = true
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoCapture: MutableState<VideoCapture<Recorder>?>,
    modifier: Modifier = Modifier,
    cameraViewModel: CameraViewModel
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val isFrontCamera by cameraViewModel.isFrontCamera

    AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                bindCamera(this, context, lifecycleOwner, videoCapture, isFrontCamera, cameraProviderFuture)
            }
        },
        modifier = modifier,
        update = { previewView ->
            if (videoCapture.value == null) {
                bindCamera(previewView, context, lifecycleOwner, videoCapture, isFrontCamera, cameraProviderFuture)
                Log.d("CameraPreview", "📷 Caméra mise à jour : isFrontCamera = $isFrontCamera")
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
