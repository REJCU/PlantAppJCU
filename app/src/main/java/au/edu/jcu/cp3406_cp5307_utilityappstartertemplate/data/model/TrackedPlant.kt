package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity(tableName = "Tracked_Plants")
data class TrackedPlant(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val wateringIntervalDays: Int,
    val lastWateredDay: Long,
    val location: String = "Front",
    val plantType: String = "Other"
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastWateredDate(): LocalDate {
        return LocalDate.ofEpochDay(lastWateredDay)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysUntilNextWater(): Int {
        val nextWateringDate = getLastWateredDate().plusDays(wateringIntervalDays.toLong())
        return ChronoUnit.DAYS.between(LocalDate.now(), nextWateringDate).toInt()
    }
}