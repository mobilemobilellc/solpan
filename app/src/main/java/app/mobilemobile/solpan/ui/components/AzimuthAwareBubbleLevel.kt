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
package app.mobilemobile.solpan.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.mobilemobile.solpan.solar.SolarCalculator
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PARTICLE_CHARACTERS = listOf("☀️", "🌞", "⚡️", "😎️", "🌟")

private data class SunParticle(
  val id: Long = System.nanoTime(),
  val character: String,
  val initialAngle: Float,
  val size: TextUnit,
  val particleAnimationDurationMillis: Long,
  val animatableProgress: Animatable<Float, AnimationVector1D> = Animatable(0f),
  val animatableAlpha: Animatable<Float, AnimationVector1D> = Animatable(1f),
)

@Composable
private fun ShootingSunsEffect(
  modifier: Modifier = Modifier,
  shootDistance: Float,
  particleCountPerWave: Int = 2,
  waveDelayMillis: Long = 150,
  minSizeSp: Int = 15,
  maxSizeSp: Int = 28,
  minDurationMillis: Long = 300,
  maxDurationMillis: Long = 700,
) {
  var particles by remember { mutableStateOf<List<SunParticle>>(emptyList()) }
  val density = LocalDensity.current

  LaunchedEffect(key1 = Unit) {
    while (true) {
      val newWave =
        List(particleCountPerWave) {
          SunParticle(
            character = PARTICLE_CHARACTERS.random(),
            initialAngle = (Math.random() * 360).toFloat(),
            size = ((Math.random() * (maxSizeSp - minSizeSp)) + minSizeSp).toInt().sp,
            particleAnimationDurationMillis =
              ((Math.random() * (maxDurationMillis - minDurationMillis)) + minDurationMillis)
                .toLong(),
          )
        }
      particles = (particles + newWave).takeLast(particleCountPerWave * 10)

      newWave.forEach { particle ->
        launch {
          particle.animatableProgress.snapTo(0f)
          particle.animatableProgress.animateTo(
            targetValue = 1f,
            animationSpec =
              tween(
                durationMillis = particle.particleAnimationDurationMillis.toInt(),
                easing = LinearEasing,
              ),
          )
        }
        launch {
          particle.animatableAlpha.snapTo(1f)
          delay(particle.particleAnimationDurationMillis / 3)
          particle.animatableAlpha.animateTo(
            targetValue = 0f,
            animationSpec =
              tween(
                durationMillis = (particle.particleAnimationDurationMillis * 0.7).toInt(),
                easing = LinearEasing,
              ),
          )
        }
      }
      delay(waveDelayMillis)
    }
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    val activeParticles =
      particles.filter { it.animatableAlpha.value > 0.01f && it.animatableProgress.value < 1f }

    activeParticles.forEach { particle ->
      val progress = particle.animatableProgress.value
      val alpha = particle.animatableAlpha.value
      val currentDistancePx = progress * shootDistance

      if (alpha > 0.01f) {
        Text(
          text = particle.character,
          fontSize = particle.size,
          color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
          modifier =
            Modifier.offset(
              x =
                with(density) {
                  (cos(Math.toRadians(particle.initialAngle.toDouble())) * currentDistancePx)
                    .toFloat()
                    .toDp()
                },
              y =
                with(density) {
                  (sin(Math.toRadians(particle.initialAngle.toDouble())) * currentDistancePx)
                    .toFloat()
                    .toDp()
                },
            ),
        )
      }
    }
  }
}

