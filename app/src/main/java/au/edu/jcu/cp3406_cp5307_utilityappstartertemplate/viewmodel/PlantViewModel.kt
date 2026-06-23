package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.BuildConfig
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository.PlantRepo
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.UiState
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.emptyList


@RequiresApi(Build.VERSION_CODES.O)
class PlantViewModel(private val repository: PlantRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _apiSearchResults = MutableStateFlow<List<PlantSearchResult>>(emptyList())

    val apiSearchResults: StateFlow<List<PlantSearchResult>> = _apiSearchResults.asStateFlow()

    private val _isSearchingNetwork = MutableStateFlow(false)
    val isSearchingNetwork = _isSearchingNetwork.asStateFlow()

    init {
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
            }.onEach { sortedPlants ->
                _uiState.update { it.copy(plants = sortedPlants) }
            }.launchIn(viewModelScope)
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

    fun searchOnlineDatabase(query: String) {
        if (query.isBlank()) {
            clearApiSearch()
            return
        }
        viewModelScope.launch {
            _isSearchingNetwork.value = true
            try {
                val results = repository.searchOnlineSpecies(
                    apiKey = BuildConfig.NASA_API_KEY,
                    query = query
                )
                _apiSearchResults.value = results
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Online search network failed", e)
                _apiSearchResults.value = emptyList()
            } finally {
                _isSearchingNetwork.value = false
            }
        }
    }

    fun clearApiSearch() {
        _apiSearchResults.value = emptyList()
    }

    fun addLocalPlant(
        name: String,
        species: String,
        wateringInterval: Int,
        location: String,
        plantType: String
    ) {
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

    fun onDeletePlantClicked(plant: TrackedPlant) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlant(plant)
        }
    }


    fun selectPlantFromNetwork(
        plantId: Int,
        fallbackCommonName: String?,
        fallbackScientific: String?,
        onDataFetched: (name: String, species: String, interval: String) -> Unit
    ) {
        val commonName = fallbackCommonName?.replaceFirstChar { it.uppercase() } ?: ""
        val plantSpecies = fallbackScientific ?: fallbackCommonName ?: ""

        onDataFetched(commonName,plantSpecies, "7")

        viewModelScope.launch {
            _isSearchingNetwork.value = true
            try {
                val completeDetails = repository.getPlantDetails(
                    apiKey = BuildConfig.NASA_API_KEY,
                    id = plantId
                )

                val calculatedInterval = when (completeDetails?.watering?.trim()?.lowercase()) {
                    "none"      -> "0"
                    "frequent"  -> "3"
                    "average"   -> "7"
                    "minimum"   -> "14"
                    else        -> "7"
                }

                onDataFetched(commonName,plantSpecies, calculatedInterval)

            } catch (e: Exception) {
                Log.e("PlantViewModel", "failed to resolve plant care details", e)
                onDataFetched(commonName,plantSpecies, "7")
            } finally {
                _isSearchingNetwork.value = false
            }
        }
    }
}