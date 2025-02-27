package com.example.epilepsytestapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class UserProfileResponse(
    val id: String,
    val nom: String,
    val prenom: String,
    val date_naissance: String?,
    val adresse: String,
    val neurologue: String
)

interface APIGetProfil{
    @GET("profil/{uid}")
    suspend fun getUserProfile(@Path("uid") uid: String): UserProfileResponse
}

object RetrofitInstance{
    val api: APIGetProfil by lazy {
        Retrofit.Builder()
            .baseUrl("http://pi-nolan.its-tps.fr:2880/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIGetProfil::class.java)
    }
}