@Composable
fun AzimuthAwareBubbleLevel(
  currentPitch: Double,
  currentRoll: Double,
  targetPitch: Double,
  currentAzimuth: Double,
  targetAzimuth: Double,
  modifier: Modifier = Modifier,
  bubbleColor: Color = MaterialTheme.colorScheme.primary,
  targetPitchRollColor: Color = MaterialTheme.colorScheme.secondary,
  inTargetPitchRollBubbleColor: Color = MaterialTheme.colorScheme.tertiary,
  housingColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
  azimuthRingColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
  currentAzimuthIndicatorColor: Color = MaterialTheme.colorScheme.primary,
  targetAzimuthIndicatorColor: Color = MaterialTheme.colorScheme.error,
  maxAngleDeviation: Float = 20f,
  pitchAlignmentThresholdDeg: Double = 3.0,
  rollAlignmentThresholdDeg: Double = 3.0,
  azimuthAlignmentThresholdDeg: Double = 5.0,
  bubbleRadiusDp: Dp = 12.dp,
  housingStrokeWidthDp: Dp = 2.dp,
  azimuthRingWidthDp: Dp = 15.dp,
  azimuthIndicatorSizeDp: Dp = 10.dp,
  visualTargetRadiusDp: Dp = 3.dp,
) {
  BoxWithConstraints(
    modifier = modifier.heightIn(max = 240.dp).aspectRatio(1f, matchHeightConstraintsFirst = true)
  ) {
    val diameter = min(constraints.maxWidth, constraints.maxHeight)
    val fullRadiusPx = diameter / 2f
    val center = Offset(fullRadiusPx, fullRadiusPx)

    val bubbleRadiusPx = with(LocalDensity.current) { bubbleRadiusDp.toPx() }
    val housingStrokeWidthPx = with(LocalDensity.current) { housingStrokeWidthDp.toPx() }
    val azimuthRingWidthPx = with(LocalDensity.current) { azimuthRingWidthDp.toPx() }
    val azimuthIndicatorSizePx = with(LocalDensity.current) { azimuthIndicatorSizeDp.toPx() }
    val visualTargetRadiusPx = with(LocalDensity.current) { visualTargetRadiusDp.toPx() }
    val cardinalTextSizePx = with(LocalDensity.current) { 12.sp.toPx() }

    val pitchRollHousingRadiusPx = fullRadiusPx - azimuthRingWidthPx - housingStrokeWidthPx

    val pitchDeviation = currentPitch - targetPitch
    val rollDeviation = currentRoll

    val normalizedPitchDeviation = (pitchDeviation.toFloat() / maxAngleDeviation).coerceIn(-1f, 1f)
    val normalizedRollDeviation = (rollDeviation.toFloat() / maxAngleDeviation).coerceIn(-1f, 1f)

    val bubbleOffsetX =
      normalizedRollDeviation *
        (pitchRollHousingRadiusPx - bubbleRadiusPx - housingStrokeWidthPx / 2)
    val bubbleOffsetY =
      normalizedPitchDeviation *
        (pitchRollHousingRadiusPx - bubbleRadiusPx - housingStrokeWidthPx / 2)
    val bubbleCenter = Offset(center.x + bubbleOffsetX, center.y + bubbleOffsetY)

    val isPitchCorrect = abs(pitchDeviation) <= pitchAlignmentThresholdDeg
    val isRollCorrect = abs(rollDeviation) <= rollAlignmentThresholdDeg
    val isPitchRollInTarget = isPitchCorrect && isRollCorrect

    val isAzimuthInTarget =
      abs(SolarCalculator.calculateAzimuthDifference(currentAzimuth, targetAzimuth)) <=
        azimuthAlignmentThresholdDeg

    val isPerfectlyAligned by
      remember(isPitchRollInTarget, isAzimuthInTarget) {
        derivedStateOf { isPitchRollInTarget && isAzimuthInTarget }
      }

    val currentBubbleColor = if (isPitchRollInTarget) inTargetPitchRollBubbleColor else bubbleColor

    val textPaint = remember {
      Paint().apply {
        color = housingColor.copy(alpha = 0.9f).toArgb()
        textSize = cardinalTextSizePx
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
      }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        color = azimuthRingColor,
        radius = fullRadiusPx - azimuthRingWidthPx / 2f,
        center = center,
        style = Stroke(width = azimuthRingWidthPx),
      )

      val cardinalDirections = listOf("N", "E", "S", "W")
      val cardinalAzimuths = listOf(0.0, 90.0, 180.0, 270.0)
      val textRadius = fullRadiusPx - azimuthRingWidthPx / 2f

      cardinalDirections.zip(cardinalAzimuths).forEach { (direction, azimuth) ->
        val drawingAngleRad = Math.toRadians(azimuth - 90.0).toFloat()
        val textX = center.x + textRadius * cos(drawingAngleRad)
        val textY = center.y + textRadius * sin(drawingAngleRad)

        val fontMetrics = textPaint.fontMetrics
        val adjustedTextY = textY - (fontMetrics.ascent + fontMetrics.descent) / 2f

        drawContext.canvas.nativeCanvas.drawText(direction, textX, adjustedTextY, textPaint)
      }

      val targetAzimuthAngleRad = Math.toRadians((targetAzimuth - 90.0)).toFloat()
      val targetIndicatorRadius = fullRadiusPx - azimuthRingWidthPx / 2f
      val targetIndicatorCenter =
        Offset(
          center.x + targetIndicatorRadius * cos(targetAzimuthAngleRad),
          center.y + targetIndicatorRadius * sin(targetAzimuthAngleRad),
        )
      drawCircle(
        color =
          if (isAzimuthInTarget) {
            targetAzimuthIndicatorColor.copy(alpha = 0.5f)
          } else {
            targetAzimuthIndicatorColor
          },
        radius = azimuthIndicatorSizePx / 1.5f,
        center = targetIndicatorCenter,
      )

      val currentAzimuthAngleRad = Math.toRadians((currentAzimuth - 90.0)).toFloat()
      val lineStartRadius = pitchRollHousingRadiusPx + housingStrokeWidthPx
      val lineEndRadius = fullRadiusPx - housingStrokeWidthPx / 2
      drawLine(
        color = currentAzimuthIndicatorColor,
        start =
          Offset(
            center.x + lineStartRadius * cos(currentAzimuthAngleRad),
            center.y + lineStartRadius * sin(currentAzimuthAngleRad),
          ),
        end =
          Offset(
            center.x + lineEndRadius * cos(currentAzimuthAngleRad),
            center.y + lineEndRadius * sin(currentAzimuthAngleRad),
          ),
        strokeWidth = housingStrokeWidthPx * 1.5f,
        cap = StrokeCap.Round,
      )

      drawCircle(
        color = housingColor,
        radius = pitchRollHousingRadiusPx - housingStrokeWidthPx / 2,
        center = center,
        style = Stroke(width = housingStrokeWidthPx),
      )

      drawCircle(
        color =
          if (isPitchRollInTarget) {
            targetPitchRollColor.copy(alpha = 0.5f)
          } else {
            targetPitchRollColor.copy(alpha = 0.2f)
          },
        radius = visualTargetRadiusPx,
        center = center,
        style = Stroke(housingStrokeWidthPx / 2),
      )

      if (!isPerfectlyAligned) {
        drawCircle(color = currentBubbleColor, radius = bubbleRadiusPx, center = bubbleCenter)
      }
    }

    if (isPerfectlyAligned) {
      ShootingSunsEffect(
        modifier = Modifier.fillMaxSize().align(Alignment.Center),
        shootDistance = fullRadiusPx * 0.7f,
        particleCountPerWave = 2,
        waveDelayMillis = 100L,
        minSizeSp = 15,
        maxSizeSp = 28,
        minDurationMillis = 400L,
        maxDurationMillis = 800L,
      )
    }
  }
}

