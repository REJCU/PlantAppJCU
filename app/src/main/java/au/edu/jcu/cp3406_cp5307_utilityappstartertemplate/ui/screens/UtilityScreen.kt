package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.R
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.components.AddPlantDialog
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.PlantViewModel
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtilityScreen(viewModel: PlantViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val apiResult by viewModel.apiSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingNetwork.collectAsState()

    var editedPlant by remember { mutableStateOf<TrackedPlant?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Garden View", style = MaterialTheme.typography.headlineMedium)

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.plants,
                    key = { plant -> plant.id }
                ) { plant ->
                    PlantCard(
                        plant = plant,
                        onWaterClick = { viewModel.waterPlant(plant.id) },
                        onDeleteClick = {viewModel.onDeletePlantClicked(plant)},
                        onEditClick = {editedPlant = plant}
                    )
                }
            }
        }
    }

    if (uiState.isAddPlantDialogVisible) {
        AddPlantDialog(
            onDismiss = { viewModel.dismissAddPlantDialog() },
            onConfirm = { name, species, interval, location, type ->
                viewModel.addLocalPlant(
                    name = name,
                    species = species,
                    wateringInterval = interval ?: 7,
                    location = location,
                    plantType = type
                )
            },
            apiResult = apiResult,
            isSearching = isSearching,
            onSearchQueryChanged = { query ->
                viewModel.searchOnlineDatabase(query)
            },
            onPlantSelected = { id, fallbackCommonName, fallbackScientific, onFetched ->
                viewModel.selectPlantFromNetwork(id, fallbackCommonName, fallbackScientific, onFetched)
            }
        )
    }

    if (editedPlant != null) {
        AddPlantDialog(
            editExistingPlant = editedPlant,
            onDismiss = { editedPlant = null },
            onConfirm = { name, species, interval, location, type ->
                viewModel.editLocalPlant(
                    plantId = editedPlant!!.id,
                    newName = name,
                    newSpecies = species,
                    newInterval = interval ?: 7,
                    newLocation = location,
                    newPlantType = type
                )
                editedPlant = null
            },
            apiResult = apiResult,
            isSearching = isSearching,
            onSearchQueryChanged = { query -> viewModel.searchOnlineDatabase(query) },
            onPlantSelected = { id, fallbackCommonName, fallbackScientific, onFetched ->
                viewModel.selectPlantFromNetwork(id, fallbackCommonName, fallbackScientific, onFetched)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlantCard(
    plant: TrackedPlant,
    onWaterClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = plant.getDaysUntilNextWater()
    val isOverdue = daysLeft < 0

    var expanded by remember { mutableStateOf(false) }

    val cardColors = if (isOverdue) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    } else {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        colors = cardColors,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plant.name, style = MaterialTheme.typography.titleLarge)
                Text(text = plant.species, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${plant.location} • ${plant.plantType}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = when {
                        daysLeft < 0 -> "Overdue by ${abs(daysLeft)} days"
                        daysLeft == 0 -> "Due today"
                        else -> "Water in $daysLeft days"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
            FilledTonalButton (onClick = onWaterClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                     if (isOverdue) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    if (isOverdue) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )) {
                Icon(
                    painterResource(R.drawable.outline_water_drop),
                    contentDescription = if (isOverdue) "Plant is overdue for water" else "Water plant",
                    tint = if (isOverdue) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.primary
                    )
            }
                Box {
                    IconButton(onClick = {expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options"
                        )
                    }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Details") },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Delete Plant",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                            }
                        )
                    }
                }
            }
        }
    }
}