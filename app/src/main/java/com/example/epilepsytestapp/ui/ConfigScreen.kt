package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.category.LocalCatManager
import com.example.epilepsytestapp.category.loadCategoriesFromNetwork
import com.example.epilepsytestapp.category.Test
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.ui.theme.PrimaryColor
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun ConfigScreen(navController: NavController, cameraViewModel: CameraViewModel = viewModel()) {
    val isFrontCamera by cameraViewModel.isFrontCamera
    val effectiveType = if (isFrontCamera) "auto" else "hetero"
    val selectedTests = remember { mutableStateOf(mutableSetOf<Test>()) }
    val scrollState = rememberScrollState()
    val categories = remember { mutableStateOf<Map<String, List<Test>>>(emptyMap()) }
    val loading = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val filename = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("configFileToLoad")

    LaunchedEffect(effectiveType, filename) {

        coroutineScope.launch {

            try {
                // Chargement du JSON serveur pour récupérer les tests à cocher, classés par catégorie
                val loadedCategories = loadCategoriesFromNetwork()

                // Chargement du JSON local selon la variable filename (s
                val localTestConfiguration = if (!filename.isNullOrBlank()) {
                    // Depuis le stockage externe quand on charge depuis l'historique des configurations
                    val file = File("EpilepsyTests/Configurations", filename).toString()
                    LocalCatManager.loadLocalTests(context, file)
                } else {
                    // Dans le stockage interne via si filename est vide
                    LocalCatManager.loadLocalTests(context)
                }

                categories.value = loadedCategories

                // Pré-sélection des tests selon la configuration précédente, affichage selon le type choisi sur l'écran précédent
                val preSelectedTests = loadedCategories.values.flatten()
                    .filter { test ->
                        (test.type == effectiveType || test.type == "both") &&
                                localTestConfiguration.any { it.id_test == test.id_test }
                    }
                    .toMutableSet()

                selectedTests.value.clear()
                selectedTests.value.addAll(preSelectedTests)

                // Si la pré-sélection des tests est vide, on coche les tests de la catégorie "Examen-type" par défaut
                if (preSelectedTests.isEmpty()) {
                    Log.i("TestConfig", "Aucun test n'a été pré-coché")
                    val defaultTests = loadedCategories.entries
                        .firstOrNull { it.key.equals("Examen type", ignoreCase = true) }
                        ?.value
                        ?.toSet() ?: emptySet()

                    if (defaultTests.isNotEmpty()) {
                        selectedTests.value.clear()
                        selectedTests.value.addAll(defaultTests)
                    }
                }

                // Si on revient depuis la page de récap (page suivante), on reprend la configuration en cours :
                val restoredSelectedTests =
                    navController.currentBackStackEntry?.savedStateHandle?.get<List<Test>>("selectedTests")

                restoredSelectedTests?.let {
                    selectedTests.value.clear()
                    selectedTests.value.addAll(it)
                }

            } catch (e: Exception) {
                Log.e("TestConfig", "Erreur lors du chargement des JSON de tests : ${e.message}")
            }
            loading.value = false
        }
    }

    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
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
                    text = "Configuration des tests",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (loading.value) {
                    CircularProgressIndicator()
                } else {
                    // Affichage des tests par catégorie à partir des fichiers JSON chargés
                    categories.value.forEach { (categoryName, testList) ->
                        val filteredTests = testList.filter {
                            it.type == effectiveType || it.type == "both"
                        }
                        if (filteredTests.isNotEmpty()) {
                            CategoryItem(
                                categoryName,
                                filteredTests,
                                selectedTests.value
                            ) { test, checked ->
                                val updatedSet = selectedTests.value.toMutableSet()
                                // On stocke les tests cochés dans un set si elles ne sont pas déjà présentes dedans (condition sur l'id_test)
                                if (checked) {
                                    updatedSet.add(test)
                                } else {
                                    updatedSet.removeIf { it.id_test == test.id_test }
                                }
                                selectedTests.value = updatedSet
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(text = "Historique des configurations") {
                    navController.navigate("configHistoryScreen")
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomButton(text = "Suivant") {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "selectedTests",
                        selectedTests.value.toList()
                    )
                    navController.navigate("recapScreen")
                }
                Spacer(modifier = Modifier.height(8.dp))

                CustomButton(text = "Annuler") {
                    navController.popBackStack()
                }


            }
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    tests: List<Test>,
    selectedTests: Set<Test>,
    onTestCheckedChange: (Test, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {

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
                    .rotate(if (isExpanded) 90f else 0f)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            tests.forEach { test ->
                val isChecked = selectedTests.any { it.id_test == test.id_test }

                if (isExpanded || isChecked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                onTestCheckedChange(test, checked)
                            }
                        )
                        Text(
                            text = test.nom,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}