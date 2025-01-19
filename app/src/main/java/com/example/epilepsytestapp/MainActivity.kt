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
import android.content.Intent
import android.net.Uri


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

            // État de l'authentification
            var isAuthenticated by remember {
                mutableStateOf(sharedPreferences.getBoolean("isLoggedIn", false))
            }

            val startDestination = when {
                intent.getStringExtra("startScreen") == "test" -> "test"
                isAuthenticated -> "home"
                else -> "login"
            }

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
        // Liste des permissions nécessaires
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        // Ajouter MANAGE_EXTERNAL_STORAGE pour Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Filtrer les permissions qui ne sont pas encore accordées
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        println("Permissions à demander : $permissionsToRequest") // Log des permissions demandées

        // Si des permissions doivent être demandées, les demander
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            println("Toutes les permissions sont déjà accordées.")
            requestStoragePermissionIfNeeded() // Demander l'accès au stockage si nécessaire
        }
    }

    // Demander l'accès au stockage pour Android 11+
    private fun requestStoragePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
        }
    }

    // Lancer la demande de permissions et gérer les réponses
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { (permission, isGranted) ->
                println("Permission $permission accordée : $isGranted") // Log de la permission
            }

            val deniedPermissions = permissions.filter { !it.value }
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Certaines autorisations sont nécessaires pour utiliser l'application.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                requestStoragePermissionIfNeeded() // Demander à nouveau les permissions de stockage si nécessaire
            }
        }

    // Ouvrir les paramètres de l'application pour que l'utilisateur accorde les permissions
    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
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
                HomePage(navController = navController)
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
                    }
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
                FilesPage(navController = navController)
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
    }
}
