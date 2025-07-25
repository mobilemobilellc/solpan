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
package app.mobilemobile.solpan

import android.hardware.GeomagneticField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.solar.SolarCalculator
import app.mobilemobile.solpan.ui.SolPan
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime

private const val EARTH_AXIAL_TILT = 23.5

@OptIn(FlowPreview::class)
class SolPanViewModel(
    key: SolPan,
) : ViewModel() {
    class Factory(
        private val key: SolPan,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SolPanViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SolPanViewModel(key) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    private val _selectedTiltModeFlow = MutableStateFlow(key.mode)
    val selectedTiltModeFlow: StateFlow<TiltMode> = _selectedTiltModeFlow.asStateFlow()

    private val _debugFakeAlignmentActive = MutableStateFlow(false)
    val debugFakeAlignmentActive: StateFlow<Boolean> = _debugFakeAlignmentActive.asStateFlow()

    fun toggleDebugFakeAlignment() {
        _debugFakeAlignmentActive.update { !it }
    }

    private val magneticDeclinationFlow: StateFlow<Float?> =
        _currentLocation
            .map { location ->
                location?.let {
                    GeomagneticField(
                        it.latitude.toFloat(),
                        it.longitude.toFloat(),
                        it.altitude ?: 0f,
                        System.currentTimeMillis(),
                    ).declination
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val optimalPanelParameters: StateFlow<OptimalPanelParameters?> =
        combine(_currentLocation.debounce(300), magneticDeclinationFlow, _selectedTiltModeFlow) {
            location,
            declination,
            mode,
            ->
            calculateOptimalParameters(location, declination, mode)
        }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    fun updateLocation(newLocation: LocationData?) {
        _currentLocation.value = newLocation
    }

    private fun calculateOptimalParameters(
        location: LocationData?,
        declination: Float?,
        mode: TiltMode,
    ): OptimalPanelParameters? {
        if (location == null) {
            return null
        }

        val lat = location.latitude
        val targetTrueAzimuth: Double
        val targetTilt: Double

        val fixedTrueAzimuthEquator = if (lat > 0) 180.0 else 0.0

        when (mode) {
            TiltMode.REALTIME -> {
                val currentSunPos =
                    SolarCalculator.calculateSunPosition(
                        dateTime = ZonedDateTime.now(),
                        latitude = lat,
                        longitude = location.longitude,
                    )
                targetTrueAzimuth = currentSunPos.azimuth
                targetTilt = currentSunPos.altitude.coerceIn(0.0, 90.0)
            }

            TiltMode.WINTER -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = (kotlin.math.abs(lat) + EARTH_AXIAL_TILT).coerceIn(0.0, 90.0)
            }

            TiltMode.SUMMER -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = (kotlin.math.abs(lat) - EARTH_AXIAL_TILT).coerceIn(0.0, 90.0)
            }

            TiltMode.SPRING_AUTUMN -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = kotlin.math.abs(lat).coerceIn(0.0, 90.0)
            }

            TiltMode.YEAR_ROUND -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = kotlin.math.abs(lat).coerceIn(0.0, 90.0)
            }
        }

        val targetMagneticAzimuth = declination?.let { (targetTrueAzimuth - it + 360.0) % 360.0 }

        return OptimalPanelParameters(
            targetTrueAzimuth = targetTrueAzimuth,
            targetMagneticAzimuth = targetMagneticAzimuth,
            targetTilt = targetTilt,
            mode = mode,
            magneticDeclination = declination,
        )
    }
}
