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

import androidx.compose.runtime.Immutable
import app.mobilemobile.solpan.solar.SolarCalculator
import kotlin.math.abs

@Immutable
data class AlignmentState(
    val phoneTargetAzimuth: Double,
    val targetTilt: Double,
    val targetRoll: Double,
    val currentAzimuth: Double,
    val currentPitch: Double,
    val currentRoll: Double,
    val azimuthDifference: Double,
    val tiltDifference: Double,
    val rollDifference: Double,
    val isAzimuthCorrect: Boolean,
    val isTiltCorrect: Boolean,
    val isRollCorrect: Boolean,
) {
    val isFullyAligned: Boolean
        get() = isAzimuthCorrect && isTiltCorrect && isRollCorrect

    companion object {
        private const val AZIMUTH_THRESHOLD = 5.0
        private const val TILT_THRESHOLD = 3.0
        private const val ROLL_THRESHOLD = 3.0
        private const val TARGET_ROLL = 0.0
        private const val HALF_ROTATION = 180.0
        private const val FULL_ROTATION = 360.0

        fun calculate(
            currentOrientation: OrientationData,
            targetParameters: OptimalPanelParameters,
            debugFakeAlignmentActive: Boolean = false,
        ): AlignmentState {
            val actualPanelTargetAzimuth =
                targetParameters.targetMagneticAzimuth ?: targetParameters.targetTrueAzimuth
            val phoneTargetAzimuth = (actualPanelTargetAzimuth + HALF_ROTATION) % FULL_ROTATION
            val targetTilt = targetParameters.targetTilt

            val currentAzimuth: Double
            val currentPitch: Double
            val currentRoll: Double

            if (debugFakeAlignmentActive) {
                currentAzimuth = phoneTargetAzimuth
                currentPitch = targetTilt
                currentRoll = TARGET_ROLL
            } else {
                currentAzimuth = currentOrientation.azimuth.toDouble()
                currentPitch = -currentOrientation.pitch.toDouble()
                currentRoll = currentOrientation.roll.toDouble()
            }

            val azimuthDifference =
                SolarCalculator.calculateAzimuthDifference(currentAzimuth, phoneTargetAzimuth)
            val tiltDifference = targetTilt - currentPitch
            val rollDifference = TARGET_ROLL - currentRoll

            return AlignmentState(
                phoneTargetAzimuth = phoneTargetAzimuth,
                targetTilt = targetTilt,
                targetRoll = TARGET_ROLL,
                currentAzimuth = currentAzimuth,
                currentPitch = currentPitch,
                currentRoll = currentRoll,
                azimuthDifference = azimuthDifference,
                tiltDifference = tiltDifference,
                rollDifference = rollDifference,
                isAzimuthCorrect =
                    debugFakeAlignmentActive || abs(azimuthDifference) <= AZIMUTH_THRESHOLD,
                isTiltCorrect = debugFakeAlignmentActive || abs(tiltDifference) <= TILT_THRESHOLD,
                isRollCorrect = debugFakeAlignmentActive || abs(rollDifference) <= ROLL_THRESHOLD,
            )
        }
    }
}
