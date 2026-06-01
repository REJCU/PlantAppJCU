package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.R
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.content.MediaType
import androidx.compose.runtime.getValue
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.BuildConfig
import java.net.URL
// retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow

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

    // infinite transition logic
    val infiniteTransition = rememberInfiniteTransition(label = "ShaderTime")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f, // one cycle
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing), // adjust number for anim length
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
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
                "Utility" -> UtilityScreen()
                "Settings" -> SettingsScreen()
            }
        }
    }
}

@Composable
fun UtilityScreen() {
    var counter by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Utility Screen", style = MaterialTheme.typography.headlineMedium)
        Text("Counter: $counter", style = MaterialTheme.typography.bodyLarge)

        Button(onClick = { counter++ }) {
            Text("Increment")
        }
    }
}

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