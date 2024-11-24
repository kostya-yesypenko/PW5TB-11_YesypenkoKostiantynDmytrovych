package com.example.calc5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ReliabilityParameters(
    val failureRate: Double,
    val avgRepairTime: Int,
    val frequency: Double,
    val avgRecoveryTime: Int?
)

val reliabilityData = mapOf(
    "T-110 kV" to ReliabilityParameters(0.015, 100, 1.0, 43),
    "T-35 kV" to ReliabilityParameters(0.02, 80, 1.0, 28),
    "T-10 kV (Cable Network)" to ReliabilityParameters(0.005, 60, 0.5, 10),
    "T-10 kV (Overhead Network)" to ReliabilityParameters(0.05, 60, 0.5, 10),
    "B-110 kV (Gas-Insulated)" to ReliabilityParameters(0.01, 30, 0.1, 30),
    "B-10 kV (Oil)" to ReliabilityParameters(0.02, 15, 0.33, 15),
    "B-10 kV (Vacuum)" to ReliabilityParameters(0.05, 15, 0.33, 15),
    "Busbars 10 kV per Connection" to ReliabilityParameters(0.03, 2, 0.33, 15),
    "AV-0.38 kV" to ReliabilityParameters(0.05, 20, 1.0, 15),
    "ED 6,10 kV" to ReliabilityParameters(0.1, 50, 0.5, 0),
    "ED 0.38 kV" to ReliabilityParameters(0.1, 50, 0.5, 0),
    "PL-110 kV" to ReliabilityParameters(0.007, 10, 0.167, 35),
    "PL-35 kV" to ReliabilityParameters(0.02, 8, 0.167, 35),
    "PL-10 kV" to ReliabilityParameters(0.02, 10, 0.167, 35),
    "CL-10 kV (Trench)" to ReliabilityParameters(0.03, 44, 1.0, 9),
    "CL-10 kV (Cable Channel)" to ReliabilityParameters(0.005, 18, 1.0, 9)
)

@Composable
fun ReliabilityScreen(navController: NavController) {
    val quantities = remember {
        mutableMapOf<String, MutableState<String>>().apply {
            reliabilityData.keys.forEach { element ->
                this[element] = mutableStateOf("0")
            }
        }
    }

    var systemFrequency by remember { mutableStateOf("") }
    var averageRecoveryTime by remember { mutableStateOf("") }
    var accidentalDowntime by remember { mutableStateOf("") }
    var plannedDowntime by remember { mutableStateOf("") }
    var dualSystemFailureRate by remember { mutableStateOf("") }
    var finalFailureRate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Reliability Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Поля введення для кожного елемента
        reliabilityData.keys.forEach { element ->
            OutlinedTextField(
                value = quantities[element]?.value ?: "",
                onValueChange = { quantities[element]?.value = it },
                label = { Text(element) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Кнопка для розрахунків
        Button(onClick = {
            val results = calculateReliabilityMetrics(quantities, reliabilityData)
            systemFrequency = "%.4f".format(results[0])
            averageRecoveryTime = "%.4f".format(results[1])
            accidentalDowntime = "%.4f".format(results[2])
            plannedDowntime = "%.4f".format(results[3])
            dualSystemFailureRate = "%.4f".format(results[4])
            finalFailureRate = "%.4f".format(results[5])
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Виведення результатів
        DisplayResult("System Failure Frequency", systemFrequency)
        DisplayResult("Average Recovery Time", averageRecoveryTime)
        DisplayResult("Accidental Downtime Coefficient", accidentalDowntime)
        DisplayResult("Planned Downtime Coefficient", plannedDowntime)
        DisplayResult("Dual System Failure Rate", dualSystemFailureRate)
        DisplayResult("Final Failure Rate with Switch", finalFailureRate)

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка "Назад"
        Button(onClick = {
            navController.navigateUp() // Повернення на головний екран
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
fun DisplayResult(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

fun calculateReliabilityMetrics(
    quantities: Map<String, MutableState<String>>,
    elements: Map<String, ReliabilityParameters>
): List<Double> {
    var totalFailureRate = 0.0
    var weightedRecoveryTime = 0.0

    quantities.forEach { (key, state) ->
        val quantity = state.value.toIntOrNull() ?: 0
        val params = elements[key] ?: return@forEach

        if (quantity > 0) {
            totalFailureRate += quantity * params.failureRate
            weightedRecoveryTime += quantity * params.failureRate * params.avgRepairTime
        }
    }

    val averageRecovery = weightedRecoveryTime / totalFailureRate
    val accidentalDowntime = averageRecovery * totalFailureRate / 8760
    val plannedDowntime = 1.2 * 43 / 8760
    val dualFailureRate = 2 * totalFailureRate * (accidentalDowntime + plannedDowntime)
    val finalRate = dualFailureRate + 0.02

    return listOf(
        totalFailureRate,
        averageRecovery,
        accidentalDowntime,
        plannedDowntime,
        dualFailureRate,
        finalRate
    )
}
