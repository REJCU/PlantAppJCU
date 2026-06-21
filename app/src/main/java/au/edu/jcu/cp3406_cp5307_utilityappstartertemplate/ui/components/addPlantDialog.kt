package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantSearchResponse
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PlantSearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int?, String, String) -> Unit,
    apiResult: List<PlantSearchResult>,
    isSearching: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onPlantSelected: (plantId: Int, fallbackCommonName: String?, fallbackScientific: String?, onFetched: (String, String, String) -> Unit) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var speciesInput by remember { mutableStateOf("") }
    var intervalInput by remember { mutableStateOf("") }

    var locationExpanded by remember { mutableStateOf(false) }
    var locationInput by remember { mutableStateOf("") }
    val locations = listOf("Front", "Back", "Right Side", "Left Side", "Inside")

    var typeExpanded by remember { mutableStateOf(false) }
    var typeInput by remember { mutableStateOf("") }
    val plantTypes = listOf(
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
    var apiDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track new plant", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = apiDropdownExpanded && (isSearching || apiResult.isNotEmpty()),
                    onExpandedChange = { apiDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = speciesInput,
                        onValueChange = { newValue ->
                            speciesInput = newValue
                            apiDropdownExpanded = true
                            onSearchQueryChanged(newValue)
                        },
                        label = { Text("Species") },
                        trailingIcon = {
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.fillMaxWidth(0.1f))
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = apiDropdownExpanded)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = apiDropdownExpanded && (isSearching || apiResult.isNotEmpty()),
                        onDismissRequest = { apiDropdownExpanded = false },
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        apiResult.forEach { plantMatch ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = plantMatch.common_name ?: "Unknown Common Name", style = MaterialTheme.typography.bodyLarge)

                                        val primaryScientific = plantMatch.scientific_name?.firstOrNull()
                                        if (primaryScientific != null) {
                                            Text(text = primaryScientific, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                },
                                onClick = {
                                    android.util.Log.d("PlantDebug", "watering ${plantMatch.watering}")
                                    val primaryScientific = plantMatch.scientific_name?.firstOrNull()

                                    onPlantSelected(
                                        plantMatch.id,
                                        plantMatch.common_name,
                                       primaryScientific
                                    ) { name, species, interval ->
                                        nameInput = name
                                        speciesInput = species
                                        intervalInput = interval
                                    }

                                    apiDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = intervalInput,
                    onValueChange = { intervalValue ->
                        if (intervalValue.all { it.isDigit() }) {
                            intervalInput = intervalValue
                        }
                    },
                    label = { Text("Watering Interval") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Location") },
                        placeholder = { Text("Location")},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        locations.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    locationInput = selectionOption
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = typeInput,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Plant Type") },
                        placeholder = { Text("Location")},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        plantTypes.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    typeInput = selectionOption
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank() && speciesInput.isNotBlank()) {
                        val intervalInt = intervalInput.toIntOrNull()
                        onConfirm(nameInput, speciesInput, intervalInt, locationInput, typeInput)
                    }
                }
            ) {
                Text("Add Plant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}