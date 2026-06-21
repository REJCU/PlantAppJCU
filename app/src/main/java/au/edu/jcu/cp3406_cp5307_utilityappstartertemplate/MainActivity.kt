package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.graphics.RuntimeShader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.remote.PerennialApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.screens.SettingsScreen
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.screens.UtilityScreen
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.BACKGROUND_SHADER_SRC
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel.PlantViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository.PlantDatabase
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository.PlantRepo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val PERU_URL = "https://perenual.com/"

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init vals
        val database = PlantDatabase.getDatabase(applicationContext)
        val plantDao = database.plantDao()

        // init retrofit client
        val retrofit = Retrofit.Builder()
            .baseUrl(PERU_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val perennialApiService = retrofit.create(PerennialApi::class.java)

        // supply to repo instance
        val plantRepository = PlantRepo(plantDao, perennialApiService)

        // factory injection
        val plantViewModelFactory = viewModelFactory {
            initializer {
            PlantViewModel(plantRepository)
            }
        }

        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                val shader = remember { RuntimeShader(BACKGROUND_SHADER_SRC) }
                val brush = remember { ShaderBrush(shader) }
                UtilityApp(shader, brush, plantViewModelFactory)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun UtilityApp(
    shader: RuntimeShader,
    brush: ShaderBrush,
    plantViewModelFactory: ViewModelProvider.Factory
) {
    var selectedTab by remember { mutableStateOf("Utility") }
    val plantViewModel: PlantViewModel = viewModel(factory =  plantViewModelFactory)
    // val uiState by plantViewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "ShaderTime")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
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
        },
        floatingActionButton = {
            if (selectedTab == "Utility") {
                FloatingActionButton(
                    onClick = { plantViewModel.showAddPlantDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add plant")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .drawWithCache {
                shader.setFloatUniform("iResolution", size.width, size.height)
                shader.setFloatUniform("iTime", time)
                shader.setFloatUniform("iDuration", 2.0f)
                onDrawBehind { drawRect(brush) }
            }
        ) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(plantViewModel)
                "Settings" -> SettingsScreen(plantViewModel)
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

            val previewFactory = viewModelFactory {
                initializer {
                    PlantViewModel(PlantRepo(FakePlantDao(), null))
                }
            }
            UtilityApp(shader, brush, plantViewModelFactory = previewFactory)
        } else {
            Text("Shader support requires Android 13+")
        }
    }
}

// dummy class
class FakePlantDao : au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.repository.PlantDao {
    override fun getAllPlantsFLow(): kotlinx.coroutines.flow.Flow<List<au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant>> {
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
    override suspend fun getPlantById(plantId: String) = null
    override suspend fun insertPlant(plant: au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.model.TrackedPlant) {}
    override suspend fun waterPlant(plantId: String, todayDate: Long) {}
}
