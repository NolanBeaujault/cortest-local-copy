package com.example.epilepsytestapp.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.theme.AppTheme
import kotlinx.coroutines.delay
import com.example.epilepsytestapp.ui.theme.Blue40

@Composable
fun DemoScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(0) }
    var auto by remember { mutableStateOf(true) } // Valeur par défaut

    LaunchedEffect(currentStep) {
        when (currentStep) {
            0 -> delay(3000).also { currentStep = 1 }
            1 -> {} // Rien ici car on attend le choix
            2 -> delay(5000).also { currentStep = 3 }
            3 -> delay(5000).also { currentStep = 4 }
            4 -> delay(7000).also { currentStep = 5 }
            5 -> delay(5000).also { currentStep = 6 }
            6 -> delay(5000).also { currentStep = 7 }
            7 -> delay(5000).also { currentStep = 8 }
            8 -> delay(5000).also { currentStep = 9 }
            9 -> delay(5000).also { currentStep = 10 }
            10 -> delay(5000).also { currentStep = 11 }
            11 -> delay(3000).also { navController.navigate("home") }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
            0 -> StartScreen()
            1 -> ChoixScreen(
                onChooseAuto = { auto = true; currentStep = 2 },
                onChooseHetero = { auto = false; currentStep = 2 }
            )
            2 -> HomeScreen1(onNextStep = { currentStep = 3 })
            3 -> InstructionScreen1(auto, onNextStep = { currentStep = 4 })
            4 -> InstructionScreen2(onNextStep = { currentStep = 5 })
            5 -> InstructionScreen3(auto, onNextStep = { currentStep = 6 })
            6 -> TextScreen1(auto)
            7 -> InstructionScreen4(auto, onNextStep = { currentStep = 8 })
            8 -> ConfirmationScreen1(onNextStep = { currentStep = 9 })
            9 -> ConfirmationScreen2(onNextStep = { currentStep = 10 })
            10 -> TextScreen2(auto)
            11 -> EndScreen()
        }

        // Ajout d'un bouton en haut à droite pour quitter la démo
        Image(
            painter = painterResource(id = R.mipmap.ic_fin_demo_simple_foreground),
            contentDescription = "Bouton arrêt démo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(60.dp)
                .clickable { navController.navigate("home") }
        )
    }
}




@Composable
fun StartScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "La démo va commencer !",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChoixScreen(onChooseAuto: () -> Unit, onChooseHetero: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40)
            .padding(20.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choisis ton mode de test : ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onChooseAuto) {
                Text(
                    text = "Test Auto",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = onChooseHetero) {
                Text(
                    text = "Test Hétéro",
                    fontSize = 24.sp
                )
            }
        }
    }
}



@Composable
fun HomeScreen1(onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }

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
                    .padding(bottom = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Rectangle bleu pâle en haut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0xFFD0EEED)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp)
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 16.dp)
                        )
                        // Titre "Home"
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        // Icône utilisateur
                        Image(
                            painter = painterResource(id = R.mipmap.ic_user_foreground),
                            contentDescription = "Profil",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 16.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Dernier test avec cadre bleu foncé
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Réduction de la largeur
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp)) // Bordure bleu foncé
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dernier test\n" +
                                "date : XX/XX/XXXX   durée : XX:XX",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prochain rendez-vous avec cadre bleu foncé
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Réduction de la largeur
                        .border(2.dp, Color(0xFF004D61), RoundedCornerShape(12.dp)) // Bordure bleu foncé
                        .background(Color(0xFFD0EEED), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Prochain RDV\n" +
                                "le XX/XX/XXXX à lieu avec Dr XXX",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Bouton "Commencer un test"
                    Button(
                        onClick = { onNextStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF31D2B6)
                        )
                    ) {
                        Text(
                            text = "Commencer un test",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 30.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Image(
                        painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                        contentDescription = "Flèche vers bouton suivant",
                        modifier = Modifier
                            .size(90.dp)
                            .rotate(-180f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Une crise se déclenche. Tu peux alors cliquer sur \"Commencer un test\".",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

            }


            // Barre de navigation en bas
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF2B4765))
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_home_foreground),
                        contentDescription = "Home",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(0.5.dp)
                    )
                    Image(
                        painter = painterResource(id = R.mipmap.ic_demo_foreground),
                        contentDescription = "Demo",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(0.5.dp)
                    )
                    Image(
                        painter = painterResource(id = R.mipmap.ic_files_foreground),
                        contentDescription = "Files",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(0.5.dp)
                    )
                    Image(
                        painter = painterResource(id = R.mipmap.ic_settings_foreground),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(0.5.dp)
                    )
                }

            }
        }
    }
}


