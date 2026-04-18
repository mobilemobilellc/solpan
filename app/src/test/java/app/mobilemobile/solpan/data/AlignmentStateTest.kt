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
package app.mobilemobile.solpan.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlignmentStateTest {
    private fun orientationAt(
        azimuth: Float = 0f,
        pitch: Float = 0f,
        roll: Float = 0f,
    ) = OrientationData(azimuth = azimuth, pitch = pitch, roll = roll)

    private fun targetAt(
        trueAzimuth: Double = 180.0,
        magAzimuth: Double? = 178.0,
        tilt: Double = 35.0,
    ) = OptimalPanelParameters(
        targetTrueAzimuth = trueAzimuth,
        targetMagneticAzimuth = magAzimuth,
        targetTilt = tilt,
        mode = TiltMode.YEAR_ROUND,
    )

    @Test
    fun `fully aligned when azimuth tilt and roll all within thresholds`() {
        // phoneTargetAzimuth = (178 + 180) % 360 = 358
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 358f, pitch = -35f, roll = 0f),
                targetParameters = targetAt(),
            )
        assertTrue(state.isAzimuthCorrect)
        assertTrue(state.isTiltCorrect)
        assertTrue(state.isRollCorrect)
        assertTrue(state.isFullyAligned)
    }

    @Test
    fun `azimuth incorrect when difference exceeds threshold`() {
        // phoneTargetAzimuth = 358, current = 340 → diff = -18 > 5
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 340f, pitch = -35f, roll = 0f),
                targetParameters = targetAt(),
            )
        assertFalse(state.isAzimuthCorrect)
        assertFalse(state.isFullyAligned)
    }

    @Test
    fun `azimuth correct when difference exactly at threshold`() {
        // phoneTargetAzimuth = 358, current = 353 → diff = -5 (at threshold)
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 353f, pitch = -35f, roll = 0f),
                targetParameters = targetAt(),
            )
        assertTrue(state.isAzimuthCorrect)
    }

    @Test
    fun `azimuth wraps correctly across north boundary`() {
        // phoneTargetAzimuth = (2 + 180) % 360 = 182, current = 177 → diff = -5 at threshold
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 177f, pitch = -35f, roll = 0f),
                targetParameters = targetAt(trueAzimuth = 0.0, magAzimuth = 2.0, tilt = 35.0),
            )
        assertTrue(state.isAzimuthCorrect)
    }

    @Test
    fun `tilt incorrect when device pitch too shallow`() {
        // targetTilt=35, currentPitch=-20 (pitch is negated) → tiltDifference=15 > 3
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 358f, pitch = -20f, roll = 0f),
                targetParameters = targetAt(),
            )
        assertFalse(state.isTiltCorrect)
        assertFalse(state.isFullyAligned)
    }

    @Test
    fun `tilt correct when within threshold`() {
        // targetTilt=35, currentPitch=-34 → tiltDiff=1 < 3
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 358f, pitch = -34f, roll = 0f),
                targetParameters = targetAt(),
            )
        assertTrue(state.isTiltCorrect)
    }

    @Test
    fun `roll incorrect when device tilted sideways`() {
        // roll = 10 > 3
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 358f, pitch = -35f, roll = 10f),
                targetParameters = targetAt(),
            )
        assertFalse(state.isRollCorrect)
    }

    @Test
    fun `debug fake alignment forces fully aligned regardless of orientation`() {
        // Completely wrong orientation, but debug mode
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 0f, pitch = 0f, roll = 45f),
                targetParameters = targetAt(),
                debugFakeAlignmentActive = true,
            )
        assertTrue(state.isAzimuthCorrect)
        assertTrue(state.isTiltCorrect)
        assertTrue(state.isRollCorrect)
        assertTrue(state.isFullyAligned)
    }

    @Test
    fun `debug fake alignment sets current values to target values`() {
        val target = targetAt(magAzimuth = 178.0, tilt = 35.0)
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(),
                targetParameters = target,
                debugFakeAlignmentActive = true,
            )
        // phoneTargetAzimuth = (178 + 180) % 360 = 358
        assertEquals(358.0, state.currentAzimuth, 0.001)
        assertEquals(35.0, state.currentPitch, 0.001)
        assertEquals(0.0, state.currentRoll, 0.001)
    }

    @Test
    fun `uses magnetic azimuth when available`() {
        // With magAzimuth=178, phoneTargetAzimuth=(178+180)%360=358
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 358f),
                targetParameters = targetAt(trueAzimuth = 180.0, magAzimuth = 178.0),
            )
        assertEquals(358.0, state.phoneTargetAzimuth, 0.001)
    }

    @Test
    fun `falls back to true azimuth when magnetic not available`() {
        // No magAzimuth, uses trueAzimuth=180, phoneTarget=(180+180)%360=0
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 0f),
                targetParameters = targetAt(trueAzimuth = 180.0, magAzimuth = null),
            )
        assertEquals(0.0, state.phoneTargetAzimuth, 0.001)
    }

    @Test
    fun `azimuth difference is signed correctly`() {
        // phoneTarget=358, current=355 → calculateAzimuthDifference(355, 358) = 3.0
        val state =
            AlignmentState.calculate(
                currentOrientation = orientationAt(azimuth = 355f),
                targetParameters = targetAt(),
            )
        assertEquals(3.0, state.azimuthDifference, 0.5)
    }
}
