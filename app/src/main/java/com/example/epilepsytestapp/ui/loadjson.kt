package com.example.epilepsytestapp.ui

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException // Ajout de l'import pour IOException
import com.google.gson.JsonObject
import java.io.File
import android.content.SharedPreferences


data class Patient(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val address: String,
    val neurologist: String,
    val username: String,
    val password: String,
    val tests: List<Test> = emptyList()
)

data class Test(
    val id: Int,
    val name: String,
    val status: String
)



fun loadPatientsFromJson(context: Context): List<Patient> {
    return try {
        // Charger le contenu du fichier JSON
        val inputStream = context.assets.open("fakedata.json")
        val json = inputStream.bufferedReader().use { it.readText() }

        // Convertir le JSON
        val gson = Gson()
        val jsonObject = gson.fromJson(json, JsonObject::class.java) // Charger comme objet JSON
        val patientsJson = jsonObject.getAsJsonArray("patients") // Extraire le tableau "patients"

        // Convertir le tableau "patients" en liste d'objets Patient
        val type = object : TypeToken<List<Patient>>() {}.type
        gson.fromJson(patientsJson, type)
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList() // Retourne une liste vide en cas d'erreur de fichier
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList() // Retourne une liste vide pour d'autres erreurs (ex. syntaxe JSON)
    }
}



fun savePatientsToJson(context: Context, patients: List<Patient>) {
    try {
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.add("patients", gson.toJsonTree(patients))

        // Chemin du fichier JSON (dans les fichiers internes de l'application)
        val file = File(context.filesDir, "fakedata.json")
        file.writeText(jsonObject.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



