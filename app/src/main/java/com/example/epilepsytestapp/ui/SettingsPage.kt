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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun SettingsPage(navController: NavHostController, onLogout: () -> Unit) {
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
                    .padding(bottom = 70.dp)
            ) {
                // Barre supérieure
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
                            .padding(horizontal = 3.dp)
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 16.dp)
                        )

                        // Titre "Paramètres"
                        Text(
                            text = "Paramètres",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Icône utilisateur
                        Image(
                            painter = painterResource(id = R.mipmap.ic_user_foreground),
                            contentDescription = "Profil",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options des paramètres
                Column(modifier = Modifier.fillMaxSize()) {
                    // Accéder aux autorisations
                    SettingsOption(
                        text = "Gérer les autorisations",
                        onClick = { openAppSettings(navController.context) }
                    )

                    // Déconnexion
                    SettingsOption(
                        text = "Déconnexion",
                        onClick = {
                            onLogout()
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
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
