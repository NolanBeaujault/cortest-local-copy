package com.example.epilepsytestapp.ui

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// Créer un client OkHttp qui peut être réutilisé
val client = OkHttpClient()

suspend fun loadCategoriesFromJson(context: Context): Map<String, List<String>> {
    val url = "http://pi-nolan.its-tps.fr:2880/categories"
    return try {
        // Exécuter la requête réseau sur un thread de fond (IO)
        val response: Response = withContext(Dispatchers.IO) {
            // Créer une requête GET pour l'URL
            val request = Request.Builder()
                .url(url)
                .build()

            // Effectuer la requête et obtenir la réponse
            client.newCall(request).execute()
        }

        // Vérifier que la requête a réussi
        if (!response.isSuccessful) {
            throw IOException("Unexpected code $response")
        }

        // Lire le corps de la réponse
        val json = response.body?.string() ?: throw IOException("Empty response body")

        // Convertir le JSON
        val gson = Gson()
        val jsonObject = try {
            gson.fromJson(json, JsonObject::class.java) // Charger comme objet JSON
        } catch (e: JsonSyntaxException) {
            throw IOException("Malformed JSON: ${e.message}")
        }

        val categories = mutableMapOf<String, List<String>>()

        // Parcourir chaque catégorie dans le JSON et extraire les tests
        for (categoryKey in jsonObject.keySet()) {
            val categoryElement = jsonObject.get(categoryKey)

            // Vérifier si l'élément est un objet JSON (et non une valeur primitive)
            if (categoryElement.isJsonObject) {
                val categoryObject = categoryElement.asJsonObject
                val tests = mutableListOf<String>()

                // Extraire les tests de chaque catégorie
                for (testKey in categoryObject.keySet()) {
                    val testElement = categoryObject.get(testKey)

                    // Vérifier si l'élément "test" est un objet JSON (et non une valeur primitive)
                    if (testElement.isJsonObject) {
                        val test = testElement.asJsonObject
                        tests.add(test.get("nom").asString) // Ajouter le nom du test à la liste
                    } else if (testElement.isJsonPrimitive) {
                        // Si c'est une valeur primitive, l'ajouter directement
                        tests.add(testElement.asString)
                    } else {
                        // Si l'élément n'est ni un objet ni une valeur primitive, le gérer autrement
                        println("Test $testKey dans la catégorie $categoryKey n'est ni un objet ni une valeur attendue.")
                    }
                }

                // Ajouter la catégorie et ses tests à la carte
                categories[categoryKey] = tests
            } else {
                // Si l'élément de la catégorie n'est pas un objet, ajouter un comportement alternatif
                println("Catégorie $categoryKey n'est pas un objet JSON attendu.")
            }
        }

        categories
    } catch (e: IOException) {
        e.printStackTrace()
        emptyMap() // Retourne une carte vide en cas d'erreur réseau ou de fichier
    } catch (e: Exception) {
        e.printStackTrace()
        emptyMap() // Retourne une carte vide pour d'autres erreurs (ex. syntaxe JSON)
    }
}