package com.example.epilepsytestapp.category

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


import com.google.gson.annotations.SerializedName

data class Test(
    val id_test: Int,
    val consigne: String
)

data class Category(
    @SerializedName("id_category") val id: Int,
    val tests: Map<String, Test>? // Il est important que ce soit nullable, car certains tests peuvent être manquants
)

typealias CategoriesResponse = Map<String, Category>


interface ApiService {
    @GET("categories") // Mettre l'URL correcte de l'API
    suspend fun getCategories(): CategoriesResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://pi-nolan.its-tps.fr:2880/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

suspend fun loadCategoriesFromNetwork(): Map<String, List<String>> {
    return withContext(Dispatchers.IO) {
        try {
            // Charger les catégories depuis l'API
            val categories = RetrofitClient.apiService.getCategories()
            Log.d("TestConfig", "Catégories chargées : $categories")

            // Pour chaque catégorie, récupérer les noms des tests (les clés des Map)
            val testsByCategory = categories.mapValues { (_, category) ->
                // Vérifier que les tests existent et ne sont pas nuls
                val tests = category.tests ?: emptyMap()
                Log.d("TestConfig", "Tests pour la catégorie ${category.id}: $tests")

                // Extraire les noms des tests
                tests.keys.toList()
            }

            // Afficher les résultats
            Log.d("TestConfig", "Tests par catégorie: $testsByCategory")

            testsByCategory
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap() // Retourner une carte vide en cas d'erreur
        }
    }
}

