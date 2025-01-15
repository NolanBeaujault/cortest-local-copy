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
import com.example.epilepsytestapp.model.Patient
import kotlinx.coroutines.launch

import androidx.compose.runtime.rememberCoroutineScope
import loadPatientsFromNetwork

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val patients = remember { mutableStateListOf<Patient>() }
            var isLoading by remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()

            // Charger les patients à partir du réseau
            LaunchedEffect(Unit) {
                scope.launch {
                    val loadedPatients = loadPatientsFromNetwork()
                    patients.addAll(loadedPatients)
                    isLoading = false // Arrêter l'indicateur de chargement une fois les données chargées
                }
            }

            EpilepsyTestApp(
                patients = patients,
                context = this,
                isLoading = isLoading
            )
        }
    }
}





@Composable
fun EpilepsyTestApp(
    patients: MutableList<Patient>,
    context: Context,
    isLoading: Boolean
) {
    if (isLoading) {
        // Afficher un anneau de chargement
        LoadingScreen()
    } else {
        val navController = rememberNavController()
        var isAuthenticated by remember { mutableStateOf(false) }

        AppTheme {
            NavigationGraph(
                navController = navController,
                patients = patients,
                isAuthenticated = isAuthenticated,
                onAuthenticated = { isAuthenticated = true },
                onSavePatients = { savePatientsToJson(context, patients) }
            )
        }
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

        // Autres écrans sécurisés
        composable("calendar") {
            if (isAuthenticated) {
                CalendarPage(navController = navController)
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

        composable("test") {
            if (isAuthenticated) {
                TestScreen(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        // Page de confirmation
        composable("confirmation") {
            ConfirmationScreen(
                onStopTestConfirmed = {
                    navController.navigate("questionnaire")
                },
                onCancelTest = {
                    navController.navigate("test")
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
