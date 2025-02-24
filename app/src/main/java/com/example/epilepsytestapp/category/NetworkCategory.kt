package com.example.epilepsytestapp.category

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

// ✅ Modèle pour un test individuel
data class NetworkTest(
    @SerializedName("id_test") val idTest: Double?, // Certains ID sont en float
    val consigne: String,
    val nom: String // Ajout du champ "nom" pour récupérer le nom du test
)

// ✅ Modèle pour une catégorie contenant plusieurs tests
data class NetworkCategory(
    @SerializedName("id_category") val idCategory: Double?,
    val nom: String, // Nom de la catégorie
    val tests: List<NetworkTest> // Liste des tests correctement extraits
)

// ✅ Définition de la réponse complète
typealias CategoriesResponse = Map<String, Any>

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): retrofit2.Response<Map<String, Any>> // Réponse sous forme d'une Map générique
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

// ✅ Fonction pour charger les catégories et tests
suspend fun loadCategoriesFromNetwork(): Map<String, List<String>> {
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

            // ✅ Conversion en Map générique
            val type = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
            val parsedData: Map<String, Map<String, Any>> = gson.fromJson(jsonString, type)

            // ✅ Extraction des catégories et tests
            val categories = parsedData.mapValues { (categoryKey, categoryData) ->
                val testsList = mutableListOf<String>()

                // Parcourir les objets de la catégorie
                categoryData.forEach { (key, value) ->
                    if (key == "id_category") return@forEach // Ignorer l'ID de la catégorie

                    if (value is Map<*, *>) {
                        val testName = value["nom"] as? String ?: "Nom inconnu" // ✅ Récupération du nom
                        testsList.add(testName)
                    }
                }

                testsList // ✅ On renvoie directement une liste de noms de tests
            }

            Log.d("NetworkCategory", "✅ Catégories et tests chargés avec succès : $categories")
            return@withContext categories
        } catch (e: Exception) {
            Log.e("NetworkCategory", "❌ Erreur lors du chargement des catégories : ${e.message}", e)
            emptyMap()
        }
    }
}
