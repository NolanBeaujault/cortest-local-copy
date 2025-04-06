package com.example.epilepsytestapp.category

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object LocalCatManager {
    private const val FILE_NAME = "localtestconfiguration.json"
    private val gson = Gson()

    suspend fun loadLocalTests(context: Context): Map<String, List<Test>> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, FILE_NAME)
                if (!file.exists()) return@withContext emptyMap()

                val json = file.readText()
                val type = object : TypeToken<Map<String, List<Test>>>() {}.type
                gson.fromJson(json, type) ?: emptyMap()
            } catch (e: Exception) {
                Log.e("LocalCategory", "Erreur : Lecture du JSON local: ${e.message}")
                emptyMap()
            }
        }
    }

    suspend fun saveLocalTests(context: Context, selectedTests: Map<String, List<Test>>) {
        withContext(Dispatchers.IO) {
            try {
                val filteredTests = selectedTests.mapValues { (categories, tests) ->
                    tests.map { test ->
                        test.copy(
                            affichage = test.affichage,
                            mot_set = test.mot_set?.takeIf { it.isNotEmpty() },
                            image = test.image?.takeIf { it.isNotEmpty() },
                            couleur = test.couleur?.takeIf { it.isNotEmpty() },
                            mot = test.mot?.takeIf { it.isNotEmpty() },
                            groupe = test.groupe?.takeIf { it.isNotEmpty() }
                        )
                    }

                }
                Log.d("LocalCategory", "Affichage : ${filteredTests}")
                val updatedTestsJson = gson.toJson(filteredTests)
                val file = File(context.filesDir, FILE_NAME)
                file.writeText(updatedTestsJson)

                Log.d("LocalCategory", "Tests enregistrés dans $FILE_NAME")
            } catch (e: Exception) {
                Log.e("LocalCategory", "Erreur : Sauvegarde du JSON local: ${e.message}")
            }
        }
    }
}
