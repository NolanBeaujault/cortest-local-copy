package com.example.epilepsytestapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun TypeConfigScreen(navController: NavController) {
    var selectedType by remember { mutableStateOf<String?>(null) }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choisissez le type de test",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    selectedType = "Hétérotest"
                    navController.navigate("testConfigScreen")
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hétérotest", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedType = "Autotest"
                    navController.navigate("testConfigScreen")
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Autotest", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}