package com.example.epilepsytestapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun TestConfigurationScreen(navController: NavController) {
    val selectedTests = remember { mutableStateMapOf<String, MutableSet<String>>() }
    val selectedTestsGlobal = remember { mutableStateOf(mutableSetOf<String>()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val categories = remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val loading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Charger les catégories et tests depuis le fichier JSON
    LaunchedEffect(Unit) {
        // Lancer un appel réseau pour récupérer les catégories
        coroutineScope.launch {
            try {
                categories.value = loadCategoriesFromJson(context)
                loading.value = false
            } catch (e: Exception) {
                // Gérer l'erreur, par exemple, afficher un message d'erreur
                e.printStackTrace()
                loading.value = false
            }
        }
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre
            Text(
                text = "Configuration des tests",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Si les données sont en cours de chargement, afficher un indicateur de chargement
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                // Liste des catégories et tests
                categories.value.forEach { (categoryName, tests) ->
                    CategoryItem(categoryName, tests, selectedTests, selectedTestsGlobal)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo
            Image(
                painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Boutons
            CustomButton(text = "Suivant") {
                val filteredTests = selectedTests.mapValues { it.value.toList() }
                navController.currentBackStackEntry?.savedStateHandle?.set("selectedTests", filteredTests)
                navController.navigate("recapScreen")
            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomButton(text = "Annuler") {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    tests: List<String>,
    selectedTests: MutableMap<String, MutableSet<String>>,
    selectedTestsGlobal: MutableState<MutableSet<String>>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp) // Espace entre les catégories
    ) {
        // Bouton pour afficher/cacher la catégorie
        Button(
            onClick = { isExpanded = !isExpanded },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.mipmap.ic_expend_more_foreground),
                contentDescription = "Expand",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(if (isExpanded) -90f else 0f)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            tests.forEach { testName ->
                val isChecked = selectedTests[title]?.contains(testName) ?: false

                // ✅ Afficher uniquement les tests cochés si la catégorie est fermée
                if (isExpanded || isChecked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selectedTests[title] = selectedTests.getOrDefault(title, mutableSetOf()).toMutableSet().apply {
                                    if (checked) add(testName) else remove(testName)
                                }

                                // Mise à jour de selectedTestsGlobal pour assurer la recomposition
                                selectedTestsGlobal.value = selectedTests.values.flatten().toMutableSet()
                            }
                        )
                        Text(
                            text = testName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}