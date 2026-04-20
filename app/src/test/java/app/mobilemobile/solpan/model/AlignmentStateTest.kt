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
package app.mobilemobile.solpan.model

import app.mobilemobile.solpan.solar.SolarCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlignmentStateTest {
    private val targetParams =
        OptimalPanelParameters(
            targetTrueAzimuth = 180.0,
            targetMagneticAzimuth = 175.0,
            targetTilt = 45.0,
            mode = TiltMode.YEAR_ROUND,
        )

    private val baseOrientation = OrientationData(azimuth = 355f, pitch = -45f, roll = 0f)

    @Test
    fun `fully aligned when azimuth tilt and roll all within thresholds`() {
        val state =
            AlignmentState.calculate(
                baseOrientation,
                targetParams,
                calculateAzimuthDiff = SolarCalculator::calculateAzimuthDifference,
            )
        assertTrue(state.isFullyAligned)
        assertTrue(state.isAzimuthCorrect)
        assertTrue(state.isTiltCorrect)
        assertTrue(state.isRollCorrect)
    }

    @Test
    fun `azimuth incorrect when difference exceeds threshold`() {
        val orientation = baseOrientation.copy(azimuth = 340f) // 15 deg off (threshold is 5)
        val state =
            AlignmentState.calculate(
                orientation,
                targetParams,
                calculateAzimuthDiff = SolarCalculator::calculateAzimuthDifference,
            )
        assertFalse(state.isAzimuthCorrect)
        assertFalse(state.isFullyAligned)
    }

    @Test
    fun `debug fake alignment forces fully aligned regardless of orientation`() {
        val veryOffOrientation = OrientationData(azimuth = 0f, pitch = 0f, roll = 90f)
        val state =
            AlignmentState.calculate(
                veryOffOrientation,
                targetParams,
                debugFakeAlignmentActive = true,
                calculateAzimuthDiff = SolarCalculator::calculateAzimuthDifference,
            )
        assertTrue(state.isFullyAligned)
        assertEquals(targetParams.targetTilt, state.currentPitch, 0.1)
    }
}
