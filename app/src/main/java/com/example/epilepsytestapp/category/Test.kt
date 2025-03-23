package com.example.epilepsytestapp.category

data class Groupe(
    val id_groupe: Int,
    val nom: String
)

data class Test(
    val id_test : Int,
    val nom: String,
    val type : String,
    val a_consigne: String? = null,
    val h_consigne: String? = null,

    val mot_memoire: List<String>? = null,
    val image: List<String>? = null,
    val mot_setA : List<String>? = null,
    val mot_setB : List<String>? = null,
    val phrase_repet : List<String>? = null,
    val couleur : List<String>? = null,
    val mot : List<String>? = null,
    val groupe: Groupe? = null
)
