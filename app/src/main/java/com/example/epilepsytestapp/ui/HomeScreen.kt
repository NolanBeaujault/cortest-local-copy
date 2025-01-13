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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun HomePage(navController: NavHostController) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, Color(0xFF2B4765), RoundedCornerShape(1.dp)) // Bordure autour de l'écran
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
                            text = "Home",
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

                // Dernier test avec cadre bleu foncé
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Réduction de la largeur
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp)) // Bordure bleu foncé
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dernière test\n" +
                                "date : XX/XX/XXXX   durée : XX:XX",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prochain rendez-vous avec cadre bleu foncé
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Réduction de la largeur
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp)) // Bordure bleu foncé
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Prochain RDV\n" +
                                "le XX/XX/XXXX à lieu avec Dr XXX",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton "Commencer un test"
                Button(
                    onClick = { navController.navigate("test") },
                    modifier = Modifier
                        .fillMaxWidth() // Pleine largeur
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF31D2B6) // Couleur bouton vert
                    )
                ) {
                    Text(
                        text = "Commencer un test",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 30.sp
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
        IconButton(onClick = { navController.navigate("calendar") }) {
            Image(
                painter = painterResource(id = R.mipmap.ic_calendar_foreground),
                contentDescription = "Calendar",
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


