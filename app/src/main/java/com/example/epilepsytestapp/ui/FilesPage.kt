package com.example.epilepsytestapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.model.Patient
import com.example.epilepsytestapp.ui.theme.AppTheme
import java.io.File


@Composable
fun FilesPage(navController: NavHostController, patient: List<Patient>) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val questionnaireFiles = remember { mutableStateListOf<File>() }
    val videoFiles = remember { mutableStateListOf<File>() }
    val consigneFiles = remember { mutableStateListOf<File>() }
    var selectedTab by remember { mutableStateOf(0) }

    // Charger les fichiers PDF (Questionnaires/Consignes) et vidéos
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                it.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }

        val questionnaireDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Questionnaires")
        val consigneDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Consignes")
        val videoDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")

        questionnaireFiles.clear()
        consigneFiles.clear()
        videoFiles.clear()

        if (questionnaireDirectory.exists()) {
            questionnaireFiles.addAll(questionnaireDirectory.listFiles { file -> file.extension == "pdf" }?.toList() ?: emptyList())
        }
        if (consigneDirectory.exists()) {
            consigneFiles.addAll(consigneDirectory.listFiles { file -> file.extension == "pdf" }?.toList() ?: emptyList())
        }
        if (videoDirectory.exists()) {
            videoFiles.addAll(videoDirectory.listFiles { file -> file.extension == "mp4" }?.toList() ?: emptyList())
        }
    }

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, Color(0xFF2B4765), RoundedCornerShape(1.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp) // Espace pour la barre de navigation
            ) {
                // Rectangle bleu pâle en haut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0xFFD0EEED)), // Bleu pâle
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp) // Diminuer la marge horizontale pour éloigner les éléments
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxHeight() // Taille ajustée
                                .padding(end = 16.dp) // Espace supplémentaire à droite du logo
                        )

                        // Titre
                        Text(
                            text = "Fichiers",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp) // Espace autour du titre
                        )

                        // Icône utilisateur
                        Image(
                            painter = painterResource(id = R.mipmap.ic_user_foreground),
                            contentDescription = "Profil",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp)
                                .clickable {
                                    val selectedPatientId = patient.firstOrNull()?.id
                                    if (selectedPatientId != null) {
                                        navController.navigate("profile/$selectedPatientId")
                                    } else {
                                        println("Aucun patient sélectionné.")
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Onglets pour les fichiers Questionnaire, Consignes, et Vidéos
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Questionnaire") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Consignes") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Vidéos") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenu de la page
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            DisplayFileList(
                                title = "Questionnaires enregistrés :",
                                files = questionnaireFiles,
                                context = context
                            )
                        }
                        1 -> {
                            DisplayFileList(
                                title = "Consignes enregistrées :",
                                files = consigneFiles,
                                context = context
                            )
                        }
                        2 -> {
                            DisplayFileList(
                                title = "Vidéos enregistrées :",
                                files = videoFiles,
                                context = context,
                                isVideo = true
                            )
                        }
                    }
                }
            }

            // Barre de navigation en bas
            NavigationBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter) // Fixe la barre en bas
            )
        }
    }
}

@Composable
fun DisplayFileList(
    title: String,
    files: List<File>,
    context: Context,
    isVideo: Boolean = false
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp
        ),
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (files.isEmpty()) {
        Text(text = "Aucun fichier trouvé.", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn {
            items(files) { file ->
                Text(
                    text = file.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isVideo) {
                                openVideo(context, file)
                            } else {
                                openPDF(context, file)
                            }
                        }
                        .padding(8.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}


fun openPDF(context: Context, file: File) {
    try {
        if (file.exists()) {
            Log.d("FileProvider", "Chemin du fichier: ${file.absolutePath}")

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",  // Utilisation correcte de l'autorité
                file
            )
            Log.d("FileProvider", "URI générée: $uri")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            context.startActivity(Intent.createChooser(intent, "Ouvrir avec"))
        } else {
            Log.e("FileProvider", "Le fichier PDF n'existe pas.")
            Toast.makeText(context, "Le fichier PDF n'existe pas.", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("FileProvider", "Erreur lors de l'ouverture du fichier PDF: ${e.message}")
        Toast.makeText(context, "Erreur: Impossible d'ouvrir le fichier PDF.", Toast.LENGTH_LONG).show()
    }
}

fun openVideo(context: Context, file: File) {
    try {
        if (file.exists()) {
            Log.d("FileProvider", "Chemin du fichier: ${file.absolutePath}")

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",  // Utilisation correcte de l'autorité
                file
            )
            Log.d("FileProvider", "URI générée: $uri")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/mp4")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                Log.d("FileProvider", "Intent trouvé. Lancement de l'application vidéo.")
                context.startActivity(Intent.createChooser(intent, "Ouvrir avec"))
            } else {
                Log.e("FileProvider", "Aucune application compatible trouvée pour ouvrir la vidéo.")
                Toast.makeText(context, "Aucune application compatible pour ouvrir cette vidéo.", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("FileProvider", "La vidéo n'existe pas.")
            Toast.makeText(context, "La vidéo n'existe pas.", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("FileProvider", "Erreur lors de l'ouverture de la vidéo: ${e.message}")
        Toast.makeText(context, "Erreur: Impossible d'ouvrir la vidéo.", Toast.LENGTH_LONG).show()
    }
}