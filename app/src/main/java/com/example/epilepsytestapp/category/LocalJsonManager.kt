package com.example.epilepsytestapp.category

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Gestion du fichier .json pour la configuration et l'utilisation des tests
object LocalCatManager {
    private val gson = Gson()

    // Chargement des données configurées depuis le fichier json local spécifié dans filename
    suspend fun loadLocalTests(context: Context, filename: String = "localtestconfiguration.json"): List<Test> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, filename)
                if (!file.exists()) return@withContext emptyList()

                val json = file.readText()
                val type = object : TypeToken<List<Test>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("LocalCategory", "Erreur : Lecture du JSON local: ${e.message}")
                emptyList()
            }
        }
    }

    // Sauvegarde du fichier json pendant la configuration
    suspend fun saveLocalTests(context: Context, filename: String, selectedTests: List<Test>) {
        withContext(Dispatchers.IO) {
            try {
                val filteredTests = selectedTests.map { test ->
                    // Copie de chaque test selectionné pendant la configuration en filtrant tous les champs inutiles (valeur null)
                    test.copy(
                        a_consigne = test.a_consigne?.takeIf { it.isNotEmpty() },
                        h_consigne = test.h_consigne?.takeIf { it.isNotEmpty() },
                        affichage = test.affichage?.takeIf{ it.isNotEmpty() },
                        mot_set = test.mot_set?.takeIf { it.isNotEmpty() },
                        mot_set_audio = test.mot_set_audio?.takeIf { it.isNotEmpty() },
                        image = test.image?.takeIf { it.isNotEmpty() },
                        couleur = test.couleur?.takeIf { it.isNotEmpty() },
                        mot = test.mot?.takeIf { it.isNotEmpty() },
                        groupe = test.groupe?.let { groupe ->
                            if (groupe.id_groupe != null && groupe.nom.isNotEmpty()) {
                                groupe
                            } else {
                                null
                            }
                        }
                    )
                }
                Log.d("LocalCategory", "Affichage : ${filteredTests}")

                // Ajout du test filtré dans le fichier json sous la forme d'une liste de tests
                val updatedTestsJson = gson.toJson(filteredTests)
                val file = File(context.filesDir, filename)
                file.writeText(updatedTestsJson)

                Log.d("LocalCategory", "Tests enregistrés dans $filename")
            } catch (e: Exception) {
                Log.e("LocalCategory", "Erreur : Sauvegarde du JSON local: ${e.message}")
            }
        }
    }
}
