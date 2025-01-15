import com.example.epilepsytestapp.model.Patient
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ApiService {
    @GET("patients.json")
    suspend fun getPatients(): List<Patient>
}

object RetrofitClient {
    private const val BASE_URL = "https://cortest-70b6e-default-rtdb.europe-west1.firebasedatabase.app/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

suspend fun loadPatientsFromNetwork(): List<Patient> {
    return withContext(Dispatchers.IO) {
        try {
            RetrofitClient.apiService.getPatients()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
