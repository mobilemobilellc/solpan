/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

/**
 * Geographic location data from the device.
 *
 * @property latitude Latitude in decimal degrees (-90 to +90)
 * @property longitude Longitude in decimal degrees (-180 to +180)
 * @property altitude Altitude above sea level in meters (optional)
 * @property accuracy Horizontal accuracy in meters (optional)
 */
@Immutable
public data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Float? = null,
    val accuracy: Float? = null,
)
