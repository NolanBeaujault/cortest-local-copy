package com.example.epilepsytestapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.epilepsytestapp.category.LocalCatManager
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.ui.theme.PrimaryColor

@Composable
fun ConfigHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val configurations = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(Unit) {
        configurations.clear()
        configurations.addAll(LocalCatManager.listConfigurations(context))
    }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                IconButton(
                    onClick = {navController.popBackStack()},
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 1.dp, start = 1.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour aux paramètres",
                        tint = PrimaryColor,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Text(
                    text = "Historique des configurations",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(configurations) { (displayName, fileName) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // On envoie le nom du fichier et on revient à ConfigScreen
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("configFileToLoad", fileName)
                                    navController.popBackStack()
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text("Retour")
                }
            }
        }
    }
}