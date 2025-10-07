package com.example.epilepsytestapp.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.category.LocalCatManager
import com.example.epilepsytestapp.category.Test
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Fonction composable pour l'écran récapitulatif de la configuration
@Composable
fun RecapScreen(navController: NavController) {
    val selectedTests = remember {
        mutableStateListOf<Test>().apply {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<List<Test>>("selectedTests")?.let { addAll(it) }
        }
    }


    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
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
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    ReorderableList(selectedTests)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gestion du retour vers l'écran précédant en gardant la configuration en cours
                CustomButton(text = "Retour") {
                    try {
                        val backStackEntry = navController.previousBackStackEntry
                        if (backStackEntry != null) {
                            val updatedList = selectedTests.toList().map { it.copy() }
                            Log.i(
                                "RecapScreen",
                                "Enregistrement des tests avant retour vers la config. : $updatedList"
                            )
                            backStackEntry.savedStateHandle["selectedTests"] = updatedList
                            navController.popBackStack()
                        } else {
                            Log.e(
                                "Navigation",
                                "Impossible de revenir en arrière, backStackEntry est null !"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "Navigation",
                            "Erreur lors du retour à la page précédente : ${e.message}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enregistrement de la configuration sous la forme d'un JSON en interne sous le nom "localtestconfiguration.json"
                // Puis en externe sous le format 'configuration_yyyy-MM-dd_HH:mm.json" pour l'historique
                CustomButton(text = "Enregistrer la configuration") {
                    coroutineScope.launch {
                        LocalCatManager.saveLocalTests(
                            context,
                            "localtestconfiguration.json",
                            selectedTests.toList()
                        )

                        val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
                        val formattedDate = formatter.format(Date())
                        val fileName = "configuration_$formattedDate.json"

                        LocalCatManager.saveLocalTests(
                            context,
                            fileName,
                            selectedTests.toList(),
                            true
                        )
                        val prefsConfig = context.getSharedPreferences("AppPrefsConfig", Context.MODE_PRIVATE)
                        prefsConfig.edit()
                            .putLong("lastConfigModification", System.currentTimeMillis())
                            .apply()

                    }
                    Toast.makeText(context, "Configuration enregistrée !", Toast.LENGTH_LONG).show()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
    }
}

// Fonction composable pour la liste réorganisable des tests
// Permet de changer l'ordre d'apparition des tests pendant le testing
@Composable
fun ReorderableList(tests: MutableList<Test>) {
    val versionMap = remember { mutableStateMapOf<Test, Int>() }

    Column {
        tests.forEachIndexed { index, test ->
            val version = versionMap[test] ?: 0

            key(test.hashCode() to version) {
                TestItem(test, index, tests, versionMap) { updatedList ->
                    tests.clear()
                    tests.addAll(updatedList)
                }
            }
        }
    }
}

@Composable
fun TestItem(
    test: Test,
    index: Int,
    tests: MutableList<Test>,
    versionMap: MutableMap<Test, Int>,
    onListUpdate: (List<Test>) -> Unit
) {
    var animateTrigger by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (animateTrigger) 16.dp else 1.dp,
        label = "elevationAnim"
    )

    val scale by animateFloatAsState(
        targetValue = if (animateTrigger) 1.02f else 1f,
        label = "scale"
    )

    // Déclenchement de l’animation (swap dans la liste) à chaque fois qu'on appuie sur une flèche
    LaunchedEffect(Unit) {
        animateTrigger = true
        kotlinx.coroutines.delay(100)
        animateTrigger = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = test.nom,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    if (index > 0) {
                        val newList = tests.toMutableList().apply {
                            swap(index, index - 1)
                        }

                        // Utilisation de versionMap pour pouvoir lancer l'animation quand on clique sur une flèche
                        versionMap[test] = (versionMap[test] ?: 0) + 1
                        versionMap[tests[index - 1]] = (versionMap[tests[index - 1]] ?: 0) + 1
                        onListUpdate(newList)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_expend_more_foreground),
                        contentDescription = "Move Up",
                        modifier = Modifier.rotate(-90f),
                        tint = if (index > 0) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                IconButton(onClick = {
                    if (index < tests.size - 1) {
                        val newList = tests.toMutableList().apply {
                            swap(index, index + 1)
                        }

                        // Utilisation de versionMap pour pouvoir lancer l'animation quand on clique sur une flèche
                        versionMap[test] = (versionMap[test] ?: 0) + 1
                        versionMap[tests[index + 1]] = (versionMap[tests[index + 1]] ?: 0) + 1
                        onListUpdate(newList)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_expend_more_foreground),
                        contentDescription = "Move Down",
                        modifier = Modifier.rotate(90f),
                        tint = if (index < tests.size - 1) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

// Fonction de swapping des éléments de la liste réorganisable
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}
