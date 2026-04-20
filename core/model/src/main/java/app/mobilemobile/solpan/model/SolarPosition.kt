/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

/**
 * Sun's position in the sky relative to the observer.
 *
 * Calculated using solar noon, declination, and equation of time algorithms
 * from commons-suncalc library.
 *
 * @property azimuth Compass bearing to the sun in degrees (0-360), where 0° = North
 * @property altitude Sun's angle above horizon in degrees (-90 to +90), positive = above horizon
 */
@Immutable
public data class SolarPosition(
    val azimuth: Double,
    val altitude: Double,
)
