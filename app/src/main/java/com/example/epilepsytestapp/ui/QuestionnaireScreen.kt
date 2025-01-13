@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.epilepsytestapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PostTestQuestionnaireScreen(onSaveTest: () -> Unit) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // Ajout du défilement vertical
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titre
                Text(
                    text = "Questionnaire\nPost-test",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 40.sp, // Augmentation de la taille
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally), // Centrage du titre
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Liste des questions
                QuestionSlider("Question n°1")
                QuestionSlider("Question n°2")
                QuestionOptions("Question n°3", listOf("Oui", "Non"))
                QuestionInput("Question n°4")
                QuestionOptions(
                    "Question n°5",
                    listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5", "Option 6")
                )
                QuestionSlider("Question n°6")
                QuestionSlider("Question n°7")

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton Enregistrer
                Button(
                    onClick = onSaveTest,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF31D2B6),
                        contentColor = Color.White
                    ),
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
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionInput(question: String) {
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
        OutlinedTextField(
            value = "",
            onValueChange = {},
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                containerColor = Color(0xFFE8F5F9)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
        )
    }
}

@Composable
fun QuestionOptions(question: String, options: List<String>) {
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

        // Gestion des colonnes pour les options
        val columnCount = 2 // Nombre de colonnes
        val chunkedOptions = options.chunked(columnCount) // Diviser les options en sous-listes

        Column {
            chunkedOptions.forEach { rowOptions ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowOptions.forEach { option ->
                        val isSelected = remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f) // Chaque option prend un espace égal
                        ) {
                            Checkbox(
                                checked = isSelected.value,
                                onCheckedChange = { isSelected.value = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.size(24.dp) // Augmenter la taille de la case
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionSlider(question: String) {
    val sliderValue = remember { mutableStateOf(0f) } // Valeur initiale à 0

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
            value = sliderValue.value,
            onValueChange = { sliderValue.value = it },
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

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewPostTestQuestionnaireScreen() {
    PostTestQuestionnaireScreen(onSaveTest = { /* Action simulée */ })
}
