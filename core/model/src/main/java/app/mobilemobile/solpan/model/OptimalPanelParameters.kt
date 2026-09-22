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
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

/**
 * Optimal solar panel alignment parameters calculated for the current context.
 *
 * Combines location, time, magnetic declination, and user's preferred tilt mode to produce the
 * target azimuth and tilt angles for panel orientation.
 *
 * @property targetTrueAzimuth True bearing (magnetic north independent) in degrees (0-360)
 * @property targetMagneticAzimuth Magnetic bearing for compass use (optional, null if declination
 *   unknown)
 * @property targetTilt Optimal elevation angle in degrees (0-90), where 0° = horizontal flat, 90° =
 *   vertical
 * @property mode The [TiltMode] strategy used to calculate these parameters
 * @property magneticDeclination Local magnetic declination from true north in degrees (optional,
 *   -180 to +180)
 */
@Immutable
public data class OptimalPanelParameters(
    val targetTrueAzimuth: Double,
    val targetMagneticAzimuth: Double?,
    val targetTilt: Double,
    val mode: TiltMode,
    val magneticDeclination: Float? = null,
)
