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
package app.mobilemobile.solpan.solar

import app.mobilemobile.solpan.data.SolarPosition
import java.time.ZonedDateTime
import org.shredzone.commons.suncalc.SunPosition as ShredzoneApiSunPosition

object SolarCalculator {
  fun calculateSunPosition(
    dateTime: ZonedDateTime,
    latitude: Double,
    longitude: Double,
  ): SolarPosition {
    val shredzoneResult: ShredzoneApiSunPosition =
      ShredzoneApiSunPosition.compute().on(dateTime).at(latitude, longitude).execute()

    val altitudeDeg: Double = shredzoneResult.altitude
    val azimuthDeg: Double = shredzoneResult.azimuth

    return SolarPosition(azimuth = azimuthDeg, altitude = altitudeDeg)
  }

  fun calculateAzimuthDifference(currentAzimuth: Double, targetAzimuth: Double): Double {
    val diff = (targetAzimuth - currentAzimuth + 360.0) % 360.0
    return if (diff > 180.0) {
      diff - 360.0
    } else {
      diff
    }
  }
}
