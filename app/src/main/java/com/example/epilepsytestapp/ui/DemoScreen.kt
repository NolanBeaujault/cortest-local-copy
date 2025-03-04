package com.example.epilepsytestapp.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.epilepsytestapp.R
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
                delay(8000)
                currentStep = 5
            }
            5 -> {
                delay(5000)
                currentStep = 6
            }
            6 -> {
                delay(timeMillis = 5000)
                navController.navigate("home")
            }
        }
    }

    when (currentStep) {
        0 -> {
            HomePageStatic()
        }
        1 -> {
            InstructionScreen()
        }
        2 -> {
            InstructionScreen2(navController,
                onNextStep = { currentStep = 3 })
        }
        3 -> {
            InstructionScreen3()
        }
        4 -> {
            InstructionScreen4()
        }
        5 -> {
            InstructionScreen5(navController,
                onNextStep = { currentStep = 6 })
        }
        6 -> {
            InstructionScreen6()
        }
    }
}

@Composable
fun HomePageStatic() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bandeau bleu clair en haut
        Box(
            modifier = Modifier.fillMaxWidth().height(75.dp).background(Color(0xFFD0EEED)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Home",
                    fontSize = 28.sp,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_user_foreground),
                    contentDescription = "Profil",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dernier test
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(Color(0xFFD0EEED)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Dernier test\nDate : XX/XX/XXXX Durée : XX:XX",
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prochain RDV
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(Color(0xFFD0EEED)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Prochain RDV\nle XX/XX/XXXX avec Dr XXX",
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texte et flèche pour "Commencer un test"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Une crise se déclenche. Toi\nou un proche qui se tient à\ncôté de toi peut alors\ncliquer sur \"Commencer\nun test\".",
                fontSize = 20.sp,
                color = Blue40,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                contentDescription = "Flèche vers Commencer un test",
                modifier = Modifier.size(60.dp)
            )
        }

        // Bouton Commencer un test (désactivé)
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(Color(0xFF31D2B6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Commencer un test",
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bandeau bleu foncé en bas
        Box(
            modifier = Modifier.fillMaxWidth().height(70.dp).background(Color(0xFF2B4765))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_home_foreground),
                    contentDescription = "Home",
                    modifier = Modifier.size(50.dp)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_calendar_foreground),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(50.dp)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_files_foreground),
                    contentDescription = "Files",
                    modifier = Modifier.size(50.dp)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_settings_foreground),
                    contentDescription = "Settings",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun InstructionScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Suis les instructions",
                fontSize = 24.sp,
                color = Blue40,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                contentDescription = "Flèche vers consigne",
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Quel est ton mot code ?",
                fontSize = 28.sp,
                color = Color.White
            )
            Row(
                modifier = Modifier

                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Image(
                    painter = painterResource(id = R.mipmap.ic_close_foreground),
                    contentDescription = "Arrêter le test",
                    modifier = Modifier.size(180.dp)
                )
                Image(
                    painter = painterResource(id = R.mipmap.ic_next_foreground),
                    contentDescription = "Instruction suivante",
                    modifier = Modifier.size(180.dp)
                )
        }
    }
}
}

@Composable
fun InstructionScreen2(navController: NavController, onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Quel est ton mot code ?",
                fontSize = 28.sp,
                color = Color.White
            )
            Text(
                text = "Passe à l'instruction suivante",
                fontSize = 24.sp,
                color = Blue40,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier.size(80.dp)
            )
        }

        // Boutons en bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier.size(80.dp)
            )

            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onNextStep() }
            )
        }
    }
}

@Composable
fun InstructionScreen3() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Lève les deux bras devant toi",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )

        // Boutons en bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier.size(80.dp)
            )

            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun InstructionScreen4() {
    LaunchedEffect(Unit) {
        delay(8000)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue40),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Plusieurs consignes vont suivre, suis-les autant que tu peux.\n" +
                    "Tu peux parler, tes gestes et tes paroles seront enregistrés et pourront ensuite être transmis à ton neurologue.",
            fontSize = 24.sp,
            color = Color(0xFF2B4765), // Bleu foncé
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun InstructionScreen5(navController: NavController, onNextStep: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onNextStep()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Lève les deux bras devant toi",
                fontSize = 28.sp,
                color = Color.White
            )
            Text(
                text = "Si la crise est finie, tu peux arrêter le test en cliquant ici",
                fontSize = 24.sp,
                color = Blue40,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_arrow_foreground),
                contentDescription = "Flèche vers bouton suivant",
                modifier = Modifier.size(80.dp)
            )
        }

        // Boutons en bas
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_close_foreground),
                contentDescription = "Bouton arrêt test",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onNextStep() }
            )

            Image(
                painter = painterResource(id = R.mipmap.ic_next_foreground),
                contentDescription = "Bouton instruction suivante",
                modifier = Modifier
                    .size(80.dp)
            )
        }
    }
}

@Composable
fun InstructionScreen6() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Blue40)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Arrêter le test",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Si tu veux finalement reprendre le test en cours, " +
                    "car la crise n'était pas finie, tu peux cliquer ici",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.mipmap.ic_arrow_foreground),
            contentDescription = "Flèche vers bouton suivant",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.mipmap.ic_check_foreground),
                    contentDescription = "check confirmation arret test",
                    modifier = Modifier.size(60.dp)
                )
                Text("Oui j'arrête le test", fontSize = 16.sp, color = Color.DarkGray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_close_foreground),
                    contentDescription = "croix revenir au test",
                    modifier = Modifier.size(60.dp)
                )
                Text("Non je continue le test", fontSize = 16.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.mipmap.ic_brain_logo_foreground),
            contentDescription = "logo",
            modifier = Modifier.size(80.dp)
        )
    }
}


