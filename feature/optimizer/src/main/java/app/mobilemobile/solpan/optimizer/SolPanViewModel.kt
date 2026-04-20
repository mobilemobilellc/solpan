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

/**
 * Main state management ViewModel for SolPan.
 *
 * Combines multiple reactive streams (location, orientation, magnetic declination, preferences)
 * to produce a unified [uiState] that the UI observes. All state updates are reactive and
 * observable via StateFlow, ensuring the UI always reflects the current system state.
 *
 * ## Reactive Architecture
 *
 * The ViewModel follows a reactive architecture pattern:
 * - **Location updates** are debounced (300ms) to avoid excessive recomputation
 * - **Magnetic declination** is computed lazily from location changes
 * - **Solar calculations** are recomputed when tilt mode or location changes
 * - **Realtime mode** ticks every 30 seconds to update sun position
 * - **UI state** combines all streams with [WhileSubscribed(5000)] to auto-cleanup when UI is backgrounded
 *
 * ## Key Flows
 *
 * - [optimalPanelParameters]: Combines location, declination, mode, and realtime ticks
 * - [uiState]: Master state object combining all UI-relevant data
 * - [showTutorial]: Combines user preferences with manual tutorial overrides
 *
 * ## Thread Safety
 *
 * All state flows are thread-safe and backed by coroutines. Updates from sensors/location
 * providers are marshalled through the [viewModelScope] dispatcher.
 *
 * @param initialMode The starting tilt mode (typically [TiltMode.REALTIME])
 * @param preferencesRepository User preferences (tutorial state, saved settings)
 * @param locationRepository Location stream and persistence
 * @param analytics Analytics event tracking
 * @param magneticDeclinationProvider Magnetic declination calculator (injectable for testing)
 */
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
        /**
         * Factory for creating [SolPanViewModel] instances with dependency injection.
         *
         * @param mode The initial tilt mode for this ViewModel instance
         * @param preferencesRepository Repository for user preferences
         * @param locationRepository Repository for location updates
         * @param analytics Analytics tracker instance
         * @return A ViewModel factory that creates properly-injected SolPanViewModel instances
         */
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

    /**
     * Updates the current device orientation from sensors.
     *
     * Called by [DeviceOrientationController] when accelerometer/magnetometer data is ready.
     * Updates trigger [uiState] recomposition if the orientation data changed.
     *
     * @param orientation The latest device orientation (pitch, roll, azimuth)
     */
    fun updateOrientation(orientation: OrientationData) {
        _currentOrientation.value = orientation
    }

    /** Current user location from GPS or fused location provider. Null if permission not granted. */
    val currentLocation: StateFlow<LocationData?> = locationRepository.currentLocation

    /**
     * Magnetic declination (angle between true north and magnetic north) at current location.
     *
     * Used to convert between true azimuth (calculated from sun position) and magnetic azimuth
     * (what compass reads). Computed lazily as location changes. Null until location is available.
     *
     * Range: -180° to +180° (negative = magnetic north is west of true north)
     */
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

    /**
     * Toggles debug mode for testing panel alignment without GPS lock.
     *
     * When enabled, the UI displays a fake "aligned" state even if the device
     * isn't actually aligned to the target azimuth. Useful for UI testing and screenshots.
     *
     * This is a debug-only feature and should not appear in production builds.
     */
    fun toggleDebugFakeAlignment() {
        _debugFakeAlignmentActive.update { !it }
    }

    /**
     * Dismisses the tutorial overlay and marks it as seen.
     *
     * Persists the state to [preferencesRepository] so the tutorial doesn't appear
     * on next app launch. Can be overridden by [requestTutorial].
     */
    fun dismissTutorial() {
        tutorialOverride.value = false
        viewModelScope.launch { preferencesRepository.setTutorialSeen(true) }
    }

    /**
     * Manually requests to show the tutorial overlay.
     *
     * Overrides the persisted "tutorial seen" state. Used when user wants to re-watch
     * the onboarding flow. Call [dismissTutorial] to close it.
     */
    fun requestTutorial() {
        tutorialOverride.value = true
    }

    /**
     * Updates the current location for optimization calculations.
     *
     * Typically called by [DeviceLocationManager] when location updates are available.
     * Updates trigger:
     * - Magnetic declination recalculation
     * - Solar position recalculation
     * - [uiState] recomposition
     *
     * @param newLocation The new location, or null to clear location
     */
    fun updateLocation(newLocation: LocationData?) {
        locationRepository.updateLocation(newLocation)
    }

    /**
     * Calculates optimal solar panel parameters for a given mode and location.
     *
     * Computation varies by tilt mode:
     * - **REALTIME**: Uses current sun position from ephemeris
     * - **SUMMER**: Uses summer solstice approximation (lat ± 23.5°)
     * - **WINTER**: Uses winter solstice approximation (lat ± 23.5°)
     * - **SPRING_AUTUMN/YEAR_ROUND**: Uses latitude directly (average annual)
     *
     * The true azimuth is converted to magnetic azimuth using declination if available.
     *
     * @param location The current location, or null to return null
     * @param declination Magnetic declination in degrees, or null to omit magnetic azimuth
     * @param mode The tilt mode determining calculation strategy
     * @return [OptimalPanelParameters] with target azimuth and tilt, or null if location unavailable
     */
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
