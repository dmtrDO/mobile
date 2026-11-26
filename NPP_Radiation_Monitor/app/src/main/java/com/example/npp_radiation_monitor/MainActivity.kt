package com.example.npp_radiation_monitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.npp_radiation_monitor.navigation.Navigation
import com.example.npp_radiation_monitor.ui.theme.NPP_Radiation_MonitorTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure osmdroid BEFORE creating the UI
        Configuration.getInstance().apply {
            load(
                this@MainActivity,
                getSharedPreferences("osmdroid", MODE_PRIVATE)
            )
            userAgentValue = "NPPRadiationMonitor/1.0 (domaratskyi.dmytro_tv33@edu.kpi.ua)"
        }

        enableEdgeToEdge()

        setContent {
            NPP_Radiation_MonitorTheme {
                Navigation()
            }
        }
    }
}
