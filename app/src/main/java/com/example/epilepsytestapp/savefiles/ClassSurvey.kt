package com.example.epilepsytestapp.savefiles

data class SurveyQuestionUI(
    val label: String,
    val type: QuestionType,
    val options: List<String> = emptyList()
)

enum class QuestionType(val label: String) {
    TEXTE("Texte libre"),
    CURSEUR_1_10("Curseur 1 à 10"),
    CHOIX_UNIQUE("Choix unique"),
    CHOIX_MULTIPLE("Choix multiple")
}

fun SurveyQuestion.toUi(): SurveyQuestionUI {
    return SurveyQuestionUI(
        label = label,
        type = QuestionType.valueOf(type), // String -> enum
        options = options
    )
}

fun SurveyQuestionUI.toData(): SurveyQuestion {
    return SurveyQuestion(
        label = label,
        type = type.name, // enum -> String
        options = options
    )
}

val defaultQuestions = listOf(
    SurveyQuestionUI("Est-ce une crise habituelle ?", QuestionType.CHOIX_UNIQUE, listOf("Oui", "Non")),
    SurveyQuestionUI("Si non, précisez :", QuestionType.TEXTE),
    SurveyQuestionUI("Facteur déclenchant ?", QuestionType.CHOIX_UNIQUE, listOf("Oui", "Non")),
    SurveyQuestionUI("Si oui, lequel ?", QuestionType.TEXTE),
    SurveyQuestionUI("Contexte de fatigue :", QuestionType.CURSEUR_1_10),
    SurveyQuestionUI("Contexte de stress :", QuestionType.CURSEUR_1_10),
    SurveyQuestionUI("Changement/oubli de traitement ces dernières 24h ?", QuestionType.CHOIX_UNIQUE, listOf("Oui", "Non"))
)

