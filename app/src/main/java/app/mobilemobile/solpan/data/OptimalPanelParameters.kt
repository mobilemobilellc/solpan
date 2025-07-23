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
package app.mobilemobile.solpan.data

/**
 * @property targetTrueAzimuth
 * @property targetMagneticAzimuth
 * @property targetTilt
 * @property mode
 * @property magneticDeclination
 */
data class OptimalPanelParameters(
    val targetTrueAzimuth: Double,
    val targetMagneticAzimuth: Double?,
    val targetTilt: Double,
    val mode: TiltMode,
    val magneticDeclination: Float? = null,
)