@Composable
fun InstructionScreen1(auto: Boolean, onNextStep: () -> Unit) {

    var cameraError by remember { mutableStateOf(false) }

    // Récupération de la hauteur de l'écran via LocalConfiguration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Fond noir au lieu de la CameraPreview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // Affichage du message d'erreur si nécessaire
        if (cameraError) {
            Text(
                text = "Impossible d'accéder à la caméra",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = screenHeight / 2, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Consigne au centre de l'écran
                Text(
                    text = "Quel est ton mot code ?",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                //Phrase de démo
                Image(
                    painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                    contentDescription = "Flèche vers bouton suivant",
                    modifier = Modifier.size(120.dp)
                        .rotate(-180f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (auto) "Suis les instructions" else "Lis les instructions à voix haute",
                    fontSize = 24.sp,
                    color = Blue40,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Boutons en bas de l'écran
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image de croix
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
            )

            // Image de flèche
            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
                    .clickable { onNextStep() }
            )
        }
    }
}

@Composable
fun InstructionScreen2(onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }


    var cameraError by remember { mutableStateOf(false) }

    // Récupération de la hauteur de l'écran via LocalConfiguration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Fond noir au lieu de la CameraPreview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // Affichage du message d'erreur si nécessaire
        if (cameraError) {
            Text(
                text = "Impossible d'accéder à la caméra",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = screenHeight / 2, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Consigne au centre de l'écran
                Text(
                    text = "Quel est ton mot code ?",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(70.dp))

                //Phrase de démo
                Text(
                    text = "Passe à l'instruction suivante",
                    fontSize = 24.sp,
                    color = Blue40,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Image(
                    painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                    contentDescription = "Flèche vers bouton suivant",
                    modifier = Modifier.size(120.dp)
                        .rotate(-30f)
                )
            }
        }

        // Boutons en bas de l'écran
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image de croix
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
            )

            // Image de flèche
            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
                    .clickable { onNextStep() }
            )
        }
    }
}


@Composable
fun InstructionScreen3(auto: Boolean, onNextStep: () -> Unit) {

    var cameraError by remember { mutableStateOf(false) }

    // Récupération de la hauteur de l'écran via LocalConfiguration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Fond noir au lieu de la CameraPreview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // Affichage du message d'erreur si nécessaire
        if (cameraError) {
            Text(
                text = "Impossible d'accéder à la caméra",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = screenHeight / 2, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Consigne au centre de l'écran
                Text(
                    text = if (auto) "Montre la main gauche à la caméra" else  "Lève les deux bras devant toi",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Boutons en bas de l'écran
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image de croix
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
            )

            // Image de flèche
            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
                    .clickable { onNextStep() }
            )
        }
    }
}



