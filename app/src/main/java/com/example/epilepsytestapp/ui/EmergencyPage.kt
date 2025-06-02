package com.example.epilepsytestapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun EmergencyPage(navController: NavHostController) {
    val context = LocalContext.current

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, Color(0xFF8A0000), RoundedCornerShape(1.dp))
                .padding(16.dp) // MARGE AUTOUR DE TOUT L’ÉCRAN
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween // espace entre header/scroll/boutons
            ) {
                // HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.wrapContentWidth() // largeur minimale selon contenu
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_phone_foreground),
                            contentDescription = "Phone",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 4.dp) // petite marge à droite
                        )
                        Text(
                            text = "EN CAS D'URGENCE",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 30.sp,
                                color = Color(0xFFE12222)
                            )
                        )
                    }
                }



                // CONTENU SCROLLABLE (checklist)
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Situations où il faut appeler le SAMU :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val checklistItems = listOf(
                        "Toute première crise d'une personne",
                        "Crise avec convulsions de plus de 5 minutes",
                        "Une 2ème crise (avec convulsions) survient avant que la personne ait repris connaissance",
                        "La personne ne reprend pas connaissance et/ou ne reprend pas sa respiration rapidement après la crise",
                        "La période de confusion suivant la crise persiste plus d'une heure",
                        "Crise survenue dans l'eau (risque d’ingestion d’eau pouvant provoquer des problèmes cardiaques et respiratoires)",
                        "La personne est blessée, a vomi, est enceinte, est diabétique ou présente des céphalées très intenses après la crise"
                    )

                    checklistItems.forEach { item ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "• ",
                                fontSize = 22.sp,
                                color = Color(0xFFE12222),
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = item,
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                color = Color(0xFF004D61)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }

                // BOUTONS EN BAS, empilés, plein largeur, espacés
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:15")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE12222)
                        )
                    ) {
                        Text("Appeler le SAMU (15)", fontSize = 24.sp)
                    }

                    Button(
                        onClick = { navController.navigate("home") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF31D2B6)
                        )
                    ) {
                        Text("Retour à l'accueil", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
