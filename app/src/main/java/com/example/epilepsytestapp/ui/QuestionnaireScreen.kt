@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.epilepsytestapp.ui

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
import com.example.epilepsytestapp.pdf.saveQuestionnaireAsPDF

@Composable
fun PostTestQuestionnaireScreen(onSaveTest: () -> Unit) {
    val context = LocalContext.current
    val questionnaireAnswers = remember { mutableStateListOf<String>() }

    // Initialiser les réponses pour chaque question
    val numQuestions = 7
    for (i in 0 until numQuestions) {
        if (questionnaireAnswers.size <= i) questionnaireAnswers.add("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Titre
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

        // Questions
        QuestionSlider("Question n°1", onValueChange = { value -> questionnaireAnswers[0] = value.toString() })
        QuestionSlider("Question n°2", onValueChange = { value -> questionnaireAnswers[1] = value.toString() })
        QuestionOptions(
            "Question n°3", listOf("Oui", "Non"),
            onSelectionChange = { selection -> questionnaireAnswers[2] = selection }
        )
        QuestionInput("Question n°4", onInput = { answer -> questionnaireAnswers[3] = answer })
        QuestionOptions(
            "Question n°5", listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6"),
            onSelectionChange = { selection -> questionnaireAnswers[4] = selection }
        )
        QuestionSlider("Question n°6", onValueChange = { value -> questionnaireAnswers[5] = value.toString() })
        QuestionSlider("Question n°7", onValueChange = { value -> questionnaireAnswers[6] = value.toString() })

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Enregistrer
        Button(
            onClick = {
                val pdfFile = saveQuestionnaireAsPDF(
                    context = context,
                    questionnaireData = questionnaireAnswers,
                    fileName = "Questionnaire_${System.currentTimeMillis()}"
                )
                if (pdfFile != null) {
                    Toast.makeText(context, "Questionnaire enregistré en PDF : ${pdfFile.name}", Toast.LENGTH_LONG).show()
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

        // Logo en bas
        Image(
            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun QuestionInput(question: String, onInput: (String) -> Unit) {
    var inputValue by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
        OutlinedTextField(
            value = inputValue,
            onValueChange = { newValue ->
                inputValue = newValue
                onInput(newValue)
            },
            label = { Text(text = "Votre réponse") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun QuestionOptions(question: String, options: List<String>, onSelectionChange: (String) -> Unit) {
    val selectedOption = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = (selectedOption.value == option),
                    onCheckedChange = {
                        selectedOption.value = if (it) option else ""
                        onSelectionChange(selectedOption.value)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionSlider(question: String, onValueChange: (Float) -> Unit) {
    val sliderValue = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = sliderValue.floatValue,
            onValueChange = {
                sliderValue.floatValue = it
                onValueChange(it)
            },
            valueRange = 0f..5f,
            steps = 4,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0..5) {
                Text(
                    text = "$i",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
