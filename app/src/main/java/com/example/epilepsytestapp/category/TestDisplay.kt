package com.example.epilepsytestapp.category

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    key: Int // Paramètre pour la clé
) {
    val context = LocalContext.current

    // Utilisation de `remember` et `mutableStateOf` pour stocker l'état mutable
    var randomMot by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    // 🎲 Sélection du mot aléatoire indépendamment
    // Observe `currentInstructionIndex` pour réinitialiser et choisir un nouveau mot à chaque nouvelle instruction
    LaunchedEffect(key1 = key) {  // key est currentInstructionIndex ici
        randomMot = ""  // Réinitialisation du mot aléatoire au début du test
        selectedImage = null  // Réinitialisation de l'image sélectionnée au début du test

        // Si mot_set n'est pas vide, sélectionne un mot aléatoire
        if (!test.mot_set.isNullOrEmpty()) {
            // Affiche la valeur de mot_set dans le log
            Log.d("TestDisplay", "🔍 Valeur de mot_set: ${test.mot_set}")

            randomMot = test.mot_set.random()
            Log.d("TestDisplay", "🎲 Mot aléatoire sélectionné: $randomMot")
        } else {
            Log.w("TestDisplay", "⚠️ mot_set est vide, aucun mot aléatoire sélectionné.")
        }
    }

    // 🎲 Gestion des images selon l'affichage
    LaunchedEffect(test.image, test.affichage) {
        if (!test.image.isNullOrEmpty()) {
            Log.d("TestDisplay", "📷 Images disponibles: ${test.image}")
            Log.d("TestDisplay", "🧩 Clé affichage: ${test.affichage}")

            if (test.image.size == 1) {
                selectedImage = test.image.first()
                Log.d("TestDisplay", "📷 Une seule image -> sélectionnée directement: $selectedImage")
            } else {
                when (test.affichage) {
                    "hasard" -> {
                        selectedImage = test.image.random()
                        Log.d("TestDisplay", "🔀 Mode hasard -> image choisie: $selectedImage")
                    }
                    "complet" -> {
                        Log.d("TestDisplay", "🧩 Mode complet avec 4 images.")
                    }
                    else -> {
                        Log.w("TestDisplay", "⚠️ Mode affichage inconnu ou non supporté: '${test.affichage}'")
                    }
                }
            }
        } else {
            Log.w("TestDisplay", "⚠️ Aucune image fournie dans le test")
        }
    }

    fun getMipmapResId(name: String): Int {
        val imageNameWithForeground = "${name}_foreground"
        return try {
            val resId = R.mipmap::class.java.getField(imageNameWithForeground).getInt(null)
            Log.d("TestDisplay", "✅ Image trouvée: R.mipmap.$imageNameWithForeground (resId=$resId)")
            resId
        } catch (e: Exception) {
            Log.e("TestDisplay", "❌ Erreur: image '$imageNameWithForeground' introuvable dans mipmap", e)
            0
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 🖼 Image unique ou aléatoire
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
            } else {
                Log.w("TestDisplay", "⚠️ Image non affichée: resId invalide pour '$imageName'")
            }
        }

        // 🖼 Grille de 4 images
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
                            } else {
                                Log.w("TestDisplay", "⚠️ Image ignorée dans le grid: $img (resId invalide)")
                            }
                        }
                    }
                }
            }
        }

        // 📝 Consigne
        val consigne = if (isFrontCamera) test.consigneA else test.consigneH
        Text(
            text = consigne,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 250.dp) // Remontée de la consigne
        )

        // 🟡 Mot aléatoire affiché en-dessous
        if (randomMot.isNotEmpty()) {
            Text(
                text = randomMot,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 50.sp),
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp) // Ajustement de l'espace pour éviter chevauchement
            )
        } else {
            Log.w("TestDisplay", "⚠️ Aucune valeur pour randomMot à afficher.")
        }
    }
}
