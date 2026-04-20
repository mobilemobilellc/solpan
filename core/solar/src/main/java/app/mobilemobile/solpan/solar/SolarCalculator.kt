/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.solar

import app.mobilemobile.solpan.model.SolarPosition
import java.time.ZonedDateTime
import org.shredzone.commons.suncalc.SunPosition as ShredzoneApiSunPosition

/**
 * Core solar position calculations using NOAA algorithms from commons-suncalc.
 *
 * Provides solar azimuth/altitude at any geographic location and time,
 * plus utility methods for computing angular differences normalized to ±180°.
 *
 * **Thread-safe**: All methods are stateless and can be called concurrently.
 */
public object SolarCalculator {
    /**
     * Calculate the sun's position in the sky for a given location and time.
     *
     * Uses NOAA solar position algorithms (commons-suncalc) which are accurate
     * to within 0.01° when accounting for refraction effects.
     *
     * @param dateTime The date/time and timezone for the calculation (must be ZonedDateTime to handle DST)
     * @param latitude Latitude in decimal degrees (-90 to +90)
     * @param longitude Longitude in decimal degrees (-180 to +180)
     * @return [SolarPosition] containing azimuth (0-360°) and altitude (-90 to +90°)
     *
     * @see SolarPosition
     */
    public fun calculateSunPosition(
        dateTime: ZonedDateTime,
        latitude: Double,
        longitude: Double,
    ): SolarPosition {
        val shredzoneResult: ShredzoneApiSunPosition =
            ShredzoneApiSunPosition
                .compute()
                .on(dateTime)
                .at(latitude, longitude)
                .execute()

        val altitudeDeg: Double = shredzoneResult.altitude
        val azimuthDeg: Double = shredzoneResult.azimuth

        return SolarPosition(azimuth = azimuthDeg, altitude = altitudeDeg)
    }

    /**
     * Calculate the shortest angular difference between two bearings, normalized to ±180°.
     *
     * Useful for UI display (e.g., "turn 15° right" vs "turn 345° left").
     *
     * **Examples:**
     * - current=350°, target=10° → +20° (turn right)
     * - current=30°, target=350° → -40° (turn left)
     * - current=180°, target=0° → ±180° (ambiguous, returns +180°)
     *
     * @param currentAzimuth Current bearing in degrees (0-360)
     * @param targetAzimuth Target bearing in degrees (0-360)
     * @return Angular difference in degrees (-180 to +180), positive = clockwise
     */
    public fun calculateAzimuthDifference(
        currentAzimuth: Double,
        targetAzimuth: Double,
    ): Double {
        val diff = (targetAzimuth - currentAzimuth + 360.0) % 360.0
        return if (diff > 180.0) {
            diff - 360.0
        } else {
            diff
        }
    }
}
