package com.example.npp_radiation_monitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.npp_radiation_monitor.ui.screens.RadiationScreen
import com.example.npp_radiation_monitor.ui.theme.NPP_Radiation_MonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NPP_Radiation_MonitorTheme {
                Surface {
                    RadiationScreen()
                }
            }
        }
    }
}