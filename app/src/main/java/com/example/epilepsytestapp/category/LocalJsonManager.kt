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

    suspend fun loadLocalTests(context: Context): Set<Test> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, FILE_NAME)
                if (!file.exists()) return@withContext emptySet<Test>()

                val json = file.readText()
                val type = object : TypeToken<Set<Test>>() {}.type
                gson.fromJson(json, type) ?: emptySet()
            } catch (e: Exception) {
                Log.e("LocalCategory", "Erreur : Lecture du JSON local: ${e.message}")
                emptySet()
            }
        }
    }

    suspend fun saveLocalTests(context: Context, selectedTests: Map<String, List<Test>>) {
        withContext(Dispatchers.IO) {
            try {

                val testset = selectedTests.values.flatten().toSet()

                /*
                val testset = mutableSetOf<Test>()
                selectedTests.values.forEach { tests ->
                    testset.addAll(tests)
                }
                */

                val filteredTests = testset.map { test ->
                    test.copy(
                        mot_memoire = test.mot_memoire?.takeIf { it.isNotEmpty() },
                        image = test.image?.takeIf { it.isNotEmpty() },
                        mot_setA = test.mot_setA?.takeIf { it.isNotEmpty() },
                        mot_setB = test.mot_setB?.takeIf { it.isNotEmpty() },
                        phrase_repet = test.phrase_repet?.takeIf { it.isNotEmpty() },
                        couleur = test.couleur?.takeIf { it.isNotEmpty() },
                        mot = test.mot?.takeIf { it.isNotEmpty() },
                        groupe = test.groupe?.takeIf { it.isNotEmpty() }
                    )
                }.toSet()

                /*
                val existingTests = loadLocalTests(context).toMutableSet()
                filteredTests.forEach { newTest ->
                    if (existingTests.none { it.id_test == newTest.id_test }) {
                        existingTests.add(newTest)
                    }
                }
                */

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
