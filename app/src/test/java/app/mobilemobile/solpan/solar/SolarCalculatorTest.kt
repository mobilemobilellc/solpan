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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class SolarCalculatorTest {
    @Test
    fun `sun position at solar noon in northern hemisphere has positive altitude`() {
        val noon = ZonedDateTime.of(2026, 6, 21, 12, 0, 0, 0, ZoneId.of("America/Chicago"))
        val pos = SolarCalculator.calculateSunPosition(noon, latitude = 36.16, longitude = -86.78)
        assertTrue("Sun should be above horizon at noon, was ${pos.altitude}", pos.altitude > 0)
    }

    @Test
    fun `sun position at midnight has negative altitude`() {
        val midnight = ZonedDateTime.of(2026, 6, 21, 0, 0, 0, 0, ZoneId.of("America/Chicago"))
        val pos = SolarCalculator.calculateSunPosition(midnight, latitude = 36.16, longitude = -86.78)
        assertTrue("Sun should be below horizon at midnight, was ${pos.altitude}", pos.altitude < 0)
    }

    @Test
    fun `azimuth difference zero when same`() {
        assertEquals(0.0, SolarCalculator.calculateAzimuthDifference(180.0, 180.0), 0.001)
    }

    @Test
    fun `azimuth difference positive when target is clockwise`() {
        val diff = SolarCalculator.calculateAzimuthDifference(170.0, 190.0)
        assertEquals(20.0, diff, 0.001)
    }

    @Test
    fun `azimuth difference negative when target is counter-clockwise`() {
        val diff = SolarCalculator.calculateAzimuthDifference(190.0, 170.0)
        assertEquals(-20.0, diff, 0.001)
    }

    @Test
    fun `azimuth difference wraps correctly across north (0-360 boundary)`() {
        // Current 350°, target 10° → should be +20° (clockwise)
        val diff = SolarCalculator.calculateAzimuthDifference(350.0, 10.0)
        assertEquals(20.0, diff, 0.001)
    }

    @Test
    fun `azimuth difference wraps correctly counter-clockwise across north`() {
        // Current 10°, target 350° → should be -20° (counter-clockwise)
        val diff = SolarCalculator.calculateAzimuthDifference(10.0, 350.0)
        assertEquals(-20.0, diff, 0.001)
    }

    @Test
    fun `azimuth difference handles 180 degree offset`() {
        val diff = SolarCalculator.calculateAzimuthDifference(0.0, 180.0)
        assertEquals(180.0, diff, 0.001)
    }

    @Test
    fun `sun in southern hemisphere at summer solstice noon has positive altitude`() {
        val noon = ZonedDateTime.of(2026, 12, 21, 12, 0, 0, 0, ZoneId.of("Australia/Sydney"))
        val pos = SolarCalculator.calculateSunPosition(noon, latitude = -33.87, longitude = 151.21)
        assertTrue("Sun should be above horizon at Sydney noon, was ${pos.altitude}", pos.altitude > 0)
    }
}
