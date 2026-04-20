/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

@Immutable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Float? = null,
    val accuracy: Float? = null,
)
