package com.example.epilepsytestapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.savefiles.saveQuestionnaireAsPDF

@Composable
fun PostTestQuestionnaireScreen(onSaveTest: () -> Unit) {
    val context = LocalContext.current
    val questionnaireAnswers = remember { mutableStateListOf<String>() }
    val questionnaireDetails = remember { mutableStateListOf<Pair<String, String>>() }

    val numQuestions = 7
    for (i in 0 until numQuestions) {
        if (questionnaireAnswers.size <= i) questionnaireAnswers.add("")
        if (questionnaireDetails.size <= i) questionnaireDetails.add(Pair("Question ${i + 1}", ""))
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
        repeat(numQuestions) { index ->
            when (val questionNumber = index + 1) {

                1 -> QuestionOptions("Est-ce une crise habituelle ?", listOf("Oui", "Non"), onSelectionChange = { selection ->

                    questionnaireAnswers[0] = selection

                    questionnaireDetails[0] = Pair("Est-ce une crise habituelle ?", "Réponse : $selection")

                })

                2 -> QuestionInput("Si non, précisez :", onInput = { answer ->

                    questionnaireAnswers[1] = answer

                    questionnaireDetails[1] = Pair("Question n°4", "Réponse : $answer")

                })

                3 -> QuestionOptions("Facteur déclenchant ?", listOf("Oui", "Non"), onSelectionChange = { selection ->

                    questionnaireAnswers[2] = selection

                    questionnaireDetails[2] = Pair("Facteur déclenchant ?", "Réponse : $selection")

                })

                4 -> QuestionInput("Si oui, lequel ?", onInput = { answer ->

                    questionnaireAnswers[3] = answer

                    questionnaireDetails[3] = Pair("Si oui, lequel ?", "Réponse : $answer")

                })

                5 -> QuestionSlider("Contexte de fatigue :",

                    onValueChange = { value ->

                        questionnaireAnswers[4] = value.toString()

                        questionnaireDetails[4] = Pair("Contexte de fatigue :", "Valeur : $value")

                    })

                6 -> QuestionSlider("Contexte de stress :",

                    onValueChange = { value ->

                        questionnaireAnswers[5] = value.toString()

                        questionnaireDetails[5] = Pair("Contexte de stress :", "Valeur : $value")

                    })

                7 -> QuestionOptions("Changement/oubli de traitement ces dernières 24h ?", listOf("Oui", "Non"), onSelectionChange = { selection ->

                    questionnaireAnswers[6] = selection

                    questionnaireDetails[6] = Pair("Changement/oubli de traitement ces dernières 24h ?", "Réponse : $selection")

                })

                8 -> QuestionInput("Commentaire libre :", onInput = { answer ->

                    questionnaireAnswers[7] = answer

                    questionnaireDetails[7] = Pair("Commentaire libre :", "Réponse : $answer")

                })

            }

        }



        Spacer(modifier = Modifier.height(24.dp))



        Button(

            onClick = {

                val pdfFile = saveQuestionnaireAsPDF(

                    context = context,

                    questionnaireData = questionnaireDetails, // Passez la liste directement

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



    Column(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 8.dp)

    ) {

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

            (0..5).forEach {

                Text(

                    text = "$it",

                    style = MaterialTheme.typography.bodyMedium,

                    color = MaterialTheme.colorScheme.primary

                )

            }

        }

    }

}



@Composable

fun QuestionSlider(question: String) {

    val sliderValue = remember { mutableFloatStateOf(0f) } // Valeur initiale à 0



    Column(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 8.dp),

        horizontalAlignment = Alignment.Start

    ) {

        // Intitulé de la question

        Text(

            text = question,

            style = MaterialTheme.typography.bodyLarge,

            color = MaterialTheme.colorScheme.primary,

            modifier = Modifier.padding(start = 8.dp)

        )



        Spacer(modifier = Modifier.height(16.dp))



        // Curseur

        Slider(

            value = sliderValue.floatValue,

            onValueChange = { sliderValue.floatValue = it },

            valueRange = 0f..5f, // Valeurs de 0 à 5

            steps = 4, // 5 étapes au total (0, 1, 2, 3, 4, 5)

            colors = SliderDefaults.colors(

                thumbColor = MaterialTheme.colorScheme.primary,

                activeTrackColor = MaterialTheme.colorScheme.primary,

                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

            )

        )



        Spacer(modifier = Modifier.height(8.dp))



        // Affichage des numéros sous le curseur

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {

            // Affichage des numéros de 0 à 5 sous le curseur

            for (i in 0..5) {

                Text(

                    text = "$i",

                    style = MaterialTheme.typography.bodyMedium,

                    color = MaterialTheme.colorScheme.primary,

                    modifier = Modifier.padding(horizontal = 4.dp)

                )

            }

        }

    }

}