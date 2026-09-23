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
package app.mobilemobile.solpan.orientation

import app.mobilemobile.solpan.orientation.DeviceOrientationController.Companion.orientationFromRadians
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.PI

class DeviceOrientationControllerTest {
    @Test
    fun `converts radians to degrees and wraps negative azimuth`() {
        val result =
            orientationFromRadians(
                floatArrayOf((-PI / 2).toFloat(), (PI / 4).toFloat(), (-PI / 6).toFloat()),
                sensorAccuracy = 3,
            )

        assertEquals(270f, result!!.azimuth, 0.01f)
        assertEquals(45f, result.pitch, 0.01f)
        assertEquals(-30f, result.roll, 0.01f)
        assertEquals(3, result.sensorAccuracy)
    }

    @Test
    fun `NaN pitch produces no reading`() {
        assertNull(orientationFromRadians(floatArrayOf(0f, Float.NaN, 0f), sensorAccuracy = null))
    }

    @Test
    fun `NaN azimuth produces no reading`() {
        assertNull(orientationFromRadians(floatArrayOf(Float.NaN, 0f, 0f), sensorAccuracy = null))
    }

    @Test
    fun `infinite roll produces no reading`() {
        assertNull(
            orientationFromRadians(
                floatArrayOf(0f, 0f, Float.POSITIVE_INFINITY),
                sensorAccuracy = null,
            ),
        )
    }
}
