package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM tracked_plants")
    fun getAllPlantsFLow(): Flow<List<TrackedPlant>>

    @Query("SELECT * FROM tracked_plants WHERE id = :plantId LIMIT 1")
    suspend fun getPlantById(plantId: String): TrackedPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: TrackedPlant)
}