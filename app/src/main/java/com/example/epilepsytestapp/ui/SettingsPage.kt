package com.example.epilepsytestapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme


@Composable
fun SettingsPage(navController: NavHostController) {
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
                                .fillMaxHeight() // Taille ajustée
                                .padding(start = 16.dp) // Espace supplémentaire à gauche de l'icône
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options des paramètres
                Text(
                    text = "Informations de connexion",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Autorisation caméra",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Déconnexion",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Barre de navigation en bas
            NavigationBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter) // Fixe la barre en bas
            )
        }
    }
}
