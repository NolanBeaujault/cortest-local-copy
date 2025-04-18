package com.example.epilepsytestapp.category

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
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

@Composable
fun TestDisplay(
    test: Test,
    isFrontCamera: Boolean,
    onImageClick: (String) -> Unit = {},
    key: Int
) {
    val context = LocalContext.current

    var randomMot by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var motColor by remember { mutableStateOf(Color.Unspecified) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // 🎲 Initialisation à chaque nouvelle instruction
    LaunchedEffect(key1 = key) {
        randomMot = ""
        selectedImage = null
        motColor = Color.Unspecified

        // 🎲 Choix du mot
        if (!test.mot_set.isNullOrEmpty()) {
            randomMot = test.mot_set.random()
            Log.d("TestDisplay", "🎲 Mot sélectionné: $randomMot")

            // 🎨 Si test.couleur est défini : appliquer une couleur différente du mot
            if (!test.couleur.isNullOrEmpty()) {
                val filteredColors = test.couleur.filterNot {
                    it.equals(randomMot, ignoreCase = true)
                }
                val selectedColorName = filteredColors.randomOrNull()
                selectedColorName?.let { colorName ->
                    frenchColorToHex(colorName)?.let { hex ->
                        motColor = Color(android.graphics.Color.parseColor(hex))
                        Log.d("TestDisplay", "🎨 Couleur appliquée : $colorName → $hex")
                    } ?: run {
                        Log.w("TestDisplay", "⚠️ Couleur inconnue : $colorName")
                    }
                }
            }

            // 🔊 Jouer l'audio si caméra frontale
            if (isFrontCamera && test.audio.isNotEmpty()) {
                val filename = test.audio.removeSuffix(".m4a")
                val resId = context.resources.getIdentifier(filename, "raw", context.packageName)

                if (resId != 0) {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(context, resId)
                    mediaPlayer?.start()
                    Log.d("TestDisplay", "▶️ Audio joué : ${test.audio}")
                } else {
                    Log.w("TestDisplay", "⚠️ Audio non trouvé : ${test.audio}")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // 🖼️ Interface visuelle
    Box(modifier = Modifier.fillMaxSize()) {
        selectedImage?.let { imageName ->
            val resId = getMipmapResId(imageName)
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = imageName,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .padding(top = 16.dp)
                )
            }
        }

        if (test.image?.size == 4 && test.affichage == "complet") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                test.image.chunked(2).forEach { rowImages ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowImages.forEach { img ->
                            val resId = getMipmapResId(img)
                            if (resId != 0) {
                                ImageClickable(
                                    imageResId = resId,
                                    contentDescription = img,
                                    onClick = { onImageClick(img) },
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
        if (!consigne.isNullOrEmpty()) {
            Text(
                text = consigne,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 250.dp)
            )
        }

        if (randomMot.isNotEmpty()) {
            Text(
                text = randomMot,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 50.sp),
                color = if (motColor != Color.Unspecified) motColor else MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp)
            )
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