@Composable
fun TextScreen1(auto: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (auto) "Plusieurs consignes vont suivre, suis-les autant que tu peux. Tu peux parler, tes gestes et tes paroles seront enregistrés et pourront ensuite être transmis à ton neurologue." else "Plusieurs consignes vont suivre. Vous pouvez parler, vos gestes et paroles seront enregistrés et pourront ensuite être transmis au neurologue.",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 40.sp),
            modifier = Modifier.padding(horizontal = 24.dp),
            maxLines = 10,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun InstructionScreen4(auto: Boolean, onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }


    var cameraError by remember { mutableStateOf(false) }

    // Récupération de la hauteur de l'écran via LocalConfiguration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Fond noir au lieu de la CameraPreview
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // Affichage du message d'erreur si nécessaire
        if (cameraError) {
            Text(
                text = "Impossible d'accéder à la caméra",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = screenHeight / 2, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Consigne au centre de l'écran
                Text(
                    text = if (auto) "Montre la main gauche à la caméra" else  "Lève les deux bras devant toi",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(50.dp))

                //Phrase de démo
                Text(
                    text = "Si la crise est finie, tu peux arrêter le test en cliquant ici",
                    fontSize = 24.sp,
                    color = Blue40,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Image(
                    painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                    contentDescription = "Flèche vers bouton suivant",
                    modifier = Modifier.size(120.dp)
                        .rotate(30f)
                        .scale(-1f, 1f)
                )
            }
        }

        // Boutons en bas de l'écran
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image de croix
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
                    .clickable { onNextStep() }
            )

            // Image de flèche
            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp)
            )
        }
    }
}


@Composable
fun ConfirmationScreen1(onNextStep: () -> Unit) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    //.fillMaxHeight(0.7f)
                    .align(Alignment.TopCenter)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titre
                Text(
                    text = "Arrêter\nle test\nen cours ?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 60.sp,
                        lineHeight = 60.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(top = 60.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Phrases de démo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Si tu veux finalement reprendre\nle test en cours, car la crise n'était\npas finie, tu peux cliquer ici",
                        fontSize = (18.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))


                    // Boutons d'action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Bouton Oui (Check)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_check_foreground),
                                contentDescription = "Oui j'arrête le test",
                                modifier = Modifier.size(140.dp)
                            )
                            Text(
                                "Oui j'arrête\nle test",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                            )
                        }

                        // Bouton Non (Retour à l'origine)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_close_foreground),
                                contentDescription = "Non je continue le test",
                                modifier = Modifier.size(140.dp)
                                    .clickable { onNextStep() }
                            )
                            Text(
                                "Non je continue\nle test",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                            )

                    }
                }
            }

            // Logo en bas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )
            }

            Image(
                painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.38f)
                    .size(90.dp)
                    .rotate(-20f)
            )

        }
    }
}


@Composable
fun ConfirmationScreen2(onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    //.fillMaxHeight(0.7f)
                    .align(Alignment.TopCenter)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titre
                Text(
                    text = "Arrêter\nle test\nen cours ?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 60.sp,
                        lineHeight = 60.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(top = 60.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Phrases de démo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Si la crise\nest bien finie,\nclique ici",
                        fontSize = (20.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                // Boutons d'action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Bouton Oui (Check)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_check_foreground),
                            contentDescription = "Oui j'arrête le test",
                            modifier = Modifier.size(140.dp)
                                .clickable { onNextStep() }
                        )
                        Text(
                            "Oui j'arrête\nle test",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                    }

                    // Bouton Non (Retour à l'origine)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_close_foreground),
                            contentDescription = "Non je continue le test",
                            modifier = Modifier.size(140.dp)
                                .clickable { onNextStep() }
                        )
                        Text(
                            "Non je continue\nle test",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )

                    }
                }
            }

            // Logo en bas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )
            }

            Image(
                painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.38f)
                    .size(90.dp)
                    .rotate(20f)
                    .scale(-1f, 1f)
            )

        }
    }
}


@Composable
fun TextScreen2(auto: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (auto) "Tu peux ensuite remplir un questionnaire post crise afin de revenir plus en détail sur des ressentis que tu pourrais partager à ton neurologue." else "Un questionnaire post crise sera ensuite disponible afin de revenir plus en détail sur des ressentis à partager au neurologue.",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 40.sp),
            modifier = Modifier.padding(horizontal = 24.dp),
            maxLines = 10,
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Composable
fun EndScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "La démo est terminée !",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
