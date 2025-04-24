package com.example.epilepsytestapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.provider.MediaStore
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
            videoFiles.addAll(
                videoDirectory.listFiles { file ->
                    file.extension == "mp4" && file.name.startsWith("Vidéo_")
                }?.toList() ?: emptyList()
            )
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
                                .fillMaxHeight()// Taille ajustée
                                .padding(end = 16.dp) // Espace supplémentaire à droite du logo
                        )

                        // Titre "Home"
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
                                    navController.navigate("profile")
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
            fontSize = 22.sp
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (files.isEmpty()) {
        Text("Aucun fichier trouvé.", style = MaterialTheme.typography.bodyMedium)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(files) { file ->
                if (isVideo) {
                    VideoCard(file, context)
                } else {
                    FileCard(file, context)
                }
            }
        }
    }
}

@Composable
fun FileCard(file: File, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openPDF(context, file) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.mipmap.ic_pdf_foreground), // ajoute une icône PDF générique dans drawable
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Modifié : ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                        Date(file.lastModified())
                    )}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VideoCard(file: File, context: Context) {
    val thumbnailBitmap by remember(file) {
        mutableStateOf(
            ThumbnailUtils.createVideoThumbnail(
                file.absolutePath,  // ✅ Le chemin absolu est nécessaire
                MediaStore.Video.Thumbnails.MINI_KIND
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openVideo(context, file) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Affichage de la miniature si dispo
            if (thumbnailBitmap != null) {
                Image(
                    bitmap = thumbnailBitmap!!.asImageBitmap(),
                    contentDescription = "Aperçu vidéo",
                    modifier = Modifier
                        .size(120.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                // ✅ Icône fallback propre si la miniature échoue
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_video_play_foreground),
                        contentDescription = "Icône vidéo",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Enregistré : ${
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(file.lastModified()))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
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