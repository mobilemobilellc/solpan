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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.designsystem.components.InfoCard
import app.mobilemobile.solpan.model.AlignmentState
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.solar.SolarCalculator
import app.mobilemobile.solpan.util.format
import kotlin.math.abs

@Composable
fun GuidanceCard(
    currentOrientation: OrientationData,
    targetParameters: OptimalPanelParameters?,
    modifier: Modifier = Modifier,
    debugFakeAlignmentActive: Boolean = false,
) {
    if (targetParameters == null) {
        InfoCard(
            title = stringResource(id = R.string.guidance_card_title),
            icon = Icons.Filled.Tune,
            modifier = modifier,
        ) {
            Text(
                text = stringResource(id = R.string.guidance_waiting_for_target),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
        }
        return
    }

    val alignment =
        AlignmentState.calculate(
            currentOrientation,
            targetParameters,
            debugFakeAlignmentActive,
            SolarCalculator::calculateAzimuthDifference,
        )

    val azimuthInstruction: String
    var azimuthIconRotation: Float

    if (alignment.isAzimuthCorrect) {
        azimuthInstruction = stringResource(id = R.string.guidance_azimuth_aligned)
        azimuthIconRotation = 0f
    } else if (alignment.azimuthDifference > 0) {
        azimuthInstruction =
            stringResource(
                id = R.string.guidance_azimuth_rotate_left,
                alignment.phoneTargetAzimuth.format(0),
            )
        azimuthIconRotation = -45f
    } else {
        azimuthInstruction =
            stringResource(
                id = R.string.guidance_azimuth_rotate_right,
                alignment.phoneTargetAzimuth.format(0),
            )
        azimuthIconRotation = 45f
    }

    val maxRelevantAzimuthDiff = 45.0
    val azimuthProgress =
        if (alignment.isAzimuthCorrect) {
            1.0f
        } else {
            (1.0f - (abs(alignment.azimuthDifference) / maxRelevantAzimuthDiff).toFloat()).coerceIn(
                0.0f,
                1.0f,
            )
        }

    val maxRelevantTiltDiff = 30.0
    val tiltInstruction =
        when {
            alignment.isTiltCorrect -> {
                stringResource(id = R.string.guidance_tilt_optimal)
            }

            alignment.tiltDifference > 0 -> {
                stringResource(id = R.string.guidance_tilt_down, alignment.targetTilt.format(0))
            }

            else -> {
                stringResource(id = R.string.guidance_tilt_up, alignment.targetTilt.format(0))
            }
        }
    val tiltIcon =
        when {
            alignment.isTiltCorrect -> Icons.Filled.CheckCircle
            alignment.tiltDifference > 0 -> Icons.Filled.ArrowDownward
            else -> Icons.Filled.ArrowUpward
        }
    val tiltProgress =
        if (alignment.isTiltCorrect) {
            1.0f
        } else {
            (1.0f - (abs(alignment.tiltDifference) / maxRelevantTiltDiff).toFloat()).coerceIn(
                0.0f,
                1.0f,
            )
        }

    val rollThreshold = 3.0
    val maxRelevantRollDiff = 30.0
    val rollInstruction =
        when {
            alignment.isRollCorrect -> {
                stringResource(id = R.string.guidance_roll_level)
            }

            alignment.currentRoll > rollThreshold -> {
                stringResource(id = R.string.guidance_roll_tilt_left_down, alignment.targetRoll.format(0))
            }

            alignment.currentRoll < -rollThreshold -> {
                stringResource(
                    id = R.string.guidance_roll_tilt_right_down,
                    alignment.targetRoll.format(0),
                )
            }

            else -> {
                stringResource(id = R.string.guidance_roll_adjust_level)
            }
        }
    val rollIcon =
        when {
            alignment.isRollCorrect -> Icons.Filled.CheckCircle
            alignment.currentRoll > rollThreshold -> Icons.AutoMirrored.Filled.RotateLeft
            alignment.currentRoll < -rollThreshold -> Icons.AutoMirrored.Filled.RotateRight
            else -> Icons.Filled.Tune
        }
    val rollProgress =
        if (alignment.isRollCorrect) {
            1.0f
        } else {
            (1.0f - (abs(alignment.rollDifference) / maxRelevantRollDiff).toFloat()).coerceIn(
                0.0f,
                1.0f,
            )
        }

    InfoCard(
        title = stringResource(id = R.string.guidance_card_title),
        icon = Icons.Filled.Tune,
        modifier = modifier,
    ) {
        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_azimuth),
            currentValue =
                stringResource(id = R.string.target_param_value_degree_unit, alignment.currentAzimuth),
            targetValue =
                stringResource(
                    id = R.string.target_param_value_degree_unit,
                    alignment.phoneTargetAzimuth,
                ),
            instruction = azimuthInstruction,
            icon = if (alignment.isAzimuthCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cached,
            isCorrect = alignment.isAzimuthCorrect,
            progress = azimuthProgress,
            iconRotation = azimuthIconRotation, // This is already 0f if isAzimuthCorrect
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_tilt),
            currentValue =
                stringResource(id = R.string.target_param_value_degree_unit, alignment.currentPitch),
            targetValue =
                stringResource(id = R.string.target_param_value_degree_unit, alignment.targetTilt),
            instruction = tiltInstruction,
            icon = tiltIcon,
            isCorrect = alignment.isTiltCorrect,
            progress = tiltProgress,
        )

        Spacer(modifier = Modifier.height(12.dp))
        GuidanceRow(
            label = stringResource(id = R.string.guidance_label_device_roll),
            currentValue =
                stringResource(id = R.string.target_param_value_degree_unit, alignment.currentRoll),
            targetValue =
                stringResource(id = R.string.target_param_value_degree_unit, alignment.targetRoll),
            instruction = rollInstruction,
            icon = rollIcon,
            isCorrect = alignment.isRollCorrect,
            progress = rollProgress,
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.guidance_footer_instruction),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        val declination = targetParameters.magneticDeclination
        if (targetParameters.targetMagneticAzimuth == null && declination != null) {
            Text(
                text =
                    stringResource(
                        id = R.string.guidance_footer_true_north_declination,
                        declination.format(1),
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
    isCorrect: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    iconRotation: Float = 0f,
) {
    val contentColor =
        if (isCorrect) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Column(modifier = modifier.fillMaxWidth()) {
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

        @OptIn(ExperimentalMaterial3ExpressiveApi::class)
        LinearWavyProgressIndicator(
            progress = { if (isCorrect) 1.0f else progress },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
