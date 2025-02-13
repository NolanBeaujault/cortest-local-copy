package com.example.epilepsytestapp.ui

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.epilepsytestapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController


@Composable
fun TestScreen(navController: NavHostController) {
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
    var currentInstructionIndex by remember { mutableStateOf(0) }
    val currentInstruction = instructions.getOrNull(currentInstructionIndex)

    // État pour gérer les erreurs de la caméra
    var cameraError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!cameraError) {
            // Affiche la caméra
            CameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Affiche un message d'erreur
            Text(
                text = "Impossible d'accéder à la caméra",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

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
            // Image de croix
            ImageClickable(
                imageResId = R.mipmap.ic_close_foreground, // Remplacez par l'ID de votre image
                contentDescription = "Arrêter le test",
                onClick = { navController.navigate("confirmation/${currentInstructionIndex}") }
            )

            // Image de flèche
            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground, // Remplacez par l'ID de votre image
                contentDescription = "Instruction suivante",
                onClick = {
                    if (currentInstructionIndex < instructions.size - 1) {
                        currentInstructionIndex++
                    } else {
                        // Si on est à la dernière consigne, arrêter le test
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
            .size(180.dp) // Taille de l'image multipliée par 5
            .padding(6.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
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

                        // Préparation de l'objet Preview
                        val preview = androidx.camera.core.Preview.Builder().build()
                        preview.surfaceProvider = this.surfaceProvider

                        // Lier la caméra à la PreviewView
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        e.printStackTrace() // Gérer les erreurs ici
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = modifier
    )
}
