package com.example.epilepsytestapp.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.savefiles.QuestionType
import com.example.epilepsytestapp.savefiles.SurveyQuestion
import com.example.epilepsytestapp.savefiles.SurveyStorage
import com.example.epilepsytestapp.savefiles.saveQuestionnaireAsPDF
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostTestQuestionnaireScreen(onSaveTest: () -> Unit) {
    val context = LocalContext.current
    val questionnaireAnswers = remember { mutableStateListOf<String>() }
    val questionnaireDetails = remember { mutableStateListOf<Pair<String, String>>() }
    var questions by remember { mutableStateOf<List<SurveyQuestion>>(emptyList()) }

    val formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(Date())
    val fileName = "Questionnaire_$formattedDate"


    LaunchedEffect(Unit) {
        questions = SurveyStorage.loadSurvey(context)
        questionnaireAnswers.clear()
        questionnaireDetails.clear()
        repeat(questions.size) {
            questionnaireAnswers.add("")
            questionnaireDetails.add("" to "")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Questionnaire\nPost-test",
            style = MaterialTheme.typography.displayLarge,
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        questions.forEachIndexed { index, question ->
            when (QuestionType.valueOf(question.type)) {
                QuestionType.TEXTE -> QuestionInput(question.label) {
                    questionnaireAnswers[index] = it
                    questionnaireDetails[index] = question.label to "Réponse : $it"
                }

                QuestionType.CHOIX_UNIQUE -> QuestionOptionsUnique(
                    question.label,
                    question.options
                ) {
                    questionnaireAnswers[index] = it
                    questionnaireDetails[index] = question.label to "Réponse : $it"
                }

                QuestionType.CHOIX_MULTIPLE -> QuestionOptionsMultiple(
                    question.label,
                    question.options
                ) { selected ->
                    questionnaireAnswers[index] = selected.joinToString(", ")
                    questionnaireDetails[index] =
                        question.label to "Réponses : ${selected.joinToString(", ")}"
                }

                QuestionType.CURSEUR_1_10 -> QuestionSlider(question.label, 0f..10f, 9) {
                    questionnaireAnswers[index] = it.toString()
                    questionnaireDetails[index] = question.label to "Valeur : $it"
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("questionnaireFilled", true).apply()

                val pdfFile = saveQuestionnaireAsPDF(
                    context = context,
                    questionnaireData = questionnaireDetails,
                    fileName = fileName
                )

                if (pdfFile != null) {
                    Toast.makeText(context, "Questionnaire enregistré", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Échec de l'enregistrement du PDF", Toast.LENGTH_LONG).show()
                }

                onSaveTest()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Text(
                text = "Enregistrer le test",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("questionnaireFilled", false).apply()

                Toast.makeText(context, "Vous pourrez le remplir plus tard", Toast.LENGTH_SHORT).show()
                onSaveTest()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF31D2B6))
        ) {
            Text("Remplir plus tard", fontSize = 18.sp)
        }



        Image(
            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun QuestionSlider(label: String, range: ClosedFloatingPointRange<Float>, steps: Int, onValueChange: (Float) -> Unit) {
    val sliderValue = remember { mutableFloatStateOf(range.start) }

    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

        Slider(
            value = sliderValue.floatValue,
            onValueChange = {
                sliderValue.floatValue = it
                onValueChange(it)
            },
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            for (i in range.start.toInt()..range.endInclusive.toInt()) {
                Text("$i", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuestionInput(label: String, onInput: (String) -> Unit) {
    var inputValue by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = inputValue,
            onValueChange = {
                inputValue = it
                onInput(it)
            },
            label = { Text("Votre réponse") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuestionOptionsUnique(label: String, options: List<String>, onSelectionChange: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge)

        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = {
                        selectedOption = option
                        onSelectionChange(option)
                    }
                )
                Text(option, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun QuestionOptionsMultiple(label: String, options: List<String>, onSelectionChange: (List<String>) -> Unit) {
    val selectedOptions = remember { mutableStateListOf<String>() }

    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge)

        options.forEach { option ->
            val isChecked = option in selectedOptions

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        if (isChecked) selectedOptions.remove(option)
                        else selectedOptions.add(option)

                        onSelectionChange(selectedOptions.toList())
                    }
                )
                Text(option, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}