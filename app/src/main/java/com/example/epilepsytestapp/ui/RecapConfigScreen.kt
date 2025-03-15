package com.example.epilepsytestapp.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.consumePositionChange
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

@Composable
fun RecapScreen(navController: NavController) {
    val selectedTests = remember {
        mutableStateListOf<Test>().apply {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<Map<String, List<Test>>>("selectedTests")
                ?.values?.flatten()
                ?.let { addAll(it) }
        }
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())  // Activation du défilement vertical
            ) {
                ReorderableList(selectedTests)
            }

            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(text = "Retour") {
                try {
                    val backStackEntry = navController.previousBackStackEntry
                    if (backStackEntry != null) {
                        // Sauvegarder les tests sélectionnés dans le `savedStateHandle`
                        backStackEntry.savedStateHandle["selectedTests"] = mapOf("Tous les tests" to selectedTests.toList())
                        navController.popBackStack()
                    } else {
                        Log.e("Navigation", "Impossible de revenir en arrière, backStackEntry est null")
                    }
                } catch (e: Exception) {
                    Log.e("Navigation", "Erreur lors du retour à la page précédente : ${e.message}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Enregistrer la configuration") {
                coroutineScope.launch {
                    LocalCatManager.saveLocalTests(context, mapOf("Tous les tests" to selectedTests))
                }
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
    }
}



@Composable
fun ReorderableList(tests: MutableList<Test>) {
    Column {
        tests.forEachIndexed { index, test ->
            TestItem(test, index, tests) { updatedList ->
                tests.clear()
                tests.addAll(updatedList)
            }
        }
    }
}

@Composable
fun TestItem(test: Test, index: Int, tests: MutableList<Test>, onListUpdate: (List<Test>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
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

// ✅ Fonction utilitaire pour échanger deux éléments
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}
