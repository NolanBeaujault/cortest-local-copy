package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.category.loadCategoriesFromNetwork
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun TestConfigurationScreen(navController: NavController) {
    val selectedTests = remember { mutableStateMapOf<String, MutableSet<String>>() }
    val scrollState = rememberScrollState()
    val categories = remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val loading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            Log.d("TestConfig", "🔄 Chargement des catégories depuis l'API...")
            try {
                val loadedCategories = loadCategoriesFromNetwork()
                categories.value = loadedCategories
                Log.d("TestConfig", "✅ Catégories chargées avec succès : $loadedCategories")
            } catch (e: Exception) {
                Log.e("TestConfig", "❌ Erreur lors du chargement des catégories : ${e.message}")
                e.printStackTrace()
            }
            loading.value = false
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
            Text(
                text = "Configuration des tests",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (loading.value) {
                CircularProgressIndicator()
            } else {
                Log.d("TestConfig", "📌 Affichage des catégories et tests...")
                categories.value.forEach { (categoryName, testList) ->
                    Log.d("TestConfig", "📁 Catégorie : $categoryName, Nombre de tests : ${testList.size}")
                    CategoryItem(categoryName, testList, selectedTests)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
    tests: List<String>, // Une liste de noms de tests
    selectedTests: MutableMap<String, MutableSet<String>>
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
                    .rotate(if (isExpanded) 180f else 0f) // 🔄 Rotation de l'icône
            )
        }

        // Liste des tests (noms des tests)
        Column(modifier = Modifier.padding(start = 16.dp)) {
            tests.forEach { testName ->
                val isChecked = selectedTests[title]?.contains(testName) ?: false

                // ✅ Afficher le test si :
                // 1️⃣ Le volet est ouvert
                // 2️⃣ Le test est coché (même si le volet est fermé)
                if (isExpanded || isChecked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val updatedTests = selectedTests.getOrDefault(title, mutableSetOf()).toMutableSet()
                                if (checked) updatedTests.add(testName) else updatedTests.remove(testName)
                                selectedTests[title] = updatedTests // ✅ Mise à jour immédiate
                            }
                        )
                        Text(
                            text = testName,  // Affichage du nom du test
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
