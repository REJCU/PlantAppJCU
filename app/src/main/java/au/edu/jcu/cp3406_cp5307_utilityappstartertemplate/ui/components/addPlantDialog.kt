package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.components

import android.R.attr.enabled
import android.R.attr.type
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, species: String, intervalDays: Int?, location: String, type: String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var speciesInput by remember { mutableStateOf("") }
    var intervalInput by remember { mutableStateOf("") }

    var locationExpanded by remember { mutableStateOf(false) }
    var locationInput by remember { mutableStateOf("") }
    val locations = listOf("Front", "Back", "Side")

    var typeExpanded by remember { mutableStateOf(false) }
    var typeInput by remember { mutableStateOf("") }
    val plantTypes = listOf("Succulent", "Fern", "Shrub", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track new plant", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = speciesInput,
                    onValueChange = { speciesInput = it },
                    label = { Text("Species") }
                )
                OutlinedTextField(
                    value = intervalInput,
                    onValueChange = { intervalValue ->
                        if (intervalValue.all { it.isDigit() }) {
                            intervalInput = intervalValue
                        }
                    },
                    label = { Text("Watering Interval") }
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