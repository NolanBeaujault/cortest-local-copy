package com.example.epilepsytestapp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.model.Patient

@Composable
fun SignupScreen(
    patient: Patient,
    onSaveProfile: (Patient) -> Unit,
    context: Context,
    patients: List<Patient>
) {
    var email by remember { mutableStateOf(patient.username) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CORTEST",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inscription",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextInputField(label = "Email", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Mot de passe", value = password, onValueChange = { password = it }, isPassword = true)
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Vérification du mot de passe", value = confirmPassword, onValueChange = { confirmPassword = it }, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "S'inscrire",
                onClick = {
                    if (password == confirmPassword) {
                        val updatedPatient = patient.copy(username = email, password = password)
                        val updatedPatients = patients.map { if (it.id == patient.id) updatedPatient else it }
                        savePatientsToJson(context, updatedPatients)
                        onSaveProfile(updatedPatient)
                    } else {
                        // Gérer le cas où les mots de passe ne correspondent pas
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun TextInputField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}
