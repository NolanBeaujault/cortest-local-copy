package com.example.epilepsytestapp.category

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object LocalCatManager {
    private val gson = Gson()

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

    suspend fun saveLocalTests(context: Context, filename: String, selectedTests: List<Test>) {
        withContext(Dispatchers.IO) {
            try {
                val filteredTests = selectedTests.map { test ->
                    test.copy(
                        a_consigne = test.a_consigne?.takeIf { it.isNotEmpty() },
                        h_consigne = test.h_consigne?.takeIf { it.isNotEmpty() },
                        mot_memoire = test.mot_memoire?.takeIf { it.isNotEmpty() },
                        image = test.image?.takeIf { it.isNotEmpty() },
                        mot_setA = test.mot_setA?.takeIf { it.isNotEmpty() },
                        mot_setB = test.mot_setB?.takeIf { it.isNotEmpty() },
                        phrase_repet = test.phrase_repet?.takeIf { it.isNotEmpty() },
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
