package com.example.epilepsytestapp.savefiles

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController

@Composable
fun SurveyEntryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val questions = remember { mutableStateListOf<SurveyQuestionUI>() }

    LaunchedEffect(Unit) {
        val saved = SurveyStorage.loadSurvey(context)
        questions.clear()
        questions.addAll(
            if (saved.isNotEmpty()) saved.map { it.toUi() } else defaultQuestions
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Modifier le Questionnaire",
            style = MaterialTheme.typography.displayLarge,
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        questions.forEachIndexed { index, question ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = question.label,
                            onValueChange = { questions[index] = question.copy(label = it) },
                            label = { Text("Question") },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { questions.removeAt(index) }) {
                            Icon(Icons.Default.Close, contentDescription = "Supprimer")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    QuestionTypeSelector(
                        selected = question.type,
                        onSelected = { questions[index] = question.copy(type = it) }
                    )

                    if (question.type == QuestionType.CHOIX_UNIQUE || question.type == QuestionType.CHOIX_MULTIPLE) {
                        Spacer(Modifier.height(8.dp))
                        question.options.forEachIndexed { optIndex, option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = option,
                                    onValueChange = {
                                        val newOptions = question.options.toMutableList()
                                        newOptions[optIndex] = it
                                        questions[index] = question.copy(options = newOptions)
                                    },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Option ${optIndex + 1}") }
                                )
                                IconButton(onClick = {
                                    val newOptions = question.options.toMutableList()
                                    newOptions.removeAt(optIndex)
                                    questions[index] = question.copy(options = newOptions)
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Supprimer l'option")
                                }
                            }
                        }

                        Button(onClick = {
                            questions[index] =
                                question.copy(options = question.options + "")
                        }) {
                            Text("Ajouter une option")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            questions.add(SurveyQuestionUI(label = "", type = QuestionType.TEXTE))
        }) {
            Text("Ajouter une nouvelle question")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val success = SurveyStorage.saveSurvey(context, questions.map { it.toData() })
                if (success) {
                    Toast.makeText(context, "✅ Questionnaire enregistré localement", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "❌ Erreur lors de l'enregistrement", Toast.LENGTH_LONG).show()
                }
                navController.navigate("home")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer le questionnaire", fontSize = 18.sp)
        }
    }
}

@Composable
fun QuestionTypeSelector(selected: QuestionType, onSelected: (QuestionType) -> Unit) {
    Column {
        Text("Type de question :", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuestionType.entries.forEach { type ->
                Row(
                    modifier = Modifier
                        .clickable { onSelected(type) }
                        .background(
                            if (type == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Text(text = type.label, fontSize = 12.sp)
                }
            }
        }
    }
}
