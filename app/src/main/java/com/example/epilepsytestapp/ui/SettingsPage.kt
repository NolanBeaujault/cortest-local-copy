package com.example.epilepsytestapp.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.model.Patient
import com.example.epilepsytestapp.ui.theme.AppTheme
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsPage(
    navController: NavHostController,
    onLogout: () -> Unit,
    onModifyConfiguration: () -> Unit,
    patient: List<Patient>,
    cameraViewModel: CameraViewModel
) {
    val isFrontCamera = cameraViewModel.isFrontCamera
    var showDialogConfig by remember { mutableStateOf(false) }
    var showDialogQuestionnaire by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showReauthDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .border(4.dp, Color(0xFF2B4765), RoundedCornerShape(1.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp) // Espace pour la barre de navigation
            ) {
                // Rectangle bleu pâle en haut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0xFFD0EEED)), // Bleu pâle
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp) // Diminuer la marge horizontale pour éloigner les éléments
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxHeight()// Taille ajustée
                                .padding(end = 16.dp) // Espace supplémentaire à droite du logo
                        )

                        // Titre "Paramètres"
                        Text(
                            text = "Paramètres",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp) // Espace autour du titre
                        )

                        // Icône utilisateur
                        Image(
                            painter = painterResource(id = R.mipmap.ic_user_foreground),
                            contentDescription = "Profil",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp)
                                .clickable {
                                    navController.navigate("profile")
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingsOption(text = "Gérer les autorisations") { openAppSettings(navController.context) }
                SettingsOption(text = "Déconnexion") { onLogout(); navController.navigate("login") }
                SettingsOption(text = "Modifier la configuration") { showDialogConfig = true }
                SettingsOption(text = "Modifier le questionnaire") {
                    showDialogQuestionnaire = true
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Ligne avec le switch caméra et ses labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Caméra",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Arrière",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = if (!isFrontCamera.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Switch(
                            checked = isFrontCamera.value,
                            onCheckedChange = { isFrontCamera.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Avant",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = if (isFrontCamera.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                DeleteAccount(
                    text = "Supprimer mon compte",
                    onClick = { showReauthDialog = true }
                )

            }

            if (showReauthDialog) {
                AlertDialog(
                    onDismissRequest = { showReauthDialog = false },
                    title = { Text("Vérification requise") },
                    text = {
                        Column {
                            Text("Veuillez entrer vos identifiants pour confirmer la suppression de votre compte.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Mot de passe") },
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            reauthenticateAndDeleteUser(
                                email = email,
                                password = password,
                                onSuccess = {
                                    Toast.makeText(navController.context, "Compte supprimé", Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                },
                                onError = {
                                    Toast.makeText(navController.context, "Erreur : ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            )
                            showReauthDialog = false
                        }) {
                            Text("Confirmer", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showReauthDialog = false }) {
                            Text("Annuler", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }

            // Affichage de la boite d'alerte avant de naviguer vers la configuration des tests
            if (showDialogConfig) {
                AlertDialog(
                    onDismissRequest = { showDialogConfig = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialogConfig = false
                            onModifyConfiguration() // Navigation vers la page de config
                        }) {
                            Text("Continuer", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogConfig = false }) {
                            Text("Retour", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "ATTENTION !",
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(36.dp)
                                    .offset(y = (-1).dp)
                            )
                            Text("ATTENTION !", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Text("La page suivante permet de modifier la configuration des tests. \n\nCette configuration ne doit être modifiée qu'avec la présence ou l'autorisation de votre neurologue, veuillez retourner en arrière si ce n'est pas le cas.")
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                )


            }

            // Affichage de la boîte d'alerte avant de naviguer vers la page de modif. du questionnaire
            if (showDialogQuestionnaire) {
                AlertDialog(
                    onDismissRequest = { showDialogQuestionnaire = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialogQuestionnaire = false
                            navController.navigate("survey_entry") // Navigation vers la modif. du questionnaire
                        }) {
                            Text("Continuer", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialogQuestionnaire = false }) {
                            Text("Retour", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "ATTENTION !",
                                tint = Color(0xFFFFA726),
                                modifier = Modifier.size(36.dp)
                                    .offset(y = (-1).dp)
                            )
                            Text("ATTENTION !", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Text("La page suivante permet de modifier le questionnaire post-test. \n\nCe questionnaire ne doit être modifié qu'avec la présence ou l'autorisation de votre neurologue, veuillez retourner en arrière si ce n'est pas le cas.")
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                )


            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    confirmButton = {
                        TextButton(onClick = {
                            deleteFirebaseAccount(
                                onSuccess = {
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(
                                        navController.context,
                                        "Compte supprimé",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                },
                                onError = {
                                    Toast.makeText(
                                        navController.context,
                                        "Erreur lors de la suppression",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                            showDeleteConfirm = false
                        }) {
                            Text("Oui", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Non", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    title = {
                        Text("Confirmer la suppression")
                    },
                    text = {
                        Text("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.")
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }


            NavigationBar(
                    navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter) // Fixe la barre en bas
            )
        }

    }
}

@Composable
fun SettingsOption(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(60.dp) // ⬅️ Hauteur augmentée
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFD0EEED))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart // ⬅️ Texte aligné à gauche verticalement centré
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}


// Fonction pour ouvrir les paramètres de l'application
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun DeleteAccount(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x99FF0000))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start
        )
    )
}

fun reauthenticateAndDeleteUser(
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val credential = EmailAuthProvider.getCredential(email, password)

    user?.reauthenticate(credential)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            user.delete()
                .addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(deleteTask.exception ?: Exception("Unknown error"))
                    }
                }
        } else {
            onError(task.exception ?: Exception("Re-authentication failed"))
        }
    }
}



fun deleteFirebaseAccount(onSuccess: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
    val user = FirebaseAuth.getInstance().currentUser

    user?.delete()
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseAuth", "Compte supprimé de Firebase")
                onSuccess()
            } else {
                Log.e("FirebaseAuth", "Erreur suppression de compte", task.exception)
                task.exception?.let { onError(it) }
            }
        }
}

class CameraViewModel : ViewModel() {
    var isFrontCamera = mutableStateOf(true)
}
