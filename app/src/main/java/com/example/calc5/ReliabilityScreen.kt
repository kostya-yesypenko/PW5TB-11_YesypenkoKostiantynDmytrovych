package com.example.calc5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class EquipmentParams(
    val failureRate: Double,
    val repairTime: Double,
    val occurrenceFreq: Double,
    val recoveryDuration: Double
)

val equipmentData = mapOf(
    "Transformer 110kV" to EquipmentParams(0.015, 100.0, 1.0, 43.0),
    "Transformer 35kV" to EquipmentParams(0.020, 80.0, 1.0, 28.0),
    "Cable Network 10kV" to EquipmentParams(0.005, 60.0, 0.5, 10.0)
)

@Composable
fun ReliabilityScreen(navController: NavController) {
    val equipmentQuantities = remember {
        mutableMapOf<String, MutableState<String>>().apply {
            equipmentData.keys.forEach { this[it] = mutableStateOf("0") }
        }
    }

    var systemFrequency by remember { mutableStateOf("") }
    var avgRepairTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Reliability Analysis", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        equipmentData.keys.forEach { label ->
            OutlinedTextField(
                value = equipmentQuantities[label]?.value ?: "",
                onValueChange = { equipmentQuantities[label]?.value = it },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(onClick = {
            val results = calculateSystemReliability(equipmentQuantities)
            systemFrequency = "%.4f".format(results.first)
            avgRepairTime = "%.4f".format(results.second)
        }) {
            Text("Compute Reliability")
        }

        Spacer(Modifier.height(16.dp))

        ResultDisplay("System Failure Frequency", systemFrequency)
        ResultDisplay("Average Repair Time", avgRepairTime)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}

fun calculateSystemReliability(
    quantities: Map<String, MutableState<String>>
): Pair<Double, Double> {
    var totalFailures = 0.0
    var weightedRepairTime = 0.0

    quantities.forEach { (key, state) ->
        val count = state.value.toIntOrNull() ?: 0
        val params = equipmentData[key] ?: return@forEach
        totalFailures += count * params.failureRate
        weightedRepairTime += count * params.failureRate * params.repairTime
    }

    val avgRepairTime = weightedRepairTime / totalFailures
    return totalFailures to avgRepairTime
}
