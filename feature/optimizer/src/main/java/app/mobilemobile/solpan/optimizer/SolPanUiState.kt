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
 * This is the single source of truth for all screen rendering. The [SolPanViewModel] produces this
 * state by combining reactive streams from sensors, location, preferences, and solar calculations.
 *
 * @property selectedMode Currently active [TiltMode] (determines how optimal tilt is calculated)
 * @property currentLocation Latest GPS location (nullable while acquiring signal)
 * @property currentOrientation Latest device orientation from fused sensor data
 * @property optimalParams Calculated optimal panel azimuth/tilt for current context (nullable if
 *   calculation failed)
 * @property isDebugFakeAlignmentActive Debug flag: if true, use synthetic location/orientation for
 *   testing
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
