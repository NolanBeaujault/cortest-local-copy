package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.ui.theme.PrimaryColor

@Composable
fun TypeConfigScreen(
    navController: NavController,
    from: String = "",
    cameraViewModel: CameraViewModel = viewModel()
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            Box(modifier = Modifier.fillMaxSize()) {
                // On n'affiche pas le bouton de retour si on vient depuis le sign-up pour forcer l'utilisateur à faire sa configuration
                if (from != "signup") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = PrimaryColor,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Choisissez le type de test",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                        color = PrimaryColor
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Choix du mode heterotest : on utilise la caméra arrière, et les tests affichés sur la page suivante sont celles qui fonctionnent dans ce mode
                    OutlinedButton(
                        onClick = {
                            cameraViewModel.isFrontCamera.value = false
                            navController.navigate("testConfigScreen")
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(4.dp, PrimaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Text(
                            "Hétérotest",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 28.sp,
                                color = PrimaryColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Choix du mode autotest : on utilise la caméra avant, et les tests affichés sur la page suivante sont celles qui fonctionnent dans ce mode
                    OutlinedButton(
                        onClick = {
                            cameraViewModel.isFrontCamera.value = true
                            navController.navigate("testConfigScreen")
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(4.dp, PrimaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Text(
                            "Autotest",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 28.sp,
                                color = PrimaryColor
                            )
                        )
                    }
                }
            }
        }
    }
}
