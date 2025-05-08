package com.example.epilepsytestapp.category

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epilepsytestapp.R
import com.example.epilepsytestapp.ui.ImageClickable
import com.example.epilepsytestapp.ui.SharedViewModel

@Composable
fun TestDisplay(
    test: Test,
    isFrontCamera: Boolean,
    onImageClick: (String) -> Unit = {},
    sharedViewModel: SharedViewModel,
    key: Int
) {
    val context = LocalContext.current
    val elapsedTime by sharedViewModel.elapsedTime.collectAsState() // Obtenez elapsedTime ici

    var randomMot by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var motColor by remember { mutableStateOf(Color.White) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // 🎲 Sélection du mot aléatoire + couleur + audio
    LaunchedEffect(key1 = key) {
        randomMot = ""
        selectedImage = null
        motColor = Color.White

        // 🎲 Sélection du mot aléatoire + couleur + audio
        if (!test.mot_set.isNullOrEmpty()) {
            randomMot = test.mot_set.random()
            sharedViewModel.addInstructionLog(Pair(" ❓ Mot : $randomMot", elapsedTime)) // Ajout au log
        }

        // 🎨 Couleur aléatoire (différente du mot)
        if (!test.couleur.isNullOrEmpty()) {
            val filteredColors = test.couleur.filterNot { it.equals(randomMot, ignoreCase = true) }
            val colorName = filteredColors.randomOrNull()
            colorName?.let { name ->
                frenchColorToHex(name)?.let { hex ->
                    motColor = Color(android.graphics.Color.parseColor(hex))
                    sharedViewModel.addInstructionLog(Pair("\uD83C\uDFA8 Couleur sélectionné: $colorName", elapsedTime))
                }
            }
        }

        // 🎧 Audio auto si caméra frontale
        if (isFrontCamera) {
            // Audio pour la consigne
            if (test.audio.isNotEmpty()) {
                val filename = test.audio.removeSuffix(".m4a")
                val resId = context.resources.getIdentifier(filename, "raw", context.packageName)
                if (resId != 0) {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(context, resId)
                    mediaPlayer?.start()
                }
            }

            // ⏳ Attendre la fin de la consigne avant de jouer le mot (optionnel mais recommandé)
            mediaPlayer?.setOnCompletionListener {
                // Audio pour le randomMot
                if (randomMot.isNotEmpty()) {
                    val motFile = randomMot.lowercase().replace(" ", "_") // en cas de phrases avec espaces
                    val motResId = context.resources.getIdentifier(motFile, "raw", context.packageName)
                    if (motResId != 0) {
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(context, motResId)
                        mediaPlayer?.start()
                        sharedViewModel.addInstructionLog(Pair("\uD83C\uDFA7 Audio du mot joué: $motFile", elapsedTime))
                    } else {
                        Log.w("TestDisplay", "⚠️ Audio non trouvé pour le mot: $motFile")
                    }
                }
            }
        }


        // ✅ Logique d’image
        if (!test.image.isNullOrEmpty()) {
            when (test.affichage) {
                "hasard" -> {
                    selectedImage = test.image.random()
                    sharedViewModel.addInstructionLog(Pair(" \uD83D\uDDBC Image choisie au hasard: $selectedImage", elapsedTime)) // Ajout au log
                }
                "complet" -> {}
                else -> {
                    if (test.image.size == 1) {
                        selectedImage = test.image.first()
                    }
                }
            }
        }
    }

    // 🔁 Stop audio à la fin
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // 🖼 Interface
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        selectedImage?.let { imageName ->
            val resId = getMipmapResId(imageName)
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = imageName,
                    modifier = Modifier
                        .size(if (test.affichage == "complet") 500.dp else 350.dp)
                        .aspectRatio(1.5f)
                        .align(Alignment.TopCenter)
                        .offset(y = 100.dp)
                )
            }
        }

        if (test.image?.size == 4 && test.affichage == "complet") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp) // Remonter la colonne des images en ajustant cette valeur
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            test.image.chunked(2).forEach { rowImages -> // Diviser les images en lignes de 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowImages.forEach { img -> // Afficher chaque image
                            val resId = getMipmapResId(img)
                            if (resId != 0) {
                                ImageClickable(
                                    imageResId = resId,
                                    contentDescription = img,
                                    onClick = {
                                        onImageClick(img) // Appeler la fonction onClick passée par le parent
                                        sharedViewModel.addInstructionLog(Pair(" \uD83D\uDDBC Image cliquée: $img ", elapsedTime)) // Ajouter l'image cliquée dans le log
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        val consigne = if (isFrontCamera) test.a_consigne else test.h_consigne
        Text(
            text = consigne ?: "NULL",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 280.dp)
        )

        if (randomMot.isNotEmpty()) {
            val isPhrase = randomMot.trim().contains(" ") // Si contient un espace, on considère que c’est une phrase
            val fontSize = if (isPhrase) 28.sp else 50.sp
            val bottomPadding = if (isPhrase) 170.dp else 180.dp

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomPadding)
                    .background(Color.Gray)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = randomMot,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
                    color = motColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun getMipmapResId(name: String): Int {
    val imageNameWithForeground = "${name}_foreground"
    return try {
        val resId = R.mipmap::class.java.getField(imageNameWithForeground).getInt(null)
        Log.d("TestDisplay", "✅ Image trouvée: R.mipmap.$imageNameWithForeground (resId=$resId)")
        resId
    } catch (e: Exception) {
        Log.e("TestDisplay", "❌ Image introuvable: $imageNameWithForeground", e)
        0
    }
}

fun frenchColorToHex(colorName: String): String? {
    return when (colorName.lowercase()) {
        "rouge" -> "#FF0000"
        "bleu" -> "#0000FF"
        "vert" -> "#008000"
        "jaune" -> "#FFFF00"
        "noir" -> "#000000"
        "blanc" -> "#FFFFFF"
        "gris" -> "#808080"
        "orange" -> "#FFA500"
        "violet" -> "#800080"
        "rose" -> "#FFC0CB"
        else -> null
    }
}

