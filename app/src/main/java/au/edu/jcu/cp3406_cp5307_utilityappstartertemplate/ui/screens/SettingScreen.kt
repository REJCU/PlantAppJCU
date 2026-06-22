package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val houseLocations = listOf("All", "Front", "Back", "Right Side", "Left Side", "Inside")
    val plantTypes = listOf(
        "All",
        "Succulent & Cactus",
        "Fern",
        "Shrub",
        "Indoor Foliage",
        "Flowering Plant",
        "Herb & Vegetable",
        "Tree & Palm",
        "Vine & Climber",
        "Other"
    )

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Garden View Filters",
            style = MaterialTheme.typography.headlineMedium
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Filter view to a side of the house.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(houseLocations) { loc ->
                    FilterChip(
                        selected = uiState.selectedLocation == loc,
                        onClick = { viewModel.updateLocation(loc) },
                        label = { Text(loc) }
                    )
                }
            }
        }


        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Show only specific types of plants.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plantTypes) { type ->
                    FilterChip(
                        selected = uiState.selectedPlantType == type,
                        onClick = { viewModel.updatePlantType(type) },
                        label = { Text(type) }
                    )
                }
            }
        }
    }
}