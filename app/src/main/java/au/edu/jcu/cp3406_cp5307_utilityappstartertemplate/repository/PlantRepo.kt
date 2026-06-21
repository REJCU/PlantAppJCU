package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.input.key.Key
import androidx.room.Query
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PerennialApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantAPI
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantSearchResponse
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantRepo(
    private val plantDao: PlantDao,
    private val perennialApi: PerennialApi?
) {
    val trackedPlants: Flow<List<TrackedPlant>> = plantDao.getAllPlantsFLow()

    suspend fun searchOnlineSpecies(apiKey: String, query: String): List<PlantSearchResult> = withContext(Dispatchers.IO) {
        if (perennialApi == null || query.isBlank()) return@withContext emptyList()
        return@withContext try {
            val response = perennialApi.searchPlants(apiKey, query)
            response.data
        } catch (e: Exception) {
            Log.e("PlantRepo", "Network lookup failed", e)
            emptyList<PlantSearchResult>()
        }
    }
    suspend fun getPlantDetails(apiKey: String, id: Int): PlantSearchResult? = withContext(Dispatchers.IO) {
        if (perennialApi == null) return@withContext null
        return@withContext try {
            perennialApi.getSpeciesDetails(plantId = id, apiKey = apiKey)
        } catch (e: Exception) {
            Log.e("PlantRepo", "Failed fetching details for plant ID: $id", e)
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateWateringTime(plantId: String) = withContext(Dispatchers.IO) {
        val existingPlant = plantDao.getPlantById(plantId)
        if (existingPlant != null) {
            val updated = existingPlant.copy(lastWateredDay = LocalDate.now().toEpochDay())
            plantDao.insertPlant(updated)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addNewPlant(
        name: String,
        species: String,
        interval: Int,
        location: String,
        plantType: String
    ) = withContext(Dispatchers.IO) {
        val newPlant = TrackedPlant(
            id = UUID.randomUUID().toString(),
            name = name,
            species = species,
            wateringIntervalDays = interval,
            lastWateredDay = LocalDate.now().toEpochDay(),
            location = location,
            plantType = plantType
        )
        plantDao.insertPlant(newPlant)
    }

    suspend fun waterPlant(plantId: String, todayDate: Long) = withContext(Dispatchers.IO) {
        plantDao.waterPlant(plantId, todayDate)
    }
}