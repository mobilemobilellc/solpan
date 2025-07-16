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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.solar.SolarCalculator
import app.mobilemobile.solpan.ui.components.AzimuthAwareBubbleLevel
import kotlin.math.abs

@Composable
fun AzimuthVisualizerCard(
  currentOrientation: OrientationData,
  targetParameters: OptimalPanelParameters?,
  debugFakeAlignmentActive: Boolean = false,
) {
  if (targetParameters == null) {
    // Or show a simpler placeholder if preferred
    InfoCard(
      title = stringResource(id = R.string.azimuth_visualizer_card_title),
      icon = Icons.Filled.Explore,
    ) {
      Text(
        text = stringResource(id = R.string.guidance_waiting_for_target),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Companion.Center,
        modifier = Modifier.Companion.fillMaxWidth().padding(8.dp),
      )
    }
    return
  }

  val actualPanelTargetAzimuth =
    targetParameters.targetMagneticAzimuth ?: targetParameters.targetTrueAzimuth
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
  val tiltDifference = targetTilt - currentPitchValueForCalculations
  val tiltThreshold = 3.0
  val rollDifference = targetRoll - currentRollValueForCalculations
  val rollThreshold = 3.0

  val isAzimuthCorrect =
    if (debugFakeAlignmentActive) true else abs(azimuthDifference) <= azimuthThreshold
  val isTiltCorrect = if (debugFakeAlignmentActive) true else abs(tiltDifference) <= tiltThreshold
  val isRollCorrect = if (debugFakeAlignmentActive) true else abs(rollDifference) <= rollThreshold

  InfoCard(
    title = stringResource(id = R.string.azimuth_visualizer_card_title),
    icon = Icons.Filled.Explore,
  ) {
    Text(
      text = stringResource(id = R.string.guidance_level_device_title),
      style = MaterialTheme.typography.titleMedium,
      modifier =
        Modifier.Companion.padding(bottom = 8.dp).align(Alignment.Companion.CenterHorizontally),
    )

    AzimuthAwareBubbleLevel(
      currentPitch = currentPitchValueForCalculations,
      currentRoll = currentRollValueForCalculations,
      targetPitch = targetTilt,
      currentAzimuth = currentAzimuthValueForCalculations,
      targetAzimuth = phoneTargetAzimuth,
      pitchAlignmentThresholdDeg = tiltThreshold,
      rollAlignmentThresholdDeg = rollThreshold,
      azimuthAlignmentThresholdDeg = azimuthThreshold,
      modifier = Modifier.Companion.fillMaxWidth().padding(vertical = 16.dp),
      maxAngleDeviation = 15f,
    )

    val guidanceTextColor =
      if (isTiltCorrect && isRollCorrect && isAzimuthCorrect) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.onSurfaceVariant
      }

    Text(
      text =
        when {
          isTiltCorrect && isRollCorrect && isAzimuthCorrect ->
            stringResource(id = R.string.guidance_perfectly_aligned)
          isTiltCorrect && isRollCorrect ->
            stringResource(id = R.string.guidance_level_adjust_azimuth)
          else -> stringResource(id = R.string.guidance_adjust_bubble_azimuth)
        },
      color = guidanceTextColor,
      textAlign = TextAlign.Companion.Center,
      modifier = Modifier.Companion.fillMaxWidth().padding(bottom = 16.dp),
    )
  }
}
