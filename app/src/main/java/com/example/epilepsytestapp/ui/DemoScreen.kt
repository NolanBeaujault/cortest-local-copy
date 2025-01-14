package com.example.epilepsytestapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.ui.theme.PrimaryColor
import com.example.epilepsytestapp.ui.theme.BackgroundColor
import com.example.epilepsytestapp.ui.theme.TextColor
import com.example.epilepsytestapp.ui.theme.ButtonTextColor


@Composable
fun DemoPage(navController: NavHostController) {
    val step = remember { mutableStateOf(1) }
    val name = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val codeWord = remember { mutableStateOf("") }
    val feeling = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Title
            Text(
                text = "Démo de l'application",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 24.sp,
                    color = TextColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (step.value) {
                1 -> DemoStep(
                    question = "Quel est votre nom et où êtes-vous ?",
                    value = name.value,
                    onValueChange = { name.value = it },
                    buttonText = "Suivant",
                    onButtonClick = { step.value = 2 }
                )
                2 -> DemoStep(
                    question = "Quel est votre mot code ?",
                    value = codeWord.value,
                    onValueChange = { codeWord.value = it },
                    buttonText = "Suivant",
                    onButtonClick = { step.value = 3 }
                )
                3 -> DemoStep(
                    question = "Comment vous sentez-vous ?",
                    value = feeling.value,
                    onValueChange = { feeling.value = it },
                    buttonText = "Suivant",
                    onButtonClick = { step.value = 4 }
                )
                4 -> DemoFinalStep(
                    onFinish = { step.value = 5 }
                )
                5 -> PostTestQuestionnaireScreen(onSaveTest = {})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoStep(
    question: String,
    value: String,
    onValueChange: (String) -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                color = TextColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = textFieldColors(
                containerColor = BackgroundColor,
                focusedTextColor = TextColor,
                focusedIndicatorColor = PrimaryColor,
                unfocusedIndicatorColor = TextColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = buttonText,
                color = ButtonTextColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DemoFinalStep(onFinish: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Levez les bras pendant quelques secondes.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                color = TextColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(PrimaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Fin de test",
                color = ButtonTextColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