@Preview(showBackground = true, name = "Azimuth Bubble Level - Aligned")
@Composable
fun AzimuthBubbleLevelAlignedPreview() {
  MaterialTheme {
    Box(modifier = Modifier.size(250.dp)) {
      AzimuthAwareBubbleLevel(
        currentPitch = 30.0,
        currentRoll = 0.0,
        targetPitch = 30.0,
        currentAzimuth = 180.0,
        targetAzimuth = 180.0,
        pitchAlignmentThresholdDeg = 0.1,
        rollAlignmentThresholdDeg = 0.1,
        azimuthAlignmentThresholdDeg = 0.1,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

@Preview(showBackground = true, name = "Azimuth Bubble Level - Pitch/Roll Off")
@Composable
fun AzimuthBubbleLevelPitchRollOffPreview() {
  MaterialTheme {
    AzimuthAwareBubbleLevel(
      currentPitch = 35.0,
      currentRoll = 5.0,
      targetPitch = 30.0,
      currentAzimuth = 180.0,
      targetAzimuth = 180.0,
      pitchAlignmentThresholdDeg = 3.0,
      rollAlignmentThresholdDeg = 3.0,
      azimuthAlignmentThresholdDeg = 5.0,
      modifier = Modifier.size(250.dp).padding(16.dp),
    )
  }
}

@Preview(showBackground = true, name = "Azimuth Bubble Level - Azimuth Off")
@Composable
fun AzimuthBubbleLevelAzimuthOffPreview() {
  MaterialTheme {
    AzimuthAwareBubbleLevel(
      currentPitch = 30.0,
      currentRoll = 0.0,
      targetPitch = 30.0,
      currentAzimuth = 190.0,
      targetAzimuth = 180.0,
      pitchAlignmentThresholdDeg = 3.0,
      rollAlignmentThresholdDeg = 3.0,
      azimuthAlignmentThresholdDeg = 5.0,
      modifier = Modifier.size(250.dp).padding(16.dp),
    )
  }
}

@Preview(showBackground = true, name = "Azimuth Bubble Level - All Off")
@Composable
fun AzimuthBubbleLevelAllOffPreview() {
  MaterialTheme {
    AzimuthAwareBubbleLevel(
      currentPitch = 35.0,
      currentRoll = 5.0,
      targetPitch = 30.0,
      currentAzimuth = 190.0,
      targetAzimuth = 180.0,
      pitchAlignmentThresholdDeg = 3.0,
      rollAlignmentThresholdDeg = 3.0,
      azimuthAlignmentThresholdDeg = 5.0,
      modifier = Modifier.size(250.dp).padding(16.dp),
    )
  }
}

@Preview(showBackground = true, name = "Shooting Suns Effect Preview")
@Composable
fun ShootingSunsEffectPreview() {
  MaterialTheme {
    Box(modifier = Modifier.size(250.dp).padding(16.dp), contentAlignment = Alignment.Center) {
      ShootingSunsEffect(modifier = Modifier.fillMaxSize(), shootDistance = 100f)
    }
  }
}
