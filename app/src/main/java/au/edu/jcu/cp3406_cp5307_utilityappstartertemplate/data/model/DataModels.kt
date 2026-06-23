package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model

data class UiState(
    val plants: List<TrackedPlant> = emptyList(),
    val sortByUrgency: Boolean = false,
    val isLoading: Boolean = false,
    val isAddPlantDialogVisible: Boolean = false,
    val selectedLocation: String = "All",
    val selectedPlantType: String = "All"
)

enum class PlantSortOrder{
    NAME,
    URGENCY
}



