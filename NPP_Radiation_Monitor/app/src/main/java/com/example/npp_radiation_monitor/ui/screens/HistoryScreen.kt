package com.example.npp_radiation_monitor.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.ResourceMonitorComponent
import com.example.npp_radiation_monitor.ui.viewmodel.RadiationViewModel

/**
 * Екран моніторингу системних ресурсів пристрою в реальному часі.
 */
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: RadiationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Підключення та зчитування даних сенсорів
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            viewModel.updateSensorValues(event.values[0], event.values[1], event.values[2])
                        }
                        Sensor.TYPE_GYROSCOPE -> {
                            viewModel.updateGyroscopeValue(event.values[2])
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        if (gyroscope != null) {
            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Панель навігації
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Screen.MainScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Головна", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Моніторинг", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Інтерактивний компонент моніторингу системних ресурсів пристрою (CPU, RAM, батарея) у реальному часі
        ResourceMonitorComponent(
            cpuUsagePct = uiState.cpuUsagePercentage,
            ramUsedMb = uiState.ramUsedMb,
            ramTotalMb = uiState.ramTotalMb,
            ramPct = uiState.ramPercentage,
            batteryPct = uiState.batteryPercentage,
            isCharging = uiState.isBatteryCharging,
            onUpdateResources = { cpu, ramUsed, ramTotal, batteryPct, isCharging ->
                viewModel.updateSystemResources(cpu, ramUsed, ramTotal, batteryPct, isCharging)
            }
        )
    }
}
