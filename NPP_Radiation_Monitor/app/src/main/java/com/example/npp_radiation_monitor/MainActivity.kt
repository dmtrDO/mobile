package com.example.npp_radiation_monitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.npp_radiation_monitor.ui.theme.NPP_Radiation_MonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppScreen()
        }
    }
}

@Composable
fun AppScreen() {
    NPP_Radiation_MonitorTheme {
        Column(
            modifier= Modifier
                .background(Color.Green)
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .padding(top=46.dp)
        ) {
            Text("hello", modifier= Modifier
                .offset(0.dp, 20.dp)
                .background(Color.Red)
            )
            Spacer(modifier=Modifier.height(50.dp).background(Color.Yellow))
            Text("hello", modifier = Modifier.background(Color.Blue))
        }
    }
}

