/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package app.mobilemobile.solpan.location

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import app.mobilemobile.solpan.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private const val LOCATION_UPDATE_INTERVAL_MS = 10_000L
private const val LOCATION_MIN_UPDATE_INTERVAL_MS = 5_000L

interface DeviceLocationController {
    fun startLocationUpdates()

    fun stopLocationUpdates()
}

@Composable
fun rememberDeviceLocationController(
    enabled: Boolean,
    onLocationUpdate: (LocationData?) -> Unit,
): DeviceLocationController {
    val context = LocalContext.current
    val controller = remember { DeviceLocationManager(context, onLocationUpdate) }
    // Only while started, so a backgrounded app holds no location request.
    LifecycleStartEffect(controller, enabled) {
        if (enabled) controller.startLocationUpdates()
        onStopOrDispose { controller.stopLocationUpdates() }
    }
    return controller
}

class DeviceLocationManager(
    context: Context,
    private val onLocationUpdate: (LocationData?) -> Unit,
) : DeviceLocationController {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var isRequestingLocationUpdates = false

    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { androidLocation ->
                    val newDeviceLocation =
                        LocationData(
                            latitude = androidLocation.latitude,
                            longitude = androidLocation.longitude,
                            altitude =
                                if (androidLocation.hasAltitude()) {
                                    androidLocation.altitude.toFloat()
                                } else {
                                    null
                                },
                            accuracy =
                                if (androidLocation.hasAccuracy()) {
                                    androidLocation.accuracy
                                } else {
                                    null
                                },
                        )
                    onLocationUpdate(newDeviceLocation)
                }
            }
        }

    override fun startLocationUpdates() {
        if (isRequestingLocationUpdates) return

        val locationRequest =
            LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL_MS)
                .setMinUpdateIntervalMillis(LOCATION_MIN_UPDATE_INTERVAL_MS)
                .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
            isRequestingLocationUpdates = true
        } catch (e: SecurityException) {
            Log.e(
                "DeviceLocationManager",
                "Failed to request location updates due to SecurityException",
                e,
            )
            onLocationUpdate(null)
            isRequestingLocationUpdates = false
        } catch (e: IllegalStateException) {
            Log.e("DeviceLocationManager", "Failed to request location updates", e)
            onLocationUpdate(null)
            isRequestingLocationUpdates = false
        }
    }

    override fun stopLocationUpdates() {
        if (!isRequestingLocationUpdates) return
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isRequestingLocationUpdates = false
    }
}
