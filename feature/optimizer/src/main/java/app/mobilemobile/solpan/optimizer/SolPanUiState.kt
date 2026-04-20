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

@Immutable
data class SolPanUiState(
    val selectedMode: TiltMode,
    val currentLocation: LocationData? = null,
    val currentOrientation: OrientationData = OrientationData(),
    val optimalParams: OptimalPanelParameters? = null,
    val isDebugFakeAlignmentActive: Boolean = false,
    val showTutorial: Boolean = false,
    val lastUpdateTime: Instant? = null,
)
