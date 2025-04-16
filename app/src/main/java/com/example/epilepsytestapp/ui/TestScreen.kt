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
import com.example.epilepsytestapp.category.Test
import com.example.epilepsytestapp.category.TestDisplay
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

    val tests = remember { mutableStateListOf<Test>() }

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

    val currentTest = tests.getOrNull(currentInstructionIndex)


    // 📂 Chargement des consignes (une seule fois)
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            Log.d("TestScreen", "📂 Chargement des tests depuis le fichier local...")
            val localTests = LocalCatManager.loadLocalTests(context)
            tests.addAll(localTests.values.flatten())
            val consigne = if (isFrontCamera) currentTest?.consigneA else currentTest?.consigneH
            currentInstruction.value = consigne ?: "Aucune consigne"
            Log.d("TestScreen", "✅ Consigne initiale : ${currentInstruction.value}")
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(bottom = 32.dp) // Marge en haut pour éviter la barre de statut
                ) {
                    currentTest?.let {
                        // Passage de currentInstructionIndex en paramètre à TestDisplay
                        TestDisplay(
                            test = it,
                            isFrontCamera = isFrontCamera,
                            key = currentInstructionIndex // Utilisation de currentInstructionIndex comme clé
                        ) }
                }
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
                    currentTest?.let {
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                        }

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
                    currentTest?.let {
                        instructionsLog.add(Pair(currentInstruction.value, elapsedTime))
                    }
                    if (currentInstructionIndex < tests.size - 1) {
                        currentInstructionIndex++
                        val consigne = if (isFrontCamera) currentTest?.consigneA else currentTest?.consigneH
                        currentInstruction.value = consigne ?: "Aucune consigne"
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
                val consigne = if (cameraViewModel.isFrontCamera.value) {
                    currentTest?.consigneA
                } else {
                    currentTest?.consigneH
                }
                currentInstruction.value = consigne ?: "Aucune consigne"
                Log.d("TestScreen", "🎥 Changement de caméra : ${cameraViewModel.isFrontCamera.value}")
                videoCapture.value = null

                // 💡 Redémarre le chrono uniquement après la nouvelle caméra
                coroutineScope.launch {
                    delay(500) // attendre que la nouvelle vidéo démarre
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
