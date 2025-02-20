package com.example.epilepsytestapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.epilepsytestapp.ui.theme.AppTheme

@Composable
fun RecapScreen(navController: NavController) {
    val selectedTests = navController.previousBackStackEntry?.savedStateHandle?.get<Map<String, List<String>>>("selectedTests") ?: emptyMap()

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Récapitulatif de la configuration",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    selectedTests.forEach { (category, tests) ->
                        if (tests.isNotEmpty()) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.headlineSmall, // Changer ici si besoin
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            tests.forEach { test ->
                                Text(
                                    text = "- $test",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(text = "Retour") {
                navController.popBackStack()
            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomButton(text = "Enregistrer la configuration") {
                // Logique d’enregistrement à implémenter ici
            }
        }
    }
}
