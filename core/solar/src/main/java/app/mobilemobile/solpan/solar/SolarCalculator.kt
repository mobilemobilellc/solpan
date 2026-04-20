/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.solar

import app.mobilemobile.solpan.model.SolarPosition
import java.time.ZonedDateTime
import org.shredzone.commons.suncalc.SunPosition as ShredzoneApiSunPosition

object SolarCalculator {
    fun calculateSunPosition(
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

    fun calculateAzimuthDifference(
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
