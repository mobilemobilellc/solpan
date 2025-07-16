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

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.mobilemobile.solpan.data.LocationData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

interface DeviceLocationController {
  fun startLocationUpdates()

  fun stopLocationUpdates()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberDeviceLocationController(
  onLocationUpdate: (LocationData?) -> Unit
): DeviceLocationController {
  val context = LocalContext.current
  val locationPermissionsState =
    rememberMultiplePermissionsState(
      listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    )

  val deviceLocationManager = remember {
    DeviceLocationManager(context, locationPermissionsState, onLocationUpdate)
  }

  LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
    if (locationPermissionsState.allPermissionsGranted) {
      deviceLocationManager.startLocationUpdatesInternal()
    } else {
      deviceLocationManager.stopLocationUpdates()
    }
  }

  DisposableEffect(Unit) { onDispose { deviceLocationManager.stopLocationUpdates() } }
  return deviceLocationManager
}

@OptIn(ExperimentalPermissionsApi::class)
class DeviceLocationManager(
  context: Context,
  private val permissionsState: MultiplePermissionsState,
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
        } ?: onLocationUpdate(null)
      }
    }

  override fun startLocationUpdates() {
    if (!permissionsState.allPermissionsGranted) {
      onLocationUpdate(null)
      return
    }
    startLocationUpdatesInternal()
  }

  internal fun startLocationUpdatesInternal() {
    if (isRequestingLocationUpdates) return

    if (!permissionsState.allPermissionsGranted) {
      stopLocationUpdates()
      onLocationUpdate(null)
      return
    }

    val locationRequest =
      LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setMinUpdateIntervalMillis(5000L)
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
    } catch (e: Exception) {
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
