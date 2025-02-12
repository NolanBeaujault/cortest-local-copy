package com.example.epilepsytestapp

import android.os.Bundle
import android.media.MediaRecorder
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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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
                mutableStateOf(firebaseAuth.currentUser != null)
            }

            LaunchedEffect(Unit) {
                firebaseAuth.addAuthStateListener { auth ->
                    isAuthenticated = auth.currentUser != null
                    sharedPreferences.edit().putBoolean("isLoggedIn", isAuthenticated).apply()
                }
            }

            val startDestination = when {
                intent.getStringExtra("startScreen") == "test" -> {
                    Log.d("MainActivity", "Redirection vers 'test'")
                    "test"
                }
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
                onAuthenticate = {
                    isAuthenticated = true
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                },
                onRememberMe = { rememberMe ->
                    sharedPreferences.edit().putBoolean("isLoggedIn", rememberMe).apply()
                },
                onLogout = {
                    isAuthenticated = false
                    firebaseAuth.signOut()
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                },
                mediaRecorder = mediaRecorder
            )
        }
    }

    private fun requestPermissionsIfNecessary() {
        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filter { !it.value }
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Certaines autorisations (Caméra et Microphone) sont nécessaires pour utiliser certaines fonctionnalités de l'application.",
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
    onRememberMe: (Boolean) -> Unit,
    onLogout: () -> Unit,
    mediaRecorder: MediaRecorder
) {
    val navController = rememberNavController()

    if (isLoading) {
        LoadingScreen()
    } else {
        AppTheme {
            LaunchedEffect(startDestination) {
                Log.d("MainActivity", "startDestination = $startDestination")
                if (startDestination == "test") {
                    navController.navigate("test") {
                        popUpTo(0) { inclusive = true } // Supprimer toute la pile
                    }
                } else if (isAuthenticated) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            NavigationGraph(
                navController = navController,
                patients = patients,
                isAuthenticated = isAuthenticated,
                startDestination = startDestination,
                onAuthenticated = onAuthenticate,
                onSavePatients = { savePatientsToJson(context, patients) },
                onRememberMe = onRememberMe,
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                mediaRecorder = mediaRecorder
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
    onLogout: () -> Unit,
    mediaRecorder: MediaRecorder
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            if (isAuthenticated) {
                HomePage(navController = navController, patient = patients)
            } else {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onNavigateToSignup = { navController.navigate("signup") },
                onAuthenticated = { navController.navigate("home") }
            )
        }


        composable("signup") {
            SignupScreen(
                patient = Patient(id = 0, username = "", password = "", lastName = "", firstName = "", address = "", neurologist = "", tests = emptyList()),
                onSaveProfile = { updatedPatient ->
                    patients.add(updatedPatient) // Ajouter le patient à la liste
                    navController.navigate("login") // Redirection vers la page de connexion après l'inscription
                },
                context = navController.context,
                patients = patients,
                onNavigateToLogin = { navController.navigate("login") }
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
                            popUpTo("settings") { inclusive = true } // Supprimer "settings" de la pile
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
}