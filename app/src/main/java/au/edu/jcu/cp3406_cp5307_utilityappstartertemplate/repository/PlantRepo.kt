package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository

import android.os.Build
import androidx.annotation.RequiresApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantAPI
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

class PlantRepo(
    private val plantDao: PlantDao,
    private val api: PlantAPI?
) {
    val trackedPlants: Flow<List<TrackedPlant>> = plantDao.getAllPlantsFLow()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateWateringTime(plantId: String) {
        val existingPlant = plantDao.getPlantById(plantId)
        if (existingPlant != null) {
            val updated = existingPlant.copy(lastWateredDay = LocalDate.now().toEpochDay())
            plantDao.insertPlant(updated)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addNewPlant(name: String, species: String, interval: Int) {
        val newPlant = TrackedPlant(
            id = UUID.randomUUID().toString(),
            name = name,
            species = species,
            wateringIntervalDays = interval,
            lastWateredDay = LocalDate.now().toEpochDay()
        )
        plantDao.insertPlant(newPlant)
    }
}