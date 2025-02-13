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
import com.example.epilepsytestapp.ui.theme.Blue40


@Composable
fun DemoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Liste des consignes
    val instructions = listOf(
        "Quel est ton mot code ?",
        "Qu'est ce que tu ressens ?",
        "Lève les deux bras devant toi",
        "Montre la main gauche à la caméra",
        "Répète le mot Citron"
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

        // Flèche pointant vers le bouton suivant avec la légende
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Texte
            Text(
                text = "Passe à l'instruction suivante",
                style = MaterialTheme.typography.headlineSmall.copy(color = Blue40),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            // Flèche pointant vers le bas
            Image(
                painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                contentDescription = "Flèche vers le bouton suivant",
                modifier = Modifier.size(110.dp) // Taille de la flèche
                    .offset(x = 30.dp)
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
                imageResId = R.mipmap.ic_close_foreground,
                contentDescription = "Arrêter le test",
                onClick = { navController.navigate("confirmation/${currentInstructionIndex}") }
            )

            // Image de flèche
            ImageClickable(
                imageResId = R.mipmap.ic_next_foreground,
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
