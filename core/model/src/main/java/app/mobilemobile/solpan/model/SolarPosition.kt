/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

@Immutable
data class SolarPosition(
    val azimuth: Double,
    val altitude: Double,
)
