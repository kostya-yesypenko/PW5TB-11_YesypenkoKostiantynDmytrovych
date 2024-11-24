package com.example.calc5
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") { MainMenuScreen(navController) }
        composable("reliability") { ReliabilityScreen(navController) }
        composable("loss_estimation") { LossEstimationScreen(navController) }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    Surface {
        Column {
            Button(onClick = { navController.navigate("reliability") }) {
                Text("Reliability Analysis")
            }
            Button(onClick = { navController.navigate("loss_estimation") }) {
                Text("Loss Estimation")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    val navController = rememberNavController()
    MainMenuScreen(navController)
}
