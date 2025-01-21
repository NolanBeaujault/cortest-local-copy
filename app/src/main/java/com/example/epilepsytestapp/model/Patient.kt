@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.example.epilepsytestapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: Int = 0,
    val lastName: String = "",
    val firstName: String = "",
    val address: String = "",
    val neurologist: String = "",
    val username: String = "",
    val password: String = "",
    val tests: List<Test> = emptyList()
)


@Serializable
data class Test(
    val id: Int,                      // Identifiant unique du test
    val name: String,                 // Nom du test
    val status: String                // Statut du test (par exemple : "En cours", "Terminé")
)
