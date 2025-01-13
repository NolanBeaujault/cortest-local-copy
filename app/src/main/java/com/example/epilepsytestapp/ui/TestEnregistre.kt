package com.example.epilepsytestapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun TestEnregistre(navController: NavHostController) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Fond blanc
                .padding(16.dp)
        ) {
            // Texte principal
            Text(
                text = "Test enregistré",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 60.sp, // Beaucoup plus gros
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground // Couleur du texte définie dans le thème
                )
            )

            // Icône "Check"
            Image(
                painter = painterResource(id = R.mipmap.ic_good_foreground), // Image du check
                contentDescription = "Check icon",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
            Spacer(modifier = Modifier.height(60.dp))
            // Bouton "Retour à l'accueil"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center) // Positionné légèrement plus haut
                    .padding(top = 250.dp)
            ) {
                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), // Plus grand
                    shape = RoundedCornerShape(50.dp), // Bouton arrondi
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF31D2B6) // Couleur #31D2B6
                    )
                ) {
                    Text(
                        text = "Retour à l'accueil",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 30.sp // Texte plus grand

                        )
                    )
                }
            }

            // Logo en bas
            Image(
                painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
