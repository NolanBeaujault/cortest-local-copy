package com.example.epilepsytestapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomePage(navController: NavHostController) {

    val context = LocalContext.current
    val videoDir = File(context.getExternalFilesDir(null), "EpilepsyTests/Videos")
    val questionnaireDir = File(context.getExternalFilesDir(null), "EpilepsyTests/Questionnaires")

    val latestVideo = videoDir.listFiles()?.maxByOrNull { it.lastModified() }
    val totalTests = videoDir.listFiles { _, name ->
        name.startsWith("Vidéo_") && name.endsWith(".mp4")
    }?.size ?: 0

    val lastQuestionnaire = questionnaireDir.listFiles()?.maxByOrNull { it.lastModified() }

    val configDate = remember {
        val prefsConfig = context.getSharedPreferences("AppPrefsConfig", Context.MODE_PRIVATE)
        val timestampConfig = prefsConfig.getLong("lastConfigModification", -1L)
        if (timestampConfig != -1L) {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestampConfig))
        } else {
            "N/A"
        }
    }



    val lastSurveyModDate = remember {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val timestamp = prefs.getLong("lastSurveyModification", -1L)
        if (timestamp != -1L) {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        } else {
            "N/A"
        }
    }


    val videoDate = latestVideo?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
        Date(it.lastModified())
    ) } ?: "N/A"
    val videoTime = latestVideo?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.lastModified())) } ?: "N/A"

    val questionnaireDate = lastQuestionnaire?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.lastModified())) } ?: "N/A"

    val isQuestionnaireFilled = remember {
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            .getBoolean("questionnaireFilled", true) // true par défaut
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
                    .padding(bottom = 70.dp), // ✅ Ajout de padding global
                verticalArrangement = Arrangement.spacedBy(16.dp) // ✅ Espacement entre tous les éléments
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
                            text = "Home",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 35.sp,
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
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 90.dp, bottom = 70.dp), // ⬅️ padding vertical seulement ici
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 📅 Dernier test
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // ⬅️ Espacement latéral ici
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp))
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dernier test : $videoDate\nHeure : $videoTime",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // 📊 Total de tests
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp))
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Nombre total de tests : $totalTests",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // 🛠️ Questionnaire / Configuration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp))
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "📝 Dernier questionnaire rempli : $questionnaireDate",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚙️ Configuration modifiée : $configDate",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚙️ Questionnaire modifié : $lastSurveyModDate",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                // ▶️ Commencer un test
                Button(
                    onClick = { navController.navigate("test") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(80.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF31D2B6)
                    )
                ) {
                    Text(
                        text = "Commencer un test",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        if (!isQuestionnaireFilled) navController.navigate("questionnaire")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isQuestionnaireFilled) Color.Gray else Color(0xFF6CA0DC)
                    ),
                    enabled = !isQuestionnaireFilled
                ) {
                    Text(
                        text = "Remplir questionnaire",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 20.sp
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

                // ▶️ Lancer test

            NavigationBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
fun NavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .height(70.dp), // Hauteur de la barre de navigation
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = { navController.navigate("home") }) {
            Image(
                painter = painterResource(id = R.mipmap.ic_home_foreground),
                contentDescription = "Home",
                modifier = Modifier
                    .fillMaxHeight() // Prend toute la hauteur disponible
                    .aspectRatio(1f) // Assure un rapport largeur/hauteur carré
            )
        }
        IconButton(onClick = { navController.navigate("info") }) {
            Image(
                painter = painterResource(id = R.mipmap.ic_info_foreground),
                contentDescription = "Info",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }
        IconButton(onClick = { navController.navigate("files") }) {
            Image(
                painter = painterResource(id = R.mipmap.ic_files_foreground),
                contentDescription = "Files",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }
        IconButton(onClick = { navController.navigate("settings") }) {
            Image(
                painter = painterResource(id = R.mipmap.ic_settings_foreground),
                contentDescription = "Settings",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }
    }
}


