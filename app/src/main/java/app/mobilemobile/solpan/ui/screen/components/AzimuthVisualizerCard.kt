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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.AlignmentState
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.ui.components.AzimuthAwareBubbleLevel

@Composable
fun AzimuthVisualizerCard(
    currentOrientation: OrientationData,
    targetParameters: OptimalPanelParameters?,
    modifier: Modifier = Modifier,
    debugFakeAlignmentActive: Boolean = false,
) {
    if (targetParameters == null) {
        InfoCard(
            title = stringResource(id = R.string.azimuth_visualizer_card_title),
            icon = Icons.Filled.Explore,
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
        AlignmentState.calculate(currentOrientation, targetParameters, debugFakeAlignmentActive)

    InfoCard(
        title = stringResource(id = R.string.azimuth_visualizer_card_title),
        icon = Icons.Filled.Explore,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.guidance_level_device_title),
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally).semantics {
                    heading()
                },
        )

        AzimuthAwareBubbleLevel(
            currentPitch = alignment.currentPitch,
            currentRoll = alignment.currentRoll,
            targetPitch = alignment.targetTilt,
            currentAzimuth = alignment.currentAzimuth,
            targetAzimuth = alignment.phoneTargetAzimuth,
            pitchAlignmentThresholdDeg = 3.0,
            rollAlignmentThresholdDeg = 3.0,
            azimuthAlignmentThresholdDeg = 5.0,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            maxAngleDeviation = 15f,
        )

        val guidanceTextColor =
            if (alignment.isFullyAligned) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

        Text(
            text =
                when {
                    alignment.isFullyAligned -> {
                        stringResource(id = R.string.guidance_perfectly_aligned)
                    }

                    alignment.isTiltCorrect && alignment.isRollCorrect -> {
                        stringResource(id = R.string.guidance_level_adjust_azimuth)
                    }

                    else -> {
                        stringResource(id = R.string.guidance_adjust_bubble_azimuth)
                    }
                },
            color = guidanceTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        )
    }
}
