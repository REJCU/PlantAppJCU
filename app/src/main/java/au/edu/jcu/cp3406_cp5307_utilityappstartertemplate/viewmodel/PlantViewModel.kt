package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository.PlantRepo
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class PlantViewModel(private val repository: PlantRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.trackedPlants, _uiState) { plants, state ->
                val filteredPlants = plants.filter { plant ->
                    val matchesLocation = state.selectedLocation == "All" ||
                            plant.location.equals(state.selectedLocation, ignoreCase = true)

                    val matchesType = state.selectedPlantType == "All" ||
                            plant.plantType.equals(state.selectedPlantType, ignoreCase = true)

                    matchesLocation && matchesType
                }
                if (state.sortByUrgency) {
                    filteredPlants.sortedBy { it.getDaysUntilNextWater() }
                } else {
                    filteredPlants.sortedBy { it.name }
                }
            }.collect { sortedPlants ->
                _uiState.update { it.copy(plants = sortedPlants) }
            }
        }
    }

    fun updateLocation(zone: String) {
        _uiState.update { it.copy(selectedLocation = zone) }
    }

    fun updatePlantType(type: String) {
        _uiState.update { it.copy(selectedPlantType = type) }
    }

    fun showAddPlantDialog() {
        _uiState.update { it.copy(isAddPlantDialogVisible = true) }
    }

    fun dismissAddPlantDialog() {
        _uiState.update { it.copy(isAddPlantDialogVisible = false) }
    }

    fun addLocalPlant(name: String, species: String, wateringInterval: Int, location: String, plantType: String) {
    viewModelScope.launch {
        repository.addNewPlant(name, species, wateringInterval, location, plantType)
        dismissAddPlantDialog()
        }
    }

    fun waterPlant(plantId: String) {
        viewModelScope.launch {
            val todayDate = LocalDate.now().toEpochDay()
            repository.waterPlant(plantId, todayDate)
        }
    }
}

