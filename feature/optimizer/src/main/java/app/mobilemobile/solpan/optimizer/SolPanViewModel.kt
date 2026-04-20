/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.optimizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.mobilemobile.solpan.analytics.AnalyticsTracker
import app.mobilemobile.solpan.analytics.logPermissionResult
import app.mobilemobile.solpan.analytics.logTutorialEnded
import app.mobilemobile.solpan.analytics.logTutorialStarted
import app.mobilemobile.solpan.data.LocationRepository
import app.mobilemobile.solpan.data.UserPreferencesRepository
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.model.TiltMode
import app.mobilemobile.solpan.solar.SolarCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.time.ZonedDateTime
import kotlin.math.abs

private const val EARTH_AXIAL_TILT = 23.5
private const val REALTIME_TICK_INTERVAL_MS = 30_000L

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SolPanViewModel(
    val initialMode: TiltMode,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationRepository: LocationRepository,
    private val analytics: AnalyticsTracker,
    private val magneticDeclinationProvider: MagneticDeclinationProvider =
        AndroidMagneticDeclinationProvider(),
) : ViewModel() {
    companion object {
        fun factory(
            mode: TiltMode,
            preferencesRepository: UserPreferencesRepository,
            locationRepository: LocationRepository,
            analytics: AnalyticsTracker,
        ) =
            viewModelFactory {
                initializer {
                    SolPanViewModel(mode, preferencesRepository, locationRepository, analytics)
                }
            }
    }

    private val _selectedTiltModeFlow = MutableStateFlow(initialMode)
    private val _debugFakeAlignmentActive = MutableStateFlow(false)
    private val tutorialOverride = MutableStateFlow<Boolean?>(null)
    private val _currentOrientation = MutableStateFlow(OrientationData())

    fun updateOrientation(orientation: OrientationData) {
        _currentOrientation.value = orientation
    }

    val currentLocation: StateFlow<LocationData?> = locationRepository.currentLocation

    private val magneticDeclinationFlow: StateFlow<Float?> =
        locationRepository.currentLocation
            .map { location ->
                location?.let {
                    magneticDeclinationProvider.getMagneticDeclination(
                        it.latitude.toFloat(),
                        it.longitude.toFloat(),
                        it.altitude ?: 0f,
                        System.currentTimeMillis(),
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val optimalPanelParameters: StateFlow<OptimalPanelParameters?> =
        combine(
            locationRepository.currentLocation.debounce(300),
            magneticDeclinationFlow,
            _selectedTiltModeFlow,
            realtimeTickerFlow(_selectedTiltModeFlow),
        ) { location, declination, mode, _ ->
            calculateOptimalParameters(location, declination, mode)
        }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val showTutorial: StateFlow<Boolean> =
        combine(
            preferencesRepository.tutorialSeen,
            tutorialOverride,
        ) { seen, override ->
            override ?: !seen
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * The single source of truth for the UI.
     * Masterclass Architecture: Unifying multiple streams into one immutable state object.
     */
    val uiState: StateFlow<SolPanUiState> =
        combine(
            _selectedTiltModeFlow,
            locationRepository.currentLocation,
            _currentOrientation,
            optimalPanelParameters,
            _debugFakeAlignmentActive,
            showTutorial,
        ) { flows ->
            val mode = flows[0] as TiltMode
            val location = flows[1] as LocationData?
            val orientation = flows[2] as OrientationData
            val params = flows[3] as OptimalPanelParameters?
            val debug = flows[4] as Boolean
            val showTutorial = flows[5] as Boolean

            SolPanUiState(
                selectedMode = mode,
                currentLocation = location,
                currentOrientation = orientation,
                optimalParams = params,
                isDebugFakeAlignmentActive = debug,
                showTutorial = showTutorial,
                lastUpdateTime = Clock.System.now(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SolPanUiState(selectedMode = initialMode),
        )

    fun onTutorialStarted() = with(analytics) { logTutorialStarted() }
    fun onTutorialEnded() = with(analytics) { logTutorialEnded() }
    fun onPermissionResult(granted: Boolean) = with(analytics) { logPermissionResult(granted) }

    fun toggleDebugFakeAlignment() {
        _debugFakeAlignmentActive.update { !it }
    }

    fun dismissTutorial() {
        tutorialOverride.value = false
        viewModelScope.launch { preferencesRepository.setTutorialSeen(true) }
    }

    fun requestTutorial() {
        tutorialOverride.value = true
    }

    fun updateLocation(newLocation: LocationData?) {
        locationRepository.updateLocation(newLocation)
    }

    private fun calculateOptimalParameters(
        location: LocationData?,
        declination: Float?,
        mode: TiltMode,
    ): OptimalPanelParameters? {
        if (location == null) return null

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
                targetTilt = (abs(lat) + EARTH_AXIAL_TILT).coerceIn(0.0, 90.0)
            }
            TiltMode.SUMMER -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = (abs(lat) - EARTH_AXIAL_TILT).coerceIn(0.0, 90.0)
            }
            TiltMode.SPRING_AUTUMN,
            TiltMode.YEAR_ROUND -> {
                targetTrueAzimuth = fixedTrueAzimuthEquator
                targetTilt = abs(lat).coerceIn(0.0, 90.0)
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

@OptIn(ExperimentalCoroutinesApi::class)
private fun realtimeTickerFlow(modeFlow: StateFlow<TiltMode>) =
    modeFlow.flatMapLatest { mode ->
        flow {
            emit(Unit)
            if (mode == TiltMode.REALTIME) {
                while (true) {
                    delay(REALTIME_TICK_INTERVAL_MS)
                    emit(Unit)
                }
            }
        }
    }
