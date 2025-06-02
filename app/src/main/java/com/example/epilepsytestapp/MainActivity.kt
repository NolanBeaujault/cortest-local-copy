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
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.epilepsytestapp.network.loadPatientsFromNetwork
import com.example.epilepsytestapp.savefiles.SurveyEntryScreen
import com.google.firebase.auth.FirebaseAuth


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
            val sharedViewModel: SharedViewModel = viewModel()
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser


            var isAuthenticated by remember {
                mutableStateOf(firebaseAuth.currentUser != null)
            }

            val lastScreen = sharedPreferences.getString("lastScreen","login") ?: "login"
            val startDestination = remember {
                val startScreen = intent.getStringExtra("startScreen")
                Log.d("MainActivity", "Received startScreen: $startScreen")
                mutableStateOf(
                    if (!startScreen.isNullOrEmpty()) {
                        Log.d("MainActivity", "Widget navigates to: $startScreen")
                        startScreen // Priorise la destination venant du widget
                    } else {
                        when {
                            isAuthenticated && lastScreen == "infoPerso" -> {
                                Log.d("MainActivity", "Navigates to: infoPerso")
                                "infoPerso"
                            }
                            isAuthenticated -> {
                                Log.d("MainActivity", "Navigates to: home")
                                "home"
                            }
                            else -> {
                                Log.d("MainActivity", "Navigates to: login")
                                "login"
                            }
                        }
                    }
                )
            }

            val widgetStartScreen = remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val screenFromIntent = intent.getStringExtra("startScreen")
                if (!screenFromIntent.isNullOrEmpty()) {
                    widgetStartScreen.value = screenFromIntent
                }
            }

            LaunchedEffect(Unit) {
                firebaseAuth.addAuthStateListener { auth ->
                    isAuthenticated = auth.currentUser != null
                    sharedPreferences.edit().putBoolean("isLoggedIn", isAuthenticated).apply()
                }
            }

            LaunchedEffect(currentUser) {
                currentUser?.uid?.let { userId ->
                    try {
                        val response = com.example.epilepsytestapp.network.RetrofitInstance.api.getUserProfile(userId)
                        sharedViewModel.setMotCode(response.mot_code ?: "")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Erreur de chargement du mot code : ${e.message}")
                    }
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
                isLoading = isLoading,
                isAuthenticated = isAuthenticated,
                startDestination = startDestination.value,
                onLogout = {
                    isAuthenticated = false
                    firebaseAuth.signOut()
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                },
                widgetStartScreen = widgetStartScreen.value
            )
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // Mettez à jour l'intention reçue
        val startScreen = intent?.getStringExtra("startScreen")
        Log.d("MainActivity", "New intent received: startScreen = $startScreen")
        if (!startScreen.isNullOrEmpty()) {
            // Naviguez directement vers la destination spécifiée par le widget
            recreate() // Recharge l'activité avec la nouvelle intention
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
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            }
        }


@Composable
fun EpilepsyTestApp(
    patients: MutableList<Patient>,
    isLoading: Boolean,
    isAuthenticated: Boolean,
    startDestination: String,
    onLogout: () -> Unit,
    widgetStartScreen: String? // <-- ADD THIS PARAMETER
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
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                widgetStartScreen = widgetStartScreen
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
    onLogout: () -> Unit,
    sharedViewModel: SharedViewModel = viewModel(),
    widgetStartScreen: String?,
) {
    val recordedVideos = remember { mutableStateListOf<String>() }
    val cameraViewModel: CameraViewModel = viewModel()

    LaunchedEffect(widgetStartScreen) {
        widgetStartScreen?.let { target ->
            if (target.isNotEmpty()) {
                navController.navigate(target) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            if (isAuthenticated) {
                HomePage(navController = navController)
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

        composable("configHistoryScreen") {
            ConfigHistoryScreen(navController = navController)
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

        composable("test") {
            TestScreen(
                navController = navController,
                recordedVideos = recordedVideos,
                cameraViewModel = cameraViewModel,
                sharedViewModel = sharedViewModel
            )
        }

        composable("confirmation") {
            ConfirmationScreen(
                navController = navController,
                recordedVideos = recordedVideos,
                sharedViewModel = sharedViewModel,
                onStopTestConfirmed = {
                    navController.navigate("questionnaire")
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

        composable("emergency") {
            EmergencyPage(navController = navController)
        }

        composable("survey_entry") {
            SurveyEntryScreen(navController = navController)
        }

        composable("info") {
            InfoPage(navController = navController)
        }


        composable("testEnregistre") {
            TestEnregistre(navController = navController)
        }
    }
}