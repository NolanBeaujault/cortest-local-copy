package com.example.epilepsytestapp.category

data class Test(
    val idtest : Int,
    val nom: String,
    val consigneA: String,
    val consigneH: String,
    val typetest: String,
    val affichage: String,

    val mot_set: List<String>? = null,
    val image: List<String>? = null,
    val couleur : List<String>? = null,
    val mot : List<String>? = null,
    val groupe: Map<String, String>? = null,
)