package com.example.calc5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LossEstimationScreen(navController: NavController) {
    var failureFrequency by remember { mutableStateOf("") }
    var avgRecoveryTime by remember { mutableStateOf("") }
    var nominalPower by remember { mutableStateOf("") }
    var tariffRate by remember { mutableStateOf("") }
    var plannedDowntimeCoeff by remember { mutableStateOf("") }
    var directEmergencyLoss by remember { mutableStateOf("") }
    var plannedLoss by remember { mutableStateOf("") }

    var emergencyShortfall by remember { mutableStateOf("") }
    var plannedShortfall by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Loss Estimation", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        ParameterInputField(failureFrequency, "Failure Frequency (ω)") { failureFrequency = it }
        ParameterInputField(avgRecoveryTime, "Average Repair Time (t_r)") { avgRecoveryTime = it }
        ParameterInputField(nominalPower, "Nominal Power (P_nom)") { nominalPower = it }
        ParameterInputField(tariffRate, "Tariff Rate (T_r)") { tariffRate = it }
        ParameterInputField(plannedDowntimeCoeff, "Planned Downtime Coeff. (k_plan)") { plannedDowntimeCoeff = it }
        ParameterInputField(directEmergencyLoss, "Emergency Losses (L_em)") { directEmergencyLoss = it }
        ParameterInputField(plannedLoss, "Planned Losses (L_plan)") { plannedLoss = it }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val emergencyMW = calculateEmergencyShortfall(
                failureFrequency.toDoubleOrNull() ?: 0.0,
                nominalPower.toDoubleOrNull() ?: 0.0,
                avgRecoveryTime.toDoubleOrNull() ?: 0.0,
                tariffRate.toDoubleOrNull() ?: 0.0
            )
            val plannedMW = calculatePlannedShortfall(
                plannedDowntimeCoeff.toDoubleOrNull() ?: 0.0,
                nominalPower.toDoubleOrNull() ?: 0.0,
                tariffRate.toDoubleOrNull() ?: 0.0
            )
            val total = calculateTotalLoss(
                emergencyMW,
                plannedMW,
                directEmergencyLoss.toDoubleOrNull() ?: 0.0,
                plannedLoss.toDoubleOrNull() ?: 0.0
            )

            emergencyShortfall = "%.4f".format(emergencyMW)
            plannedShortfall = "%.4f".format(plannedMW)
            totalCost = "%.4f".format(total)
        }) {
            Text("Compute Losses")
        }

        Spacer(Modifier.height(16.dp))

        ResultDisplay("Emergency Shortfall (MW_em)", emergencyShortfall)
        ResultDisplay("Planned Shortfall (MW_plan)", plannedShortfall)
        ResultDisplay("Total Loss (Cost)", totalCost)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}

@Composable
fun ParameterInputField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
fun ResultDisplay(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

fun calculateEmergencyShortfall(omega: Double, power: Double, time: Double, rate: Double): Double {
    return omega * power * time * rate
}

fun calculatePlannedShortfall(coeff: Double, power: Double, rate: Double): Double {
    return coeff * power * rate
}

fun calculateTotalLoss(emergencyMW: Double, plannedMW: Double, emergencyLoss: Double, plannedLoss: Double): Double {
    return emergencyLoss + (emergencyMW * emergencyLoss) + (plannedMW * plannedLoss)
}
