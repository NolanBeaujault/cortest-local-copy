import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Modèle des données envoyées
data class RegisterRequest(
    val id: String,
    val nom: String,
    val prenom: String,
    val adresse: String,
    val neurologue: String,
    val date_naissance: String
)

// Interface Retrofit
interface APIPost {
    @POST("register")
    suspend fun registerUser(@Body request: RegisterRequest)
}

// Instance Retrofit
object RetrofitInstance {
    val api: APIPost by lazy {
        Retrofit.Builder()
            .baseUrl("http://pi-nolan.its-tps.fr:2880/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIPost::class.java)
    }
}
