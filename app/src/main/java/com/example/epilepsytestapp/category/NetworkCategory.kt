package com.example.epilepsytestapp.category

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


interface ApiService {
    @GET("categories")
    suspend fun getCategories(): retrofit2.Response<Map<String, Any>>
}


object RetrofitClient {
    private const val BASE_URL = "http://pi-nolan.its-tps.fr:2880/"

    val apiService: ApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}


suspend fun loadCategoriesFromNetwork(): Map<String, List<Test>> {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("NetworkCategory", "🔄 Lancement du chargement des catégories...")

            val response = RetrofitClient.apiService.getCategories()

            if (!response.isSuccessful) {
                Log.e("NetworkCategory", "❌ Erreur HTTP : ${response.code()}")
                return@withContext emptyMap()
            }

            val rawJson = response.body()
            Log.d("NetworkCategory", "📨 Réponse brute reçue : $rawJson")

            if (rawJson.isNullOrEmpty()) {
                Log.e("NetworkCategory", "⚠ Réponse vide de l'API")
                return@withContext emptyMap()
            }

            val gson = GsonBuilder().setLenient().create()
            val jsonString = gson.toJson(rawJson)

            val type = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
            val parsedData: Map<String, Map<String, Any>> = gson.fromJson(jsonString, type)

            val categoriesMap = mutableMapOf<String, List<Test>>()

            parsedData.forEach { (_, categoryData) ->
                val categoryName = categoryData["nom"] as? String ?: "Catégorie inconnue"
                val testsList = mutableListOf<Test>()

                categoryData.forEach { (key, value) ->
                    if (key == "id_category" || key == "nom") return@forEach

                    if (value is Map<*, *>) {
                        val testName = value["nom"] as? String ?: "Test inconnu"
                        val affichage = value["affichage"] as? String ?: "Affichage inconnu"
                        Log.d("NetworkCategory", "Affichage récupéré : $affichage")
                        val testType = value["type"] as? String ?: "Type inconnu"
                        val audio = value["audio"] as? String ?: "Audio indisponible"
                        val a_consigne = value["a_consigne"] as? String ?: "Consigne Auto inconnue"
                        val h_consigne = value["h_consigne"] as? String ?: "Consigne Hetero inconnue"
                        val idTest = when (val id = value["id_test"]) {
                            is Int -> id
                            is Double -> id.toInt()
                            else -> -1                            }

                        val motset = value["mot_set"] as? List<String> ?: emptyList()
                        val image = value["image"] as? List<String> ?: emptyList()
                        val couleur = value["couleur"] as? List<String> ?: emptyList()
                        val mot = value["mot"] as? List<String> ?: emptyList()

                        val groupeData = value["groupe"] as? Map<String, Any>

                        val idGroupe = groupeData?.get("id_groupe") as? Int ?: -1
                        val nomGroupe = groupeData?.get("nom") as? String ?: ""

                        val groupe = Groupe(id_groupe = idGroupe, nom = nomGroupe)

                        val test = Test(
                            id_test = idTest,
                            type = testType,
                            nom = testName,
                            affichage = affichage,
                            a_consigne = a_consigne,
                            h_consigne = h_consigne,
                            image = image,
                            couleur = couleur,
                            mot = mot,
                            mot_set = motset,
                            groupe = groupe,
                            audio = audio,
                        )

                        testsList.add(test)
                    }
                }

                categoriesMap[categoryName] = testsList
            }

            Log.d("NetworkCategory", "✅ Catégories et tests chargés avec succès : $categoriesMap")
            return@withContext categoriesMap
        } catch (e: Exception) {
            Log.e("NetworkCategory", "❌ Erreur lors du chargement des catégories : ${e.message}", e)
            emptyMap()
        }
    }
}
