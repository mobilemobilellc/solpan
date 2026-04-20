/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

@Immutable
data class OptimalPanelParameters(
    val targetTrueAzimuth: Double,
    val targetMagneticAzimuth: Double?,
    val targetTilt: Double,
    val mode: TiltMode,
    val magneticDeclination: Float? = null,
)
