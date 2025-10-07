package com.example.epilepsytestapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun InfoPage(navController: NavHostController) {
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
                                text = "Informations",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 30.sp,
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

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = { navController.navigate("demo/0") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF31D2B6)
                        )
                    ) {
                        Text("Lancer la démo", fontSize = 24.sp)
                    }


                    // Bouton En cas d'urgence
                    Button(
                        onClick = { navController.navigate("emergency") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE12222)
                        )
                    ) {
                        Text("En cas d’urgence", fontSize = 24.sp)
                    }


                    // Bloc de consignes avec scroll vertical interne
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(430.dp) // hauteur fixe pour que le scroll soit visible
                            .padding(horizontal = 16.dp)
                            .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp))
                            .background(Color(0xFFE6F7F6), RoundedCornerShape(12.dp))
                            .padding(7.dp)
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🎥 Comment filmer le patient :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp,
                                lineHeight = 30.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_cadrage_foreground),
                                    contentDescription = "Illustration cadrage",
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .fillMaxHeight()

                                )

                                Text(
                                    text = """
            1. Placez le patient bien au centre de l’image.
            2. Cadrez le haut du corps et le visage clairement : du torse jusqu’au sommet de la tête.
        """.trimIndent(),
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    color = Color(0xFF004D61),
                                    modifier = Modifier.weight(2f)
                                )
                            }

                            Text(
                                text = """
             3. Laissez un peu d’espace de chaque côté du patient dans le cadre.
             4. Gardez la caméra stable, posez-la sur un trépied ou une surface fixe.
        """.trimIndent(),
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                color = Color(0xFF004D61),
                            )




                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "⚠️ Sécurité avant et pendant la vidéo :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp,
                                lineHeight = 30.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = "La sécurité prime : si quelque chose d’anormal survient, suivez les consignes d’urgence.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.mipmap.ic_chaise_foreground), // ← Remplace par ton image réelle
                                    contentDescription = "Illustration cadrage",
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight()
                                )

                                Text(
                                    text = """
            1. Vérifier que le patient ne risque pas de tomber.
            """.trimIndent(),
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    color = Color(0xFF004D61),
                                    modifier = Modifier.weight(2f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.mipmap.ic_listsafety_foreground), // ← Remplace par ton image réelle
                                    contentDescription = "Illustration cadrage",
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight()
                                )

                                Text(
                                    text = """
            2. Vérifier qu’il n’y ait pas d’objets dangereux à proximité.
            3. Ne pas laisser le patient seul.
            """.trimIndent(),
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    color = Color(0xFF004D61),
                                    modifier = Modifier.weight(2f)
                                )
                                }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(id = R.mipmap.ic_stop_foreground), // ← Remplace par ton image réelle
                                    contentDescription = "Illustration cadrage",
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .fillMaxHeight()
                                )

                                Text(
                                    text = """
            4. Arrêtez la vidéo immédiatement si vous voyez quelque chose d’anormal (perte de conscience, convulsions…) et suivez les consignes d’urgence.
             """.trimIndent(),
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    color = Color(0xFF004D61),
                                    modifier = Modifier.weight(2f)
                                )
                            }


                        }
                    }
                }


                    // NavigationBar en bas de l'écran
            NavigationBar(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}