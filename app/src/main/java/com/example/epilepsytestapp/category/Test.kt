package com.example.epilepsytestapp.category

data class Test(
    val idtest : Int,
    val nom: String,
    val consigneA: String,
    val consigneH: String,
    val typetest: String,

    val mot_memoire: List<String>? = null,
    val image: List<String>? = null,
    val mot_setA : List<String>? = null,
    val mot_setB : List<String>? = null,
    val phrase_repet : List<String>? = null,
    val couleur : List<String>? = null,
    val mot : List<String>? = null,
    val groupe: Map<String, String>? = null,
)