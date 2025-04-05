package com.example.epilepsytestapp.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.model.Patient
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun SettingsPage(
    navController: NavHostController,
    onLogout: () -> Unit,
    onModifyConfiguration: () -> Unit,
    patient: List<Patient>,
    cameraViewModel: CameraViewModel
) {
    val isFrontCamera = cameraViewModel.isFrontCamera


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
                                .fillMaxHeight()// Taille ajustée
                                .padding(end = 16.dp) // Espace supplémentaire à droite du logo
                        )

                        // Titre "Home"
                        Text(
                            text = "Paramètres",
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

                SettingsOption(text = "Gérer les autorisations") { openAppSettings(navController.context) }
                SettingsOption(text = "Déconnexion") { onLogout(); navController.navigate("login") }
                SettingsOption(text = "Modifier la configuration", onClick = onModifyConfiguration)

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Ligne avec le switch caméra et ses labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Caméra",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Arrière",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = if (!isFrontCamera.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Switch(
                            checked = isFrontCamera.value,
                            onCheckedChange = { isFrontCamera.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Avant",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = if (isFrontCamera.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }


            }
            NavigationBar(
                    navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter) // Fixe la barre en bas
            )
        }

    }
}

@Composable
fun SettingsOption(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(12.dp),
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Start
        )
    )
}

// Fonction pour ouvrir les paramètres de l'application
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

class CameraViewModel : ViewModel() {
    var isFrontCamera = mutableStateOf(true)
}
