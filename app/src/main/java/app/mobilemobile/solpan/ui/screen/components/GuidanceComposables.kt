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
package app.mobilemobile.solpan.ui.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.solar.SolarCalculator // Corrected import
import app.mobilemobile.solpan.ui.theme.SolPanTheme
import app.mobilemobile.solpan.util.format
import kotlin.math.abs

@Composable
fun GuidanceCard(
    currentOrientation: OrientationData,
    targetParameters: OptimalPanelParameters?,
    debugFakeAlignmentActive: Boolean = false, // New parameter with default
) {
    if (targetParameters == null) {
        InfoCard(title = stringResource(id = R.string.guidance_card_title), icon = Icons.Filled.Tune) {
            Text(
                text = stringResource(id = R.string.guidance_waiting_for_target),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
        }
        return
    }

    // Determine the actual target azimuth for the panel
    val actualPanelTargetAzimuth =
        targetParameters.targetMagneticAzimuth ?: targetParameters.targetTrueAzimuth

    // The phone's top edge should point towards the panel's actual target azimuth.
    // So the phone's target azimuth is 180 degrees from the panel's target.
    val phoneTargetAzimuth = (actualPanelTargetAzimuth + 180) % 360.0

    val targetTilt = targetParameters.targetTilt
    val targetRoll = 0.0 // Assuming panel should be level side-to-side

    val currentAzimuthValueForCalculations: Double
    val currentPitchValueForCalculations: Double
    val currentRollValueForCalculations: Double

    if (debugFakeAlignmentActive) {
        currentAzimuthValueForCalculations = phoneTargetAzimuth
        currentPitchValueForCalculations = targetTilt
        currentRollValueForCalculations = targetRoll
    } else {
        currentAzimuthValueForCalculations = currentOrientation.azimuth.toDouble()
        currentPitchValueForCalculations = -currentOrientation.pitch.toDouble() // Invert pitch
        currentRollValueForCalculations = currentOrientation.roll.toDouble()
    }

    val azimuthDifference =
        SolarCalculator.calculateAzimuthDifference(
            currentAzimuthValueForCalculations,
            phoneTargetAzimuth,
        )
    val azimuthThreshold = 5.0 // degrees
    val maxRelevantAzimuthDiff = 45.0 // For progress calculation

    val isAzimuthCorrect =
        if (debugFakeAlignmentActive) true else abs(azimuthDifference) <= azimuthThreshold

    val azimuthInstruction: String
    var azimuthIconRotation: Float // Changed to var to allow modification

    if (isAzimuthCorrect) {
        azimuthInstruction = stringResource(id = R.string.guidance_azimuth_aligned)
        azimuthIconRotation = 0f
    } else if (azimuthDifference > 0) { // Current is to the East (clockwise) of phoneTargetAzimuth
        azimuthInstruction =
            stringResource(id = R.string.guidance_azimuth_rotate_left, phoneTargetAzimuth.format(0))
        azimuthIconRotation = -45f // Rotate icon left
    } else { // Current is to the West (counter-clockwise) of phoneTargetAzimuth
        azimuthInstruction =
            stringResource(id = R.string.guidance_azimuth_rotate_right, phoneTargetAzimuth.format(0))
        azimuthIconRotation = 45f // Rotate icon right
    }
    val azimuthProgress =
        if (isAzimuthCorrect) {
            1.0f
        } else {
            (1.0f - (abs(azimuthDifference) / maxRelevantAzimuthDiff).toFloat()).coerceIn(0.0f, 1.0f)
        }

    val tiltDifference = targetTilt - currentPitchValueForCalculations
    val tiltThreshold = 3.0
    val maxRelevantTiltDiff = 30.0

    val isTiltCorrect = if (debugFakeAlignmentActive) true else abs(tiltDifference) <= tiltThreshold

    val tiltInstruction =
        when {
            isTiltCorrect -> stringResource(id = R.string.guidance_tilt_optimal)
            tiltDifference > 0 -> stringResource(id = R.string.guidance_tilt_down, targetTilt.format(0))
            else -> stringResource(id = R.string.guidance_tilt_up, targetTilt.format(0))
        }
    val tiltIcon =
        when {
            isTiltCorrect -> Icons.Filled.CheckCircle
            tiltDifference > 0 -> Icons.Filled.ArrowDownward
            else -> Icons.Filled.ArrowUpward
        }

    val tiltProgress =
        if (isTiltCorrect) {
            1.0f
        } else {
            (1.0f - (abs(tiltDifference) / maxRelevantTiltDiff).toFloat()).coerceIn(0.0f, 1.0f)
        }

    val rollDifference = targetRoll - currentRollValueForCalculations
    val rollThreshold = 3.0
    val maxRelevantRollDiff = 30.0

    val isRollCorrect = if (debugFakeAlignmentActive) true else abs(rollDifference) <= rollThreshold

    val rollInstruction =
        when {
            isRollCorrect -> {
                stringResource(id = R.string.guidance_roll_level)
            }

            currentRollValueForCalculations > rollThreshold -> {
                stringResource(id = R.string.guidance_roll_tilt_left_down, targetRoll.format(0))
            }

            currentRollValueForCalculations < -rollThreshold -> {
                stringResource(id = R.string.guidance_roll_tilt_right_down, targetRoll.format(0))
            }

            else -> {
                stringResource(id = R.string.guidance_roll_adjust_level)
            }
        }

    val rollIcon =
        when {
            isRollCorrect -> Icons.Filled.CheckCircle
            currentRollValueForCalculations > rollThreshold -> Icons.AutoMirrored.Filled.RotateLeft
            currentRollValueForCalculations < -rollThreshold -> Icons.AutoMirrored.Filled.RotateRight
            else -> Icons.Filled.Tune
        }
    val rollProgress =
        if (isRollCorrect) {
            1.0f
        } else {
            (1.0f - (abs(rollDifference) / maxRelevantRollDiff).toFloat()).coerceIn(0.0f, 1.0f)
        }

    InfoCard(title = stringResource(id = R.string.guidance_card_title), icon = Icons.Filled.Tune) {
        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_azimuth),
            currentValue = "${currentAzimuthValueForCalculations.format(1)}°",
            targetValue = "${phoneTargetAzimuth.format(1)}°",
            instruction = azimuthInstruction,
            icon = if (isAzimuthCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cached,
            iconRotation = azimuthIconRotation, // This is already 0f if isAzimuthCorrect
            isCorrect = isAzimuthCorrect,
            progress = azimuthProgress,
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_tilt),
            currentValue = "${currentPitchValueForCalculations.format(1)}°",
            targetValue = "${targetTilt.format(1)}°",
            instruction = tiltInstruction,
            icon = tiltIcon,
            isCorrect = isTiltCorrect,
            progress = tiltProgress,
        )

        Spacer(modifier = Modifier.height(12.dp))
        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_roll),
            currentValue = "${currentRollValueForCalculations.format(1)}°",
            targetValue = "${targetRoll.format(1)}°",
            instruction = rollInstruction,
            icon = rollIcon,
            isCorrect = isRollCorrect,
            progress = rollProgress,
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.guidance_footer_instruction),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        if (targetParameters.targetMagneticAzimuth == null &&
            targetParameters.magneticDeclination != null
        ) {
            Text(
                text =
                    stringResource(
                        id = R.string.guidance_footer_true_north_declination,
                        targetParameters.magneticDeclination.format(1),
                    ),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        } else if (targetParameters.targetMagneticAzimuth != null) {
            Text(
                text = stringResource(id = R.string.guidance_footer_magnetic_north),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
fun GuidanceRow(
    label: String,
    currentValue: String,
    targetValue: String,
    instruction: String,
    icon: ImageVector?,
    iconRotation: Float = 0f,
    isCorrect: Boolean,
    progress: Float,
) {
    val contentColor =
        if (isCorrect) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = contentColor)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = instruction,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp).graphicsLayer(rotationZ = iconRotation),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.guidance_row_current_value, currentValue),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
            )
            Text(
                text = stringResource(id = R.string.guidance_row_target_value, targetValue),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
            )
        }
        Text(
            instruction,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                ),
            color = contentColor,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 2.dp, bottom = 4.dp),
        )

        LinearProgressIndicator(
            progress = { if (isCorrect) 1.0f else progress },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Preview(showBackground = true, name = "GuidanceCard Preview (Aligned)")
@Composable
fun GuidanceCardAlignedPreview() {
    SolPanTheme {
        Surface {
            // Panel target magnetic azimuth is 178.0. Phone's top should point to 178.0 for
            // alignment.
            val sampleOrientation = OrientationData(azimuth = 178f, pitch = -13f, roll = 1f)
            val sampleTarget =
                OptimalPanelParameters(
                    targetTrueAzimuth = 180.0,
                    targetMagneticAzimuth = 178.0, // Panel target magnetic azimuth
                    targetTilt = 12.0,
                    mode = TiltMode.SUMMER,
                    magneticDeclination = -2.0f,
                )
            GuidanceCard(
                currentOrientation = sampleOrientation,
                targetParameters = sampleTarget,
                debugFakeAlignmentActive = true,
            )
        }
    }
}

@Preview(showBackground = true, name = "GuidanceCard Preview (Misaligned)")
@Composable
fun GuidanceCardMisalignedPreview() {
    SolPanTheme {
        Surface {
            // Panel target magnetic azimuth is 178.0. Phone is currently pointing to 150f.
            val sampleOrientation = OrientationData(azimuth = 150f, pitch = -25f, roll = 10f)
            val sampleTarget =
                OptimalPanelParameters(
                    targetTrueAzimuth = 180.0,
                    targetMagneticAzimuth = 178.0, // Panel target magnetic azimuth
                    targetTilt = 12.0,
                    mode = TiltMode.SUMMER,
                    magneticDeclination = -2.0f,
                )
            GuidanceCard(currentOrientation = sampleOrientation, targetParameters = sampleTarget)
        }
    }
}

@Preview(showBackground = true, name = "GuidanceCard Preview (No Target)")
@Composable
fun GuidanceCardNoTargetPreview() {
    SolPanTheme {
        Surface {
            val sampleOrientation = OrientationData(azimuth = 150f, pitch = -25f, roll = 10f)
            GuidanceCard(currentOrientation = sampleOrientation, targetParameters = null)
        }
    }
}
