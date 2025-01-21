package com.example.epilepsytestapp

import ConfirmationScreen
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.epilepsytestapp.model.Patient
import com.example.epilepsytestapp.ui.*
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.launch
import loadPatientsFromNetwork
import androidx.appcompat.app.AlertDialog


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (!allGranted) {
                Toast.makeText(
                    this,
                    "Certaines autorisations sont nécessaires pour utiliser l'application.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        checkAndRequestPermissions()

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

            LaunchedEffect(Unit) {
                scope.launch {
                    try {
                        val loadedPatients = loadPatientsFromNetwork()
                        patients.addAll(loadedPatients)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to load patients: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }

            AppTheme {
                EpilepsyTestApp(
                    patients = patients,
                    context = this,
                    isLoading = isLoading,
                    startDestination = startDestination,
                    isAuthenticated = isAuthenticated,
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
    }

    private fun checkAndRequestPermissions() {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (toRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(toRequest.toTypedArray())
        }
    }
}

@Composable
fun EpilepsyTestApp(
    patients: MutableList<Patient>,
    context: Context,
    isLoading: Boolean,
    startDestination: String,
    isAuthenticated: Boolean,
    onAuthenticate: () -> Unit,
    onRememberMe: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    if (isLoading) {
        LoadingScreen()
    } else {
        AppTheme {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login") {
                    LoginScreen(
                        patients = patients,
                        onNavigateToSignup = { navController.navigate("signup") },
                        onNavigateToHome = { username, password, rememberMe ->
                            val isValid =
                                patients.any { it.username == username && it.password == password }
                            if (isValid) {
                                onAuthenticate()
                                onRememberMe(rememberMe)
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true } // Efface uniquement "login"
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
                            popUpTo("home") { inclusive = true }
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
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            patients = patients
                        )
                    } else {
                        navController.navigate("login") {
                            popUpTo("settings") { inclusive = true }
                        }
                    }
                }

                // Autres destinations sans duplication
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
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
