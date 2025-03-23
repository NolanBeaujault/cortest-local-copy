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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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

    LaunchedEffect(currentStep) {
        when (currentStep) {
            0 -> {
                delay(5000)
                currentStep = 1
            }
            1 -> {
                delay(5000)
                currentStep = 2
            }
            2 -> {
                delay(5000)
                currentStep = 3
            }
            3 -> {
                delay(5000)
                currentStep = 4
            }
            4 -> {
                delay(7000)
                currentStep = 5
            }
            5 -> {
                delay(5000)
                currentStep = 6
            }
            6 -> {
                delay(5000)
                currentStep = 7
            }
            7 -> {
                delay(5000)
                currentStep = 8
            }
            8 -> {
                delay(5000)
                currentStep = 9
            }
            9 -> {
                delay(5000)
                currentStep = 10
            }
            10 -> {
                delay(timeMillis = 5000)
                navController.navigate("home")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    when (currentStep) {
        0 -> {
            StartScreen()
        }
        1 -> {
            HomeScreen1(onNextStep = { currentStep = 2 })
        }
        2 -> {
            InstructionScreen1()
        }
        3 -> {
            InstructionScreen2(onNextStep = { currentStep = 4 })
        }
        4 -> {
            InstructionScreen3()
        }
        5 -> {
            TextScreen1()
        }
        6 -> {
            InstructionScreen4(onNextStep = { currentStep = 7 })
        }
        7 -> {
            ConfirmationScreen1()
        }
        8 -> {
            ConfirmationScreen2(onNextStep = { currentStep = 9 })
        }
        9 -> {
            TextScreen2()
        }
        10 -> {
            EndScreen()
        }
    }
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
                    .padding(bottom = 70.dp)
            ) {
                // Rectangle bleu pâle en haut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color(0xFFD0EEED)), // Bleu pâle
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp) // Diminuer la marge horizontale pour éloigner les éléments
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
                            modifier = Modifier.padding(horizontal = 16.dp) // Espace autour du titre
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
                        text = "Dernière test\n" +
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

                // Bouton "Commencer un test"
                Button(
                    onClick = {onNextStep()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
            }

            // Phrase de démo
            Text(
                text = "Une crise se déclenche. Toi\nou un proche qui se tient à\ncôté de toi peut alors\ncliquer sur \"Commencer\nun test\".",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
                    .offset(y = (40).dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .offset(x = 20.dp, y = (-70).dp)
                    .rotate(-180f)
            )

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
                        .height(70.dp), // Hauteur de la barre de navigation
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
fun InstructionScreen1() {

    var cameraError by remember { mutableStateOf(false) }

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

        // Consigne au centre de l'écran
        Text(
            text = "Quel est ton mot code ?",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            maxLines = 2
        )

        //Phrases de démo
        Text(
            text = "Suis les instructions",
            fontSize = 24.sp,
            color = Blue40,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (130).dp)
        )
        Image(
            painter = painterResource(id = R.mipmap.ic_arrow_foreground),
            contentDescription = "Flèche vers bouton suivant",
            modifier = Modifier.size(120.dp)
                .align(Alignment.Center)
                .offset(x = 20.dp, y = 60.dp)
                .rotate(-180f)
        )

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

        // Consigne au centre de l'écran
        Text(
            text = "Quel est ton mot code ?",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            maxLines = 2
        )

        //Phrases de démo
        Text(
            text = "Passe à l'instruction suivante",
            fontSize = 24.sp,
            color = Blue40,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (180).dp)
        )
        Image(
            painter = painterResource(id = R.mipmap.ic_arrow_foreground),
            contentDescription = "Flèche vers bouton suivant",
            modifier = Modifier.size(120.dp)
                .align(Alignment.Center)
                .offset(x = 20.dp, y = 250.dp)
                .rotate(-30f)
        )

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
fun InstructionScreen3() {
    var cameraError by remember { mutableStateOf(false) }

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

        // Consigne au centre de l'écran
        Text(
            text = "Lève les deux bras devant toi",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            maxLines = 2
        )

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
            )
        }
    }
}



@Composable
fun TextScreen1() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Plusieurs consignes vont\nsuivre, suis-les autant que\ntu peux. Tu peux parler,\ntes gestes et tes paroles\nseront enregistrés et\npourront ensuite être\ntransmis à ton\nneurologue.",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 40.sp)
        )
    }
}

@Composable
fun InstructionScreen4(onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }

    var cameraError by remember { mutableStateOf(false) }

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

        // Consigne au centre de l'écran
        Text(
            text = "Lève les deux bras devant toi",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
            maxLines = 2
        )

        //Phrases de démo
        Text(
            text = "Si la crise est finie, tu peux\narrêter le test en cliquant ici",
            fontSize = 24.sp,
            color = Blue40,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (180).dp)
                .padding(5.dp)
        )
        Image(
            painter = painterResource(id = R.mipmap.ic_arrow_foreground),
            contentDescription = "Flèche vers bouton suivant",
            modifier = Modifier.size(120.dp)
                .align(Alignment.Center)
                .offset(x = -20.dp, y = 250.dp)
                .rotate(30f)
                .scale(-1f, 1f)
        )

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
fun ConfirmationScreen1() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
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
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Phrases de démo
            Text(
                text = "Si tu veux finalement\nreprendre le test en cours,\ncar la crise n'était pas finie,\ntu peux cliquer ici",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
                    .offset(y = (-110).dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .offset(x = 20.dp, y = (-20).dp)
                    .rotate(-20f)
            )

            // Boutons d'action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(130.dp))

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
                        Text("Oui j'arrête\nle test", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center,)
                    }

                    // Bouton Non (Retour à l'origine)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_close_foreground),
                            contentDescription = "Non je continue le test",
                            modifier = Modifier.size(140.dp)
                        )
                        Text("Non je continue\nle test", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center,)
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
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmationScreen2(onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
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
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Phrases de démo
            Text(
                text = "Si la crise est bien finie,\nclique ici",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
                    .offset(y = (-90).dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_dark_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .offset(x = -20.dp, y = (-20).dp)
                    .rotate(20f)
                    .scale(-1f, 1f)
            )

            // Boutons d'action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(130.dp))

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
                        Text("Oui j'arrête\nle test", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center,)
                    }

                    // Bouton Non (Retour à l'origine)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_close_foreground),
                            contentDescription = "Non je continue le test",
                            modifier = Modifier.size(140.dp)
                        )
                        Text("Non je continue\nle test", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center,)
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
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )
            }
        }
    }
}


@Composable
fun TextScreen2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tu peux ensuite remplir\n" +
                    "un questionnaire post-\n" +
                    "crise afin de revenir plus\n" +
                    "en détail sur des ressentis\n" +
                    "que tu pourrais partager à\n" +
                    "ton neurologue.",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 40.sp)
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