package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote

import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


data class PlantSearchResponse(
    val data: List<PlantSearchResult>
)

data class PlantSearchResult(
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>?,
    val watering: String?
)

interface PerennialApi {
    @GET("api/v2/species-list")
    suspend fun searchPlants(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ) : PlantSearchResponse

    @GET("api/v2/species/details/{id}")
    suspend fun getSpeciesDetails(
        @Path("id") plantId: Int,
        @Query("key") apiKey: String
    ): PlantSearchResult
}
