package com.example.epilepsytestapp.ui

import android.Manifest
import android.content.ActivityNotFoundException
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
    val pdfFiles = remember { mutableStateListOf<File>() }
    val videoFiles = remember { mutableStateListOf<File>() }
    var selectedTab by remember { mutableStateOf(0) }

    // Charger les fichiers PDF et vidéos à partir du répertoire interne
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                it.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }

        val pdfDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests")
        val videoDirectory = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
        if (pdfDirectory.exists() && videoDirectory.exists()) {
            pdfFiles.clear()
            videoFiles.clear()
            val pdfFilesList = pdfDirectory.listFiles { file -> file.extension == "pdf" }
            val videoFilesList = videoDirectory.listFiles { file -> file.extension == "mp4" }
            pdfFiles.addAll(pdfFilesList?.toList() ?: emptyList())
            videoFiles.addAll(videoFilesList?.toList() ?: emptyList())
        } else {
            if (!pdfDirectory.exists()) {
                Log.e("FilesPage", "PDF Directory does not exist")
                Toast.makeText(context, "PDF Directory does not exist", Toast.LENGTH_SHORT).show()
            }
            if (!videoDirectory.exists()) {
                Log.e("FilesPage", "Video Directory does not exist")
                Toast.makeText(context, "Video Directory does not exist", Toast.LENGTH_SHORT).show()
            }
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
                                    // Supposons que le premier patient est sélectionné
                                    val selectedPatientId = patient.firstOrNull()?.id

                                    if (selectedPatientId != null) {
                                        navController.navigate("profile/$selectedPatientId")
                                    } else {
                                        // Gérer le cas où aucun patient n'est disponible
                                        println("Aucun patient sélectionné.")
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Onglets pour les fichiers PDF et vidéos
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("PDF") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
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
                            Text(
                                text = "Fichiers PDF enregistrés :",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 28.sp
                                ),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Affichage d'un message si aucun fichier PDF n'est trouvé
                            if (pdfFiles.isEmpty()) {
                                Text(text = "Aucun fichier PDF trouvé.", modifier = Modifier.padding(16.dp))
                            } else {
                                LazyColumn {
                                    items(pdfFiles) { file ->
                                        Text(
                                            text = file.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { openPDF(context, file) }
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
                        1 -> {
                            Text(
                                text = "Vidéos enregistrées :",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 28.sp
                                ),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Affichage d'un message si aucune vidéo n'est trouvée
                            if (videoFiles.isEmpty()) {
                                Text(text = "Aucune vidéo trouvée.", modifier = Modifier.padding(16.dp))
                            } else {
                                LazyColumn {
                                    items(videoFiles) { file ->
                                        Column {
                                            Text(
                                                text = file.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { openVideo(context, file) }
                                                    .padding(8.dp),
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontSize = 18.sp
                                                )
                                            )
                                            Text(
                                                text = "Emplacement: ${file.absolutePath}",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontSize = 14.sp
                                                ),
                                                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                                            )
                                        }
                                    }
                                }
                            }
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

fun openPDF(context: Context, file: File) {
    try {
        if (file.exists()) {
            Log.d("FileProvider", "Chemin du fichier: ${file.absolutePath}")

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",  // ✅ Utilisation correcte de l'autorité
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
                "${context.packageName}.provider",  // ✅ Utilisation correcte de l'autorité
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