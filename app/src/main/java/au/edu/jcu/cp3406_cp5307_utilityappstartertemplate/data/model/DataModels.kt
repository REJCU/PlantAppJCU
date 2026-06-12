package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PlantSpeciesInfo(
    val id: Int,
    val commonName: String,
    val wateringInterval: Int,
    val description: String
)

data class UiState(
    val plants: List<TrackedPlant> = emptyList(),
    val sortByUrgency: Boolean = false,
    val isLoading: Boolean = false,
    val isAddPlantDialogVisible: Boolean = false
)
