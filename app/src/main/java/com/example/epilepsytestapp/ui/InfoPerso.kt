package com.example.epilepsytestapp.ui

import RegisterRequest
import RetrofitInstance
import android.app.DatePickerDialog
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InfoPersoScreen(navController: NavHostController, onContinue: () -> Unit) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var neurologue by remember { mutableStateOf("") }
    var date_naissance by remember { mutableStateOf("") }
    var date_affichee by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userId = currentUser?.uid ?: ""

    // Configuration de la locale française
    val localeFF = Locale.FRANCE

    // Deux formats de date distincts
    val formatBDD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatAffichage = SimpleDateFormat("dd/MM/yyyy", localeFF)

    val context = androidx.compose.ui.platform.LocalContext.current

    // Utilisation d'une approche simplifiée pour le calendrier en français
    val showDatePicker = {
        // Sauvegarde de la locale actuelle
        val originalLocale = Locale.getDefault()

        // Définit temporairement la locale par défaut en français
        Locale.setDefault(localeFF)

        val calendar = Calendar.getInstance(localeFF)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault_Dialog),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                date_naissance = formatBDD.format(selectedDate.time)
                date_affichee = formatAffichage.format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Configuration des textes en français
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Confirmer", datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Annuler", datePickerDialog)
        datePickerDialog.setTitle("Sélectionner une date")

        // Affichage du DatePicker
        datePickerDialog.show()

        // Restauration de la locale d'origine
        Locale.setDefault(originalLocale)
    }

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
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Informations Personnelles",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prénom") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = date_affichee,
                onValueChange = {},
                label = { Text("Date de naissance") },
                readOnly = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Sélectionner une date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { showDatePicker() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = { Text("Adresse") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = neurologue,
                onValueChange = { neurologue = it },
                label = { Text("Neurologue") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (userId.isNotEmpty() && nom.isNotEmpty() && prenom.isNotEmpty() && adresse.isNotEmpty() && neurologue.isNotEmpty() && date_naissance.isNotEmpty()) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val request = RegisterRequest(userId, nom, prenom, date_naissance, adresse, neurologue)
                                RetrofitInstance.api.registerUser(request)
                                Log.d("API", "Utilisateur enregistré avec succès")
                                isLoading = false
                                onContinue()
                            } catch (e: Exception) {
                                Log.e("API", "Erreur lors de l'envoi : ${e.message}")
                                isLoading = false
                            }
                        }
                    } else {
                        Log.e("API", "Veuillez remplir tous les champs")
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Envoyer", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}