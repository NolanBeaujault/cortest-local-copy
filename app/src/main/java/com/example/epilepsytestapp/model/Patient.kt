@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.example.epilepsytestapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: Int,                      // Identifiant unique du patient
    val lastName: String,             // Nom de famille du patient
    val firstName: String,            // Prénom du patient
    val address: String,              // Adresse du patient
    val neurologist: String,          // Nom du neurologue du patient
    val username: String,             // Nom d'utilisateur pour la connexion
    val password: String,             // Mot de passe pour la connexion
    val tests: List<Test> = emptyList() // Liste des tests associés au patient
)

@Serializable
data class Test(
    val id: Int,                      // Identifiant unique du test
    val name: String,                 // Nom du test
    val status: String                // Statut du test (par exemple : "En cours", "Terminé")
)
