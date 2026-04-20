/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.optimizer

import androidx.compose.runtime.Immutable
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.model.TiltMode
import kotlinx.datetime.Instant

/**
 * Complete UI state for the SolPan app, combining user inputs and calculated results.
 *
 * This is the single source of truth for all screen rendering. The [SolPanViewModel]
 * produces this state by combining reactive streams from sensors, location, preferences,
 * and solar calculations.
 *
 * @property selectedMode Currently active [TiltMode] (determines how optimal tilt is calculated)
 * @property currentLocation Latest GPS location (nullable while acquiring signal)
 * @property currentOrientation Latest device orientation from fused sensor data
 * @property optimalParams Calculated optimal panel azimuth/tilt for current context (nullable if calculation failed)
 * @property isDebugFakeAlignmentActive Debug flag: if true, use synthetic location/orientation for testing
 * @property showTutorial Whether to display the first-use tutorial overlay
 * @property lastUpdateTime Timestamp of the last state update for debugging/diagnostics
 */
@Immutable
public data class SolPanUiState(
    val selectedMode: TiltMode,
    val currentLocation: LocationData? = null,
    val currentOrientation: OrientationData = OrientationData(),
    val optimalParams: OptimalPanelParameters? = null,
    val isDebugFakeAlignmentActive: Boolean = false,
    val showTutorial: Boolean = false,
    val lastUpdateTime: Instant? = null,
)
