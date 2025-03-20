package com.example.epilepsytestapp.category

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

// ✅ Interface API
interface ApiService {
    @GET("categories")
    suspend fun getCategories(): retrofit2.Response<Map<String, Any>>
}

// ✅ Initialisation du client Retrofit
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

            // ✅ Conversion en Map générique
            val type = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
            val parsedData: Map<String, Map<String, Any>> = gson.fromJson(jsonString, type)

            // ✅ Initialisation du Map final
            val categoriesMap = mutableMapOf<String, List<Test>>()

            // ✅ Extraction des catégories et tests
            parsedData.forEach { (_, categoryData) ->
                val categoryName = categoryData["nom"] as? String ?: "Catégorie inconnue"
                val testsList = mutableListOf<Test>()

                // ✅ Parcourir les objets de la catégorie
                categoryData.forEach { (key, value) ->
                    if (key == "id_category" || key == "nom") return@forEach // Ignorer l'ID et le nom de la catégorie

                    if (value is Map<*, *>) {
                        val testName = value["nom"] as? String ?: "Test inconnu"
                        val consigneA = value["a_consigne"] as? String ?: "ConsigneA inconnue"
                        val consigneH = value["h_consigne"] as? String ?: "ConsigneH inconnue"
                        val typetest = value["type"] as? String ?: "Type inconnu"
                        val idTest = when (val id = value["id_test"]) {
                            is Int -> id
                            is Double -> id.toInt()
                            else -> -1                            }

                        val motMemoire = value["mot_memoire"] as? List<String> ?: emptyList()
                        val image = value["image"] as? List<String> ?: emptyList()
                        val motSetA = value["mot_setA"] as? List<String> ?: emptyList()
                        val motSetB = value["mot_setB"] as? List<String> ?: emptyList()
                        val phraseRepet = value["phrase_repet"] as? List<String> ?: emptyList()
                        val couleur = value["couleur"] as? List<String> ?: emptyList()
                        val mot = value["mot"] as? List<String> ?: emptyList()
                        val groupe = value["groupe"] as? Map<String, String> ?: emptyMap()

                        val test = Test(
                            idtest = idTest,
                            nom = testName,
                            consigneA = consigneA,
                            consigneH = consigneH,
                            mot_memoire = motMemoire,
                            image = image,
                            mot_setA = motSetA,
                            mot_setB = motSetB,
                            phrase_repet = phraseRepet,
                            couleur = couleur,
                            mot = mot,
                            groupe = groupe,
                            typetest = typetest,
                        )

                        testsList.add(test)
                    }
                }

                // ✅ Ajout à la Map finale avec le bon nom de catégorie
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
