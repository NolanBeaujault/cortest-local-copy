package com.example.epilepsytestapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        navController.previousBackStackEntry?.savedStateHandle?.get<Map<String, List<Test>>>("selectedTests")
            ?.mapValues { it.value.toMutableList() }
            ?.toMutableMap() ?: mutableStateMapOf<String, MutableList<Test>>()
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
            ) {
                selectedTests.forEach { (category, tests) ->
                    if (tests.isNotEmpty()) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ReorderableList(tests)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(text = "Retour") {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedTests", selectedTests)
                navController.popBackStack()
            }

            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Enregistrer la configuration") {
                coroutineScope.launch {
                    LocalCatManager.saveLocalTests(context, selectedTests)
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
    var list by remember { mutableStateOf(tests.toList()) }

    Column {
        list.forEachIndexed { index, test ->
            TestItem(test, index, list) { updatedList ->
                list = updatedList
            }
        }
    }
}

@Composable
fun TestItem(test: Test, index: Int, tests: List<Test>, onListUpdate: (List<Test>) -> Unit) {
    var offsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumePositionChange()
                    offsetY += dragAmount.y
                    if (offsetY > 50 && index < tests.size - 1) {
                        val newList = tests.toMutableList()
                        val temp = newList[index]
                        newList[index] = newList[index + 1]
                        newList[index + 1] = temp
                        onListUpdate(newList)
                        offsetY = 0f
                    } else if (offsetY < -50 && index > 0) {
                        val newList = tests.toMutableList()
                        val temp = newList[index]
                        newList[index] = newList[index - 1]
                        newList[index - 1] = temp
                        onListUpdate(newList)
                        offsetY = 0f
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column {
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
                            val newList = tests.toMutableList()
                            val temp = newList[index]
                            newList[index] = newList[index - 1]
                            newList[index - 1] = temp
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
                            val newList = tests.toMutableList()
                            val temp = newList[index]
                            newList[index] = newList[index + 1]
                            newList[index + 1] = temp
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
}
