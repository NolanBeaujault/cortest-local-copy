package com.example.epilepsytestapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextInputField(
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textState = remember { mutableStateOf("") }
    val visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        androidx.compose.foundation.text.BasicTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            visualTransformation = visualTransformation,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

