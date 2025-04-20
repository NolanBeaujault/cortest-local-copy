package com.example.epilepsytestapp.category

// Définition de la classe Groupe
data class Groupe(
    val id_groupe: Int,
    val nom: String
)

// Définition de la classe Test
data class Test(
    val id_test : Int,
    val nom: String,
    val type : String,
    val affichage: String? = null,
    val audio: String,

    val a_consigne: String? = null,
    val h_consigne: String? = null,

    val mot_set: List<String>? = null,
    val mot_set_audio : List<String>? = null,
    val image: List<String>? = null,
    val couleur : List<String>? = null,
    val mot : List<String>? = null,
    val groupe: Groupe? = null
)
