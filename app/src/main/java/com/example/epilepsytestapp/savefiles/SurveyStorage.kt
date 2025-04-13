package com.example.epilepsytestapp.savefiles

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class SurveyQuestion(
    val label: String,
    val type: String, // Correspond à l'enum: TEXTE, CURSEUR_1_5, etc.
    val options: List<String> = emptyList()
)

object SurveyStorage {
    private const val FILE_NAME = "custom_survey.json"
    private val gson = Gson()

    fun saveSurvey(context: Context, questions: List<SurveyQuestion>): Boolean {
        return try {
            val json = gson.toJson(questions)
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(json)
            Log.d("SurveyStorage", "✅ Questionnaire sauvegardé: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("SurveyStorage", "❌ Erreur sauvegarde questionnaire", e)
            false
        }
    }

    fun loadSurvey(context: Context): List<SurveyQuestion> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) {
                Log.w("SurveyStorage", "⚠️ Aucun questionnaire enregistré")
                return emptyList()
            }

            val json = file.readText()
            val type = object : TypeToken<List<SurveyQuestion>>() {}.type
            val questions: List<SurveyQuestion> = gson.fromJson(json, type)
            Log.d("SurveyStorage", "📥 Questionnaire chargé avec ${questions.size} questions")
            questions
        } catch (e: Exception) {
            Log.e("SurveyStorage", "❌ Erreur chargement questionnaire", e)
            emptyList()
        }
    }

    fun deleteSurvey(context: Context): Boolean {
        return File(context.filesDir, FILE_NAME).delete()
    }
}
