package com.example.npp_radiation_monitor.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.npp_radiation_monitor.data.model.EnergyStation
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreenComponent(
    stations: List<EnergyStation>,
    userLat: Double,
    userLng: Double,
    onStationClick: (EnergyStation) -> Unit
) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "дозволити додатку доступ до місцезнаходження пристрою?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("надати дозвіл gps")
                    }
                }
            }
        }

        AndroidView(
            factory = { ctx ->
                val mapTiler = object : OnlineTileSourceBase(
                    "MapTilerStreets", 0, 19, 256, ".png",
                    arrayOf("https://api.maptiler.com/maps/streets-v2/256/"),
                    "© MapTiler © OpenStreetMap contributors"
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String =
                        baseUrl +
                                MapTileIndex.getZoom(pMapTileIndex) + "/" +
                                MapTileIndex.getX(pMapTileIndex) + "/" +
                                MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding +
                                "?key=CXrIzHc97vgBW3WPXi31"
                }

                MapView(ctx).apply {
                    setTileSource(mapTiler)
                    setMultiTouchControls(true)
                    controller.setZoom(7.0)
                    controller.setCenter(GeoPoint(50.4501, 30.5234))
                }
            },
            update = { mapView ->
                mapView.overlays.clear()

                stations.forEach { station ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(station.latitude, station.longitude)
                        title = station.name
                        snippet = "тип: ${station.stationType} | стан: ${station.status} | фон: ${station.radiationValue} мкЗв/год"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { m, _ ->
                            m.showInfoWindow()
                            onStationClick(station)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
                mapView.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
