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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.NavType
import androidx.navigation.navArgument


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

            var isAuthenticated by remember {
                mutableStateOf(sharedPreferences.getBoolean("isLoggedIn", false))
            }

            val startDestination = when {
                intent.getStringExtra("startScreen") == "test" -> "test"
                isAuthenticated -> "home"
                else -> "login"
            }

            Log.d("MainActivity", "Intent startScreen: ${intent.getStringExtra("startScreen")}")
            Log.d("MainActivity", "Start destination: $startDestination")



            // Charger les patients
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
                onAuthenticate = {
                    isAuthenticated = true
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                },
                onRememberMe = { rememberMe ->
                    sharedPreferences.edit().putBoolean("isLoggedIn", rememberMe).apply()
                },
                onLogout = {
                    isAuthenticated = false
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                }
            )
        }
    }

    // Fonction de demande de permissions
    private fun requestPermissionsIfNecessary() {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isNotEmpty()) {
            // Demander les permissions manquantes
            requestPermissionsLauncher.launch(deniedPermissions.toTypedArray())
        } else {
            Log.i("MainActivity", "Toutes les permissions sont déjà accordées.")
        }
    }


    // Lancer la demande de permissions et gérer les réponses
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.i("MainActivity", "Toutes les permissions nécessaires ont été accordées.")
            } else {
                val deniedPermissions = permissions.filter { !it.value }
                Log.e("MainActivity", "Permissions refusées : $deniedPermissions")
                Toast.makeText(
                    this,
                    "Certaines autorisations sont nécessaires pour utiliser l'application.",
                    Toast.LENGTH_LONG
                ).show()
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
    onRememberMe: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var dynamicStartDestination by remember { mutableStateOf(startDestination) }

    if (isLoading) {
        LoadingScreen()
    } else {
        AppTheme {
            LaunchedEffect(isAuthenticated) {
                // Met à jour la destination de départ si l'utilisateur est authentifié
                dynamicStartDestination = if (isAuthenticated) "home" else "login"
                if (isAuthenticated) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            NavigationGraph(
                navController = navController,
                patients = patients,
                isAuthenticated = isAuthenticated,
                startDestination = dynamicStartDestination,
                onAuthenticated = onAuthenticate,
                onSavePatients = { savePatientsToJson(context, patients) },
                onRememberMe = onRememberMe,
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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
    onRememberMe: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                patients = patients,
                onNavigateToSignup = { navController.navigate("signup") },
                onNavigateToHome = { username, password, rememberMe ->
                    // Validation des identifiants
                    val isValid = patients.any { it.username == username && it.password == password }
                    if (isValid) {
                        // Mettre à jour l'état d'authentification
                        onAuthenticated()
                        onRememberMe(rememberMe)

                        // Navigation vers la page d'accueil
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true } // Supprimer "login" de la pile
                        }
                    }
                    isValid
                }
            )
        }

        composable("home") {
            if (isAuthenticated) {
                HomePage(navController = navController, patient = patients)
            } else {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true } // Éviter de revenir en arrière
                }
            }
        }

        composable("settings") {
            if (isAuthenticated) {
                SettingsPage(
                    navController = navController,
                    onLogout = {
                        onLogout()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true } // Supprimer "settings" de la pile
                        }
                    },
                    patients = patients
                )
            } else {
                navController.navigate("login")
            }
        }

        composable("calendar") {
            if (isAuthenticated) {
                CalendarPage(navController = navController)
            } else {
                navController.navigate("login")
            }
        }

        composable("files") {
            if (isAuthenticated) {
                FilesPage(navController = navController, patient = patients)
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

        composable("questionnaire") {
            PostTestQuestionnaireScreen(
                onSaveTest = {
                    navController.navigate("testEnregistre")
                }
            )
        }

        composable("testEnregistre") {
            TestEnregistre(navController = navController)
        }

        composable(
            route = "profile/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId")
            val patient = patients.find { it.id == patientId }

            if (patient != null) {
                ProfilePage(
                    patients = patients,
                    navController = navController
                )
            } else {
                // Si le patient n'est pas trouvé, retourner à la page d'accueil
                navController.popBackStack()
            }
        }

    }
}}
