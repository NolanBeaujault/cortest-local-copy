package com.example.epilepsytestapp

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
import android.content.SharedPreferences
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.example.epilepsytestapp.network.loadPatientsFromNetwork
import com.example.epilepsytestapp.savefiles.SurveyEntryScreen
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

            val lastScreen = sharedPreferences.getString("lastScreen","login") ?: "login"
            val startDestination = remember {
                mutableStateOf(
                    when {
                        intent.getStringExtra("startScreen")?.startsWith("test/") == true -> {
                            intent.getStringExtra("startScreen") ?: "test/0"
                        }
                        isAuthenticated && lastScreen == "infoPerso" -> "infoPerso"
                        isAuthenticated -> "home"
                        else -> "login"
                    }
                )
            }


            LaunchedEffect(Unit) {
                firebaseAuth.addAuthStateListener { auth ->
                    isAuthenticated = auth.currentUser != null
                    sharedPreferences.edit().putBoolean("isLoggedIn", isAuthenticated).apply()
                }
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
                startDestination = startDestination.value,
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
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    if (isLoading) {
        LoadingScreen()
    } else {
        AppTheme {
            NavigationGraph(
                navController = navController,
                patients = patients,
                isAuthenticated = isAuthenticated,
                startDestination = startDestination,
                onAuthenticated = onAuthenticate,
                onRememberMe = onRememberMe,
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
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
    onRememberMe: (Boolean) -> Unit,
    onLogout: () -> Unit,
) {
    val recordedVideos = remember { mutableStateListOf<String>() }
    val cameraViewModel: CameraViewModel = viewModel()

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
                    patients.add(updatedPatient)
                    navController.navigate("infoPerso")
                },
                context = navController.context,
                patients = patients,
                onNavigateToLogin = { navController.navigate("login") },
                navController = navController
            )
        }

        composable("infoPerso") {
            Log.d("NavigationGraph", "InfoPersoScreen ajouté au graph")
            InfoPersoScreen(navController = navController, onContinue = { navController.navigate("testTypeSelectionScreen?from=signup") })
        }

        composable("testTypeSelectionScreen?from={from}") { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            TypeConfigScreen(navController = navController, from = from, cameraViewModel = cameraViewModel)
        }

        composable("testConfigScreen") {
            ConfigScreen(
                navController = navController,
                cameraViewModel = cameraViewModel
                )
        }

        composable(route = "profile"){
            ProfilePage(navController = navController)
        }

        composable("recapScreen") {
            RecapScreen(
                navController = navController,
            )
        }

        composable("settings") {
            if (isAuthenticated) {
                SettingsPage(
                    navController = navController,
                    onLogout = {
                        onLogout()
                        navController.navigate("login") {
                            popUpTo("settings") { inclusive = true }
                        }
                    },
                    onModifyConfiguration = {
                        navController.navigate("testTypeSelectionScreen?from=settings")
                    },
                    patient = patients,
                    cameraViewModel = cameraViewModel
                )
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
                FilesPage(navController = navController, patient = patients)
            } else {
                navController.navigate("login")
            }
        }

        // Test
        composable(
            route = "test/{currentInstructionIndex}",
            arguments = listOf(navArgument("currentInstructionIndex") { defaultValue = 0 })
        ) {
            if (isAuthenticated) {
                TestScreen(
                    navController = navController,
                    recordedVideos = recordedVideos,
                    cameraViewModel = cameraViewModel
                )
            } else {
                navController.navigate("login")
            }
        }


        composable("confirmation") {
            ConfirmationScreen(
                navController = navController, // Pass the navController here
                recordedVideos = recordedVideos,
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

        composable("survey_entry") {
            SurveyEntryScreen(navController = navController)
        }


        composable("testEnregistre") {
            TestEnregistre(navController = navController)
        }

    }
}