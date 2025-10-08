package com.example.epilepsytestapp.ui

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.category.LocalCatManager
import com.example.epilepsytestapp.savefiles.startRecording
import com.example.epilepsytestapp.savefiles.stopRecording
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.epilepsytestapp.category.Test
import com.example.epilepsytestapp.category.TestDisplay

@Composable
fun TestScreen(
    navController: NavHostController,
    recordedVideos: MutableList<String>,
    cameraViewModel: CameraViewModel,
    sharedViewModel: SharedViewModel // Injectez le ViewModel partagé
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isFrontCamera by cameraViewModel.isFrontCamera

    val tests = remember { mutableStateListOf<Test>() }
    val currentInstructionIndex by sharedViewModel.currentInstructionIndex.collectAsState()
    val elapsedTime by sharedViewModel.elapsedTime.collectAsState()

    val currentInstruction = remember { mutableStateOf("Chargement...") }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val recording = remember { mutableStateOf<Recording?>(null) }
    val videoFilePath = remember { mutableStateOf<String?>(null) }

    var isRecording by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var showSafetyDialog by remember { mutableStateOf(true) }


    BackHandler(enabled = true) {}

    // 📂 Charger les tests
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val localTests = LocalCatManager.loadLocalTests(context)
            tests.addAll(localTests)

            // Afficher la première consigne
            val consigne = if (isFrontCamera) tests[currentInstructionIndex].a_consigne else tests[currentInstructionIndex].h_consigne
            currentInstruction.value = consigne ?: "Aucune consigne"
        }
    }

    // ⏳ Timer pendant l'enregistrement
    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000L)
            sharedViewModel.updateElapsedTime(elapsedTime + 1)
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


        // 🔸 Affichage de la consigne/image/mot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(bottom = 32.dp)
        ) {
            tests.getOrNull(currentInstructionIndex)?.let {
                TestDisplay(
                    test = it,
                    isFrontCamera = isFrontCamera,
                    key = currentInstructionIndex,
                    sharedViewModel = sharedViewModel,
                    onImageClick = { image ->
                        // Logique pour passer à l'instruction suivante quand une image est cliquée
                        if (currentInstructionIndex < tests.size - 1) {
                            sharedViewModel.updateInstructionIndex(currentInstructionIndex + 1)
                            val consigne = if (isFrontCamera) tests[currentInstructionIndex + 1].a_consigne else tests[currentInstructionIndex + 1].h_consigne
                            currentInstruction.value = consigne ?: "Aucune consigne"
                        } else {
                            // Fin des consignes, naviguer vers la confirmation
                            navController.navigate("confirmation")
                        }
                    }
                )
            }
        }

        // 🔘 Boutons bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Arrêter le test
            ImageClickable(
                imageResId = R.mipmap.ic_close_foreground,
                contentDescription = "Arrêter le test",
                onClick = {
                    if (isRecording) {
                        stopRecording(context, recording, videoFilePath)
                        isRecording = false
                    }

                    // Ajouter la consigne actuelle et le temps écoulé aux logs
                    tests.getOrNull(currentInstructionIndex)?.let {
                        sharedViewModel.addInstructionLog(Pair(currentInstruction.value, elapsedTime))
                    }

                    // Naviguer vers l'écran de confirmation
                    navController.navigate("confirmation")
                }
            )

            // Passer à la consigne suivante
            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
                contentDescription = "Instruction suivante",
                onClick = {
                    // Ajouter la consigne actuelle et le temps écoulé aux logs
                    tests.getOrNull(currentInstructionIndex)?.let {
                        sharedViewModel.addInstructionLog(Pair(currentInstruction.value, elapsedTime))
                    }

                    if (currentInstructionIndex < tests.size - 1) {
                        // Passer à l'instruction suivante
                        sharedViewModel.updateInstructionIndex(currentInstructionIndex + 1)

                        // Mettre à jour la consigne
                        val consigne = if (isFrontCamera) tests[currentInstructionIndex + 1].a_consigne else tests[currentInstructionIndex + 1].h_consigne
                        currentInstruction.value = consigne ?: "Aucune consigne"
                    } else {
                        // Fin des consignes : naviguer vers l'écran de confirmation
                        if (isRecording) {
                            stopRecording(context, recording, videoFilePath)
                            isRecording = false
                        }
                        navController.navigate("confirmation")
                    }
                }
            )
        }

        // 🔄 Switch caméra
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
                sharedViewModel.addInstructionLog(Pair("Changement de caméra : $cameraLabel", elapsedTime))

                // ✅ Appel correct du ViewModel
                cameraViewModel.toggleCamera()

                val consigne = if (cameraViewModel.isFrontCamera.value) {
                    tests.getOrNull(currentInstructionIndex)?.a_consigne
                } else {
                    tests.getOrNull(currentInstructionIndex)?.h_consigne
                }
                currentInstruction.value = consigne ?: "Aucune consigne"
                videoCapture.value = null
            },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopEnd)
                .size(80.dp)
        )


        if (showSafetyDialog) {
            AlertDialog(
                onDismissRequest = { /* Pas de fermeture via clic extérieur */ },
                confirmButton = {
                    TextButton(onClick = { showSafetyDialog = false }) {
                        Text("Continuer", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Sécurité",
                            tint = Color(0xFFFFA726),
                            modifier = Modifier.size(32.dp)
                        )
                        Text("Avant de commencer", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        """
                - Assurez-vous que le patient est assis ou allongé confortablement, pas debout.
                - Retirez tout objet dangereux ou pointu autour.
                - Restez à côté du patient pendant tout le test.
                - Arrêtez immédiatement en cas de comportement anormal ou inattendu et suivez les consignes d’urgence.
                """.trimIndent()
                    )
                },
                dismissButton = {}, // Pas de bouton "Retour"
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
                textContentColor = MaterialTheme.colorScheme.onSurface
            )
        }

        // 🎥 Démarrage de l'enregistrement vidéo
        LaunchedEffect(videoCapture.value, isFrontCamera) {
            if (videoCapture.value != null && !isRecording) {
                delay(500L) // Attend un peu plus longtemps pour plus de sûreté
                recording.value = startRecording(context, videoCapture.value!!, recording, videoFilePath, recordedVideos)
                isRecording = true
                Log.d("TestScreen", "🎥 Démarrage de l'enregistrement effectué.")
            } else {
                Log.d("TestScreen", "🎥 Démarrage non effectué : videoCapture pas prêt ou déjà enregistrement.")
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
    val isFrontCamera by cameraViewModel.isFrontCamera // ✅ Liaison au ViewModel

    AndroidView(
        factory = { ctx ->
            androidx.camera.view.PreviewView(ctx).apply {
                bindCamera(this, context, lifecycleOwner, videoCapture, isFrontCamera, cameraProviderFuture)
            }
        },
        modifier = modifier,
        update = { previewView ->
            // ✅ Mise à jour de la caméra seulement si nécessaire
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