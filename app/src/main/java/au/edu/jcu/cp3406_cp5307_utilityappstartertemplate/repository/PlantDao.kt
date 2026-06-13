package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import kotlinx.coroutines.flow.Flow
import java.sql.Date

@Dao
interface PlantDao {
    @Query("SELECT * FROM Tracked_Plants")
    fun getAllPlantsFLow(): Flow<List<TrackedPlant>>

    @Query("SELECT * FROM Tracked_Plants WHERE id = :plantId LIMIT 1")
    suspend fun getPlantById(plantId: String): TrackedPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: TrackedPlant)

    @Query("Update Tracked_Plants SET lastWateredDay = :todayDate WHERE id = :plantId")
    suspend fun waterPlant(plantId: String, todayDate: Long)
}