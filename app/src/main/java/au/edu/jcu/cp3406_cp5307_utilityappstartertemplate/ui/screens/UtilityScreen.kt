package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.PlantViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtilityScreen(viewModel: PlantViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Utility Screen", style = MaterialTheme.typography.headlineMedium)

        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize()) {
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
                        onWaterClick = { viewModel.waterPlant(plant.id) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlantCard(
    plant: TrackedPlant,
    onWaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = plant.getDaysUntilNextWater()
    val isOverdue = daysLeft < 0

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
                    text = when {
                        daysLeft < 0 -> "Overdue by ${absoluteValue(daysLeft)} days"
                        daysLeft == 0 -> "Due today"
                        else -> "Water in $daysLeft days"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = onWaterClick) {
                Text(
                    text = if (isOverdue) "Thirsty" else "💧 Water",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun absoluteValue(value: Int): Int = if (value < 0) -value else value