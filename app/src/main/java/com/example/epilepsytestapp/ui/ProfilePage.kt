package com.example.epilepsytestapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.model.Patient
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun ProfilePage(patients: List<Patient>, navController: NavHostController) {
    // Sélectionnez le premier patient de la liste ou null si la liste est vide
    val patient = patients.firstOrNull()

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, Color(0xFF2B4765), RoundedCornerShape(1.dp)) // Bordure
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

                        // Titre
                        Text(
                            text = "Profil",
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
                                    val selectedPatientId = patients.firstOrNull()?.id
                                    if (selectedPatientId != null) {
                                        navController.navigate("profile/$selectedPatientId")
                                    } else {
                                        println("Aucun patient sélectionné.")
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Photo de profil ou avatar
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(2.dp, Color(0xFF004D61), CircleShape)
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = R.mipmap.ic_profile_foreground, // Image par défaut si pas de photo
                        contentDescription = "Photo de profil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Informations du profil
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    if (patient != null) {
                        ProfileInfoRow(label = "Nom", value = patient.lastName)
                        ProfileInfoRow(label = "Prénom", value = patient.firstName)
                        ProfileInfoRow(label = "Adresse", value = patient.address)
                        ProfileInfoRow(label = "Neurologue", value = patient.neurologist)
                        ProfileInfoRow(label = "Identifiant", value = patient.username)
                    } else {
                        Text(
                            text = "Aucun patient sélectionné",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Barre de navigation
            NavigationBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label :",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}
