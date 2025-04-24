package com.example.epilepsytestapp.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.example.epilepsytestapp.model.Patient
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import androidx.compose.ui.unit.sp

@Composable
fun SignupScreen(
    patient: Patient,
    onSaveProfile: (Patient) -> Unit,
    context: Context,
    patients: List<Patient>,
    onNavigateToLogin: () -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf(patient.username) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val firebaseAuth = remember { FirebaseAuth.getInstance() }

    LaunchedEffect(Unit) {
        if (firebaseAuth != null) {
            Log.d("FirebaseAuthTest", "FirebaseAuth est prêt à être utilisé !")
        } else {
            Log.e("FirebaseAuthTest", "Erreur : FirebaseAuth est null")
        }
    }

    AppTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
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

                // Logo
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Créer un compte",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                fun getPasswordErrors(password: String): List<String> {
                    val errors = mutableListOf<String>()

                    if (password.length < 6) errors.add("Au moins 6 caractères requis")
                    if (password.length > 4096) errors.add("Maximum 4096 caractères")
                    if (!password.any { it.isDigit() }) errors.add("Au moins un chiffre requis")
                    if (!password.any { "!@#\$%^&*(),.?\":{}|<>".contains(it) }) errors.add("Au moins un caractère spécial requis")

                    return errors
                }

                // Vérification de la correspondance des mots de passe
                val passwordErrors = getPasswordErrors(password)
                val isPasswordValid = passwordErrors.isEmpty()
                val isConfirmPasswordValid = confirmPassword == password && isPasswordValid

                // Déterminer la couleur de la bordure en fonction de la validité du mot de passe
                val passwordBorderColor =
                    if (isPasswordValid) MaterialTheme.colorScheme.primary else Color.Red
                val confirmPasswordBorderColor =
                    if (isConfirmPasswordValid) MaterialTheme.colorScheme.primary else Color.Red


                // Mot de passe
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility
                                ),
                                contentDescription = if (isPasswordVisible) "Masquer le mot de passe" else "Voir le mot de passe"
                            )
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = passwordBorderColor,
                        unfocusedBorderColor = passwordBorderColor,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (passwordErrors.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        passwordErrors.forEach { error ->
                            Text(
                                text = "-$error",
                                color = Color.Red,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Confirmation du mot de passe
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Vérification du mot de passe") },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (isConfirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility
                                ),
                                contentDescription = if (isConfirmPasswordVisible) "Masquer le mot de passe" else "Voir le mot de passe"
                            )
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = confirmPasswordBorderColor,
                        unfocusedBorderColor = confirmPasswordBorderColor,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
                if (!isConfirmPasswordValid) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Les mots de passe ne correspondent pas",
                            color = Color.Red,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Bouton d'inscription
                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            isLoading = true
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Log.d(
                                            "Signup",
                                            "Inscription réussie : ${firebaseAuth.currentUser?.email}"
                                        )
                                        navController.navigate("infoPerso") {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        Log.e("Signup", "Échec de l'inscription", task.exception)
                                        Toast.makeText(
                                            context,
                                            "Échec de l'inscription : ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            Log.e("Signup", "Les mots de passe ne correspondent pas")
                        }
                    },
                    shape = RoundedCornerShape(50),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text("S'inscrire", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Déjà un compte ? Se connecter",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun traduireErreurFirebase(message: String?): String {
    return when {
        message.isNullOrEmpty() -> "Une erreur inconnue s'est produite."
        message.contains(
            "The email address is already in use",
            ignoreCase = true
        ) -> "Cet email est déjà utilisé."

        message.contains(
            "The email address is badly formatted",
            ignoreCase = true
        ) -> "Format d'email invalide."

        message.contains(
            "Password should be at least 6 characters",
            ignoreCase = true
        ) -> "Le mot de passe doit contenir au moins 6 caractères."

        message.contains(
            "There is no user record corresponding to this identifier",
            ignoreCase = true
        ) -> "Aucun utilisateur trouvé avec cet email."

        message.contains(
            "The password is invalid or the user does not have a password",
            ignoreCase = true
        ) -> "Mot de passe incorrect."

        message.contains(
            "A network error",
            ignoreCase = true
        ) -> "Erreur réseau. Vérifiez votre connexion internet."

        else -> "Erreur : $message" // Affiche l'erreur originale si non reconnue
    }
}

