package com.example.epilepsytestapp

import ConfirmationScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.epilepsytestapp.ui.*
import com.example.epilepsytestapp.ui.theme.AppTheme
import android.content.Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Charger les patients depuis le fichier JSON
        val patients = loadPatientsFromJson(this).toMutableList()


        setContent {
            EpilepsyTestApp(patients = patients, context = this)
        }
    }
}

@Composable
fun EpilepsyTestApp(patients: MutableList<Patient>, context: Context) {
    val navController = rememberNavController()

    // État pour gérer si l'utilisateur est authentifié
    var isAuthenticated by remember { mutableStateOf(false) }

    AppTheme {
        NavigationGraph(
            navController = navController,
            patients = patients,
            isAuthenticated = isAuthenticated,
            onAuthenticated = { isAuthenticated = true },
            onSavePatients = { savePatientsToJson(context, patients) } // Callback pour sauvegarder les patients
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    patients: MutableList<Patient>,
    isAuthenticated: Boolean,
    onAuthenticated: () -> Unit,
    onSavePatients: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Écran de connexion
        composable("login") {
            LoginScreen(
                patients = patients,
                onNavigateToSignup = { navController.navigate("signup") },
                onNavigateToHome = { username, password ->
                    val isValid = patients.any { it.username == username && it.password == password }
                    if (isValid) {
                        onAuthenticated()
                        navController.navigate("home")
                    }
                }
            )
        }

        // Écran de création ou modification de compte
        /*composable("signup") {
            SignupScreen(
                patient = Patient(
                    id = patients.size + 1, // ID unique basé sur la taille actuelle
                    lastName = "",
                    firstName = "",
                    address = "",
                    neurologist = "",
                    username = "",
                    password = ""
                ),
                onSaveProfile = { updatedPatient ->
                    val index = patients.indexOfFirst { it.id == updatedPatient.id }
                    if (index != -1) {
                        patients[index] = updatedPatient
                    } else {
                        patients.add(updatedPatient)
                    }
                    onSavePatients() // Sauvegarde des modifications
                    navController.navigate("home")
                }
            )
        }*/

        // Écran d'accueil
        composable("home") {
            if (isAuthenticated) {
                HomePage(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        // Page Demo
        composable("demo/{currentInstructionIndex}") {
            if (isAuthenticated) {
                DemoScreen(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        composable("files") {
            if (isAuthenticated) {
                FilesPage(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        composable("settings") {
            if (isAuthenticated) {
                SettingsPage(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        // Test
        composable("test/{currentInstructionIndex}") {
            if (isAuthenticated) {
                TestScreen(navController = navController)
            } else {
                navController.navigate("login")
            }
        }


        // Page de confirmation
        composable("confirmation/{currentInstructionIndex}") { backStackEntry ->
            val currentInstructionIndex = backStackEntry.arguments?.getString("currentInstructionIndex")?.toIntOrNull() ?: 0
            ConfirmationScreen(
                navController = navController,
                currentInstructionIndex = currentInstructionIndex, // Passer l'index ici
                onStopTestConfirmed = {
                    navController.navigate("questionnaire") {
                        popUpTo("homepage") { inclusive = false }
                    }
                }
            )
        }



        // Page du questionnaire post-test
        composable("questionnaire") {
            PostTestQuestionnaireScreen(
                onSaveTest = {
                    navController.navigate("testEnregistre")
                }
            )
        }

        // Page test enregistré
        composable("testEnregistre") {
            TestEnregistre(navController = navController)
        }

    }
}
