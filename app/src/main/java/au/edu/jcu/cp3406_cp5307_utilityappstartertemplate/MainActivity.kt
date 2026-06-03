package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.R.attr.icon
import android.R.id.icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
// shader imports
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.BACKGROUND_SHADER_SRC
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
// shader anim
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
// retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel

const val myKey = BuildConfig.NASA_API_KEY

// retrofit


// root response object
data class PlantSpeciesInfo(
    val id: Int,
    val commonName: String,
    val wateringInterval: Int,
    val description: String
)

interface PlantAPI {
    @GET("species-list")
    suspend fun getPlantSpecies(
        @Query("key") apiKey : String,
        @Query("q") query: String
    ): List<PlantSpeciesInfo>
}


data class TrackedPlant(
    val id: String,
    val name: String,
    val species: String,
    val wateringIntervalDays: Int,
    val lastWatered: LocalDate
) {
    val daysUntilNextWater: Int
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val nextWateringDate = lastWatered.plusDays(wateringIntervalDays.toLong())
            return ChronoUnit.DAYS.between(LocalDate.now(), nextWateringDate).toInt()
        }
    val needsWatering: Boolean
        @RequiresApi(Build.VERSION_CODES.O)
        get() = daysUntilNextWater < 0
}

class PlantRepo(private val api: PlantAPI) {
    @RequiresApi(Build.VERSION_CODES.O)
    private val _trackedPlants = mutableListOf(
        TrackedPlant(
            UUID.randomUUID().toString(),
            "Rose",
            "Crimson Siluetta",
            3,
            LocalDate.now().minusDays(1)),
        TrackedPlant(
            UUID.randomUUID().toString(),
            "Lily",
            "Casa Blanca",
            2,
            LocalDate.now().minusDays(2)),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrackedPlants(): List<TrackedPlant> = _trackedPlants.toList()

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateWateringTime(plantId: String) {
        val index = _trackedPlants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val plant = _trackedPlants[index]
            _trackedPlants[index] = plant.copy(lastWatered = LocalDate.now())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewPlant(name: String, species: String, interval: Int)  {
        val newPlant = TrackedPlant(
            id = UUID.randomUUID().toString(),
            name = name,
            species = species,
            wateringIntervalDays = interval,
            lastWatered = LocalDate.now()
        )
        _trackedPlants.add(newPlant)
    }
}

data class UiState(
    val plants: List<TrackedPlant> = emptyList(),
    val sortByUrgency: Boolean = false,
    val isLoading: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
class PlantViewModel : ViewModel() {

    private val mockPlants = mutableListOf(
        TrackedPlant(
            UUID.randomUUID().toString(),
            "Rose",
            "Crimson Siluetta",
            3,
            LocalDate.now().minusDays(1)
        ),
        TrackedPlant(
            UUID.randomUUID().toString(),
            "Lily",
            "Casa Blanca",
            2,
            LocalDate.now().minusDays(2)
        ),
        TrackedPlant(
            UUID.randomUUID().toString(),
            "Fern",
            "Boston Sword",
            7,
            LocalDate.now().minusDays(0)
        )
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refreshUiList()
    }

    fun waterPlant(plantId: String) {
        val index = mockPlants.indexOfFirst { it.id == plantId }
        if (index != -1) {
            val plant = mockPlants[index]
            mockPlants[index] = plant.copy(lastWatered = LocalDate.now())
        }
        refreshUiList()
    }

    fun toggleSort(sort: Boolean) {
        _uiState.update { it.copy(sortByUrgency = sort) }
        refreshUiList()
    }

    private fun refreshUiList() {
        val sortedList = if (_uiState.value.sortByUrgency) {
            mockPlants.sortedBy { it.daysUntilNextWater }
        } else {
            mockPlants.sortedBy { it.name }
        }
        _uiState.update { it.copy(plants = sortedList) }
    }
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU) // shaders requires version 33
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                val shader = remember { RuntimeShader(BACKGROUND_SHADER_SRC) }
                val brush = remember { ShaderBrush(shader) }

                UtilityApp(shader, brush)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shader = RuntimeShader(BACKGROUND_SHADER_SRC)
            val brush = ShaderBrush(shader)
            UtilityApp(shader, brush)
        } else {
            Text("Shader support requires Android 13+")
        }
    }
}

// either svg or programmatically
@Composable
fun AppLogo () {

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UtilityApp(shader: RuntimeShader ,brush: ShaderBrush) {
    var selectedTab by remember { mutableStateOf("Utility") }

    val plantViewModel: PlantViewModel = viewModel()

    // infinite transition logic
    val infiniteTransition = rememberInfiniteTransition(label = "ShaderTime")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f, // one cycle
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing), // adjust number for anim length
            repeatMode = RepeatMode.Restart
        ),
        label = "TimeUniform"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Utility") },
                    label = { Text("Utility") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .drawWithCache {
                shader.setFloatUniform(
                    "iResolution",
                    size.width,
                    size.height
                )
                shader.setFloatUniform("iTime", time)
                shader.setFloatUniform("iDuration", 2.0f)

                onDrawBehind {
                    drawRect(brush)
                }
            }
        )
    {
            when (selectedTab) {
                "Utility" -> UtilityScreen(viewModel = plantViewModel)
                "Settings" -> SettingsScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtilityScreen(
    viewModel: PlantViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Utility Screen", style = MaterialTheme.typography.headlineMedium)

        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                    key = { plant -> plant.id}
                ) {plant ->
                    PlantCard(
                        plant = plant,
                        onWaterClick = { viewModel.waterPlant(plant.id)}
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
    val daysLeft = plant.daysUntilNextWater
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
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = plant.species,
                    style = MaterialTheme.typography.bodyMedium
                )
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
                // replace with icon after making the svgs
                Text(
                    text = if (isOverdue) "Thirsty" else "💧 Water",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// absolute value math eval for clean text translation logic
private fun absoluteValue(value: Int): Int = if (value < 0) -value else value

@Composable
fun SettingsScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp), Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings Screen", style = MaterialTheme.typography.headlineMedium)
        Text("This is where you can add toggles or preferences.")
    }
}