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

import android.content.SharedPreferences

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        // Vérifier et demander les autorisations nécessaires
        requestPermissionsIfNecessary()

        setContent {
            val patients = remember { mutableStateListOf<Patient>() }
            var isLoading by remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()

            // Vérifier si l'utilisateur est déjà connecté
            var isAuthenticated by remember {
                mutableStateOf(sharedPreferences.getBoolean("isLoggedIn", false))
            }

            val startDestination = when {
                intent.getStringExtra("startScreen") == "test" -> "test"
                isAuthenticated -> "home"
                else -> "login"
            }

            LaunchedEffect(Unit) {
                scope.launch {
                    val loadedPatients = loadPatientsFromNetwork()
                    patients.addAll(loadedPatients)
                    isLoading = false
                }
            }

            EpilepsyTestApp(
                patients = patients,
                context = this,
                isLoading = isLoading,
                isAuthenticated = isAuthenticated,
                startDestination = startDestination,
                onAuthenticate = { isAuthenticated = true },
                onRememberMe = { rememberMe ->
                    sharedPreferences.edit().putBoolean("isLoggedIn", rememberMe).apply()
                }
            )
        }
    }

    private fun requestPermissionsIfNecessary() {
        // Liste des permissions nécessaires
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )

        // Filtrer les permissions qui ne sont pas encore accordées
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // Si des permissions doivent être demandées, les demander
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Vérifiez si toutes les autorisations ont été accordées
            val allGranted = permissions.entries.all { it.value }
            if (!allGranted) {
                Toast.makeText(
                    this,
                    "Certaines autorisations sont nécessaires pour utiliser l'application.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}


@Composable
fun EpilepsyTestApp(
    patients: MutableList<Patient>,
    context: Context,
    isLoading: Boolean,
    isAuthenticated: Boolean,
    startDestination: String,
    onAuthenticate: () -> Unit,
    onRememberMe: (Boolean) -> Unit
) {
    if (isLoading) {
        LoadingScreen()
    } else {
        val navController = rememberNavController()

        AppTheme {
            NavigationGraph(
                navController = navController,
                patients = patients,
                isAuthenticated = isAuthenticated,
                startDestination = startDestination, // Passez la destination initiale
                onAuthenticated = onAuthenticate,
                onSavePatients = { savePatientsToJson(context, patients) },
                onRememberMe = onRememberMe
            )
        }
    }
}


@Composable
fun NavigationGraph(
    navController: NavHostController,
    patients: MutableList<Patient>,
    isAuthenticated: Boolean,
    startDestination: String,
    onAuthenticated: () -> Unit,
    onSavePatients: () -> Unit,
    onRememberMe: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination // Utiliser la destination initiale
    ) {
        composable("login") {
            LoginScreen(
                patients = patients,
                onNavigateToSignup = { navController.navigate("signup") },
                onNavigateToHome = { username, password, rememberMe ->
                    val isValid = patients.any { it.username == username && it.password == password }
                    if (isValid) {
                        onAuthenticated()
                        onRememberMe(rememberMe)
                    }
                    isValid // Retourne si la connexion a réussi
                }
            )
        }


        composable("home") {
            if (isAuthenticated) {
                HomePage(navController = navController)
            } else {
                navController.navigate("login")
            }
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
