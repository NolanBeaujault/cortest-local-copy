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


@Composable
fun SignupScreen(
    patient: Patient,
    onSaveProfile: (Patient) -> Unit,
    context: Context, // Ajout du contexte pour accéder aux fichiers
    patients: List<Patient> // Liste actuelle des patients
) {
    var lastName by remember { mutableStateOf(patient.lastName) }
    var firstName by remember { mutableStateOf(patient.firstName) }
    var address by remember { mutableStateOf(patient.address) }
    var neurologist by remember { mutableStateOf(patient.neurologist) }
    var username by remember { mutableStateOf(patient.username) }
    var password by remember { mutableStateOf(patient.password) }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre principal
            Text(
                text = "CORTEST",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Modification du profil",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Champs d'entrée
            TextInputField(label = "Nom", value = lastName, onValueChange = { lastName = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Prénom", value = firstName, onValueChange = { firstName = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Adresse", value = address, onValueChange = { address = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Neurologue", value = neurologist, onValueChange = { neurologist = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Identifiant", value = username, onValueChange = { username = it })
            Spacer(modifier = Modifier.height(8.dp))
            TextInputField(label = "Mot de passe", value = password, onValueChange = { password = it }, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))

            // Bouton de sauvegarde
            CustomButton(
                text = "Sauvegarder le profil",
                onClick = {
                    val updatedPatient = Patient(
                        id = patient.id,
                        lastName = lastName,
                        firstName = firstName,
                        address = address,
                        neurologist = neurologist,
                        username = username,
                        password = password
                    )
                    // Mettre à jour la liste des patients
                    val updatedPatients = patients.map {
                        if (it.id == patient.id) updatedPatient else it
                    }
                    savePatientsToJson(context, updatedPatients) // Sauvegarde dans le fichier JSON
                    onSaveProfile(updatedPatient) // Callback pour gérer d'autres actions
                }
            )
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

