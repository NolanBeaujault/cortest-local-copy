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
import com.example.epilepsytestapp.network.RetrofitInstance
import com.example.epilepsytestapp.network.UserProfileResponse
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(navController: NavHostController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userId = currentUser?.uid ?: ""

    var profile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Fonction pour charger la page en récupérant les données de l'utilisateur
    fun loadProfile(userId : String) {
        if (userId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    isLoading = true
                    val response = RetrofitInstance.api.getUserProfile(userId)
                    profile = response
                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Erreur : ${e.message}"
                    isLoading = false
                }
            }
        } else {
            errorMessage = "Aucun utilisateur connecté."
            isLoading = false
        }
    }

    // LaunchedEffect pour récupérer les données de l'utilisateur
    LaunchedEffect(userId) {
        loadProfile(userId)
    }

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
                // Header
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
                        Image(
                            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxHeight()
                        )
                        Text(
                            text = "Profil",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Image(
                            painter = painterResource(id = R.mipmap.ic_user_foreground),
                            contentDescription = "Profil",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp)
                                .clickable {
                                    loadProfile(userId)
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Affichage des données du profil
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF004D61)
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "Erreur inconnue",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (profile != null) {
                    val patient = profile!!

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally)
                            .border(2.dp, Color(0xFF004D61), CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = R.mipmap.ic_user_foreground, // Image par défaut
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
                        ProfileInfoRow(label = "Nom", value = patient.nom)
                        ProfileInfoRow(label = "Prénom", value = patient.prenom)
                        ProfileInfoRow(label = "Adresse", value = patient.adresse)
                        ProfileInfoRow(label = "Neurologue", value = patient.neurologue)
                        ProfileInfoRow(label = "Date de naissance", value = patient.date_naissance ?: "Non renseignée")
                        ProfileInfoRow(label = "Mot code", value = patient.mot_code)
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
