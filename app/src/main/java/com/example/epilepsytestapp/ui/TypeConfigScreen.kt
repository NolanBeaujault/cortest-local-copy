package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.ui.theme.PrimaryColor

@Composable
fun TypeConfigScreen(navController: NavController, cameraViewModel: CameraViewModel = viewModel())
{
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Choisissez le type de test",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                color = PrimaryColor
            )

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
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, color = PrimaryColor)
                )
            }

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
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp, color = PrimaryColor)
                )
            }
        }
    }
}
