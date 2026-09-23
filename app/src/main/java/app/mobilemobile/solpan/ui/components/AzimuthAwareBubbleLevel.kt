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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.solar.SolarCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** Keep this many waves of particles alive before the oldest are dropped. */
private const val PARTICLE_WAVES_RETAINED = 10

/** Stagger within a wave, as a fraction of each particle's own duration. */
private const val PARTICLE_STAGGER_DIVISOR = 3

/** Below this a particle is invisible, so stop drawing and retaining it. */
private const val MIN_VISIBLE_ALPHA = 0.01f

/** Compass geometry. Azimuth is clockwise from north; canvas angles start at three o'clock. */
private const val QUARTER_TURN_DEGREES = 90.0
private const val HALF_TURN_DEGREES = 180.0
private const val THREE_QUARTER_TURN_DEGREES = 270.0

/** The target marker dims rather than disappears once the phone is pointing the right way. */
private const val IN_TARGET_INDICATOR_ALPHA = 0.5f
private const val TARGET_INDICATOR_SIZE_DIVISOR = 1.5f
private const val NEEDLE_STROKE_MULTIPLIER = 1.5f

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
    shootDistance: Float,
    modifier: Modifier = Modifier,
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
                        initialAngle = Random.nextFloat() * 360f,
                        size = ((Random.nextFloat() * (maxSizeSp - minSizeSp)) + minSizeSp).toInt().sp,
                        particleAnimationDurationMillis =
                            (
                                (Random.nextFloat() * (maxDurationMillis - minDurationMillis)) +
                                    minDurationMillis
                            ).toLong(),
                    )
                }
            particles = (particles + newWave).takeLast(particleCountPerWave * PARTICLE_WAVES_RETAINED)

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
                    delay(particle.particleAnimationDurationMillis / PARTICLE_STAGGER_DIVISOR)
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
            particles.filter {
                it.animatableAlpha.value > MIN_VISIBLE_ALPHA && it.animatableProgress.value < 1f
            }

        activeParticles.forEach { particle ->
            val progress = particle.animatableProgress.value
            val alpha = particle.animatableAlpha.value
            val currentDistancePx = progress * shootDistance

            if (alpha > MIN_VISIBLE_ALPHA) {
                Text(
                    text = particle.character,
                    fontSize = particle.size,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                    modifier =
                        Modifier.offset(
                            x =
                                with(density) {
                                    (
                                        cos(Math.toRadians(particle.initialAngle.toDouble())) *
                                            currentDistancePx
                                    ).toFloat()
                                        .toDp()
                                },
                            y =
                                with(density) {
                                    (
                                        sin(Math.toRadians(particle.initialAngle.toDouble())) *
                                            currentDistancePx
                                    ).toFloat()
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
        modifier =
            modifier.heightIn(max = 240.dp).aspectRatio(1f, matchHeightConstraintsFirst = true),
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

        val azimuthDiff = SolarCalculator.calculateAzimuthDifference(currentAzimuth, targetAzimuth)
        val isAzimuthInTarget = abs(azimuthDiff) <= azimuthAlignmentThresholdDeg

        val isPerfectlyAligned by
            remember(isPitchRollInTarget, isAzimuthInTarget) {
                derivedStateOf { isPitchRollInTarget && isAzimuthInTarget }
            }

        val currentBubbleColor = if (isPitchRollInTarget) inTargetPitchRollBubbleColor else bubbleColor

        val alignedDesc = stringResource(R.string.bubble_level_aligned)
        val rotateClockwiseDesc = stringResource(R.string.bubble_level_rotate_clockwise)
        val rotateCounterClockwiseDesc = stringResource(R.string.bubble_level_rotate_counter_clockwise)
        val adjustTiltDesc = stringResource(R.string.bubble_level_adjust_tilt)
        val semanticContentDescription =
            bubbleLevelDescription(
                isPerfectlyAligned = isPerfectlyAligned,
                isAzimuthInTarget = isAzimuthInTarget,
                isPitchRollInTarget = isPitchRollInTarget,
                azimuthDiff = azimuthDiff,
                alignedDesc = alignedDesc,
                rotateClockwiseDesc = rotateClockwiseDesc,
                rotateCounterClockwiseDesc = rotateCounterClockwiseDesc,
                adjustTiltDesc = adjustTiltDesc,
            )

        val textPaint =
            remember {
                Paint().apply {
                    color = housingColor.copy(alpha = 0.9f).toArgb()
                    textSize = cardinalTextSizePx
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    isAntiAlias = true
                }
            }

        Box(
            modifier =
                Modifier.fillMaxSize().semantics {
                    contentDescription = semanticContentDescription
                    liveRegion = LiveRegionMode.Polite
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = azimuthRingColor,
                    radius = fullRadiusPx - azimuthRingWidthPx / 2f,
                    center = center,
                    style = Stroke(width = azimuthRingWidthPx),
                )

                drawCardinalLabels(textPaint, fullRadiusPx, azimuthRingWidthPx)

                drawTargetAzimuthIndicator(
                    targetAzimuth = targetAzimuth,
                    isInTarget = isAzimuthInTarget,
                    color = targetAzimuthIndicatorColor,
                    fullRadiusPx = fullRadiusPx,
                    azimuthRingWidthPx = azimuthRingWidthPx,
                    indicatorSizePx = azimuthIndicatorSizePx,
                )

                drawCurrentAzimuthNeedle(
                    currentAzimuth = currentAzimuth,
                    color = currentAzimuthIndicatorColor,
                    fullRadiusPx = fullRadiusPx,
                    housingRadiusPx = pitchRollHousingRadiusPx,
                    strokeWidthPx = housingStrokeWidthPx,
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
        } // end semantics Box
    }
}

@Preview(showBackground = true, name = "Azimuth Bubble Level - Aligned")
@Composable
private fun AzimuthBubbleLevelAlignedPreview() {
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
private fun AzimuthBubbleLevelPitchRollOffPreview() {
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
private fun AzimuthBubbleLevelAzimuthOffPreview() {
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
private fun AzimuthBubbleLevelAllOffPreview() {
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
private fun ShootingSunsEffectPreview() {
    MaterialTheme {
        Box(modifier = Modifier.size(250.dp).padding(16.dp), contentAlignment = Alignment.Center) {
            ShootingSunsEffect(modifier = Modifier.fillMaxSize(), shootDistance = 100f)
        }
    }
}

/**
 * What TalkBack reads out. Azimuth first because the user has to turn the whole panel before the
 * tilt reading means anything.
 */
@Suppress("LongParameterList")
private fun bubbleLevelDescription(
    isPerfectlyAligned: Boolean,
    isAzimuthInTarget: Boolean,
    isPitchRollInTarget: Boolean,
    azimuthDiff: Double,
    alignedDesc: String,
    rotateClockwiseDesc: String,
    rotateCounterClockwiseDesc: String,
    adjustTiltDesc: String,
): String {
    if (isPerfectlyAligned) return alignedDesc
    return buildString {
        if (!isAzimuthInTarget) {
            append(if (azimuthDiff > 0) rotateCounterClockwiseDesc else rotateClockwiseDesc)
        }
        if (!isPitchRollInTarget) {
            if (isNotEmpty()) append(". ")
            append(adjustTiltDesc)
        }
    }
}

/** N/E/S/W around the outer ring, each centred on its bearing. */
private fun DrawScope.drawCardinalLabels(
    textPaint: Paint,
    fullRadiusPx: Float,
    azimuthRingWidthPx: Float,
) {
    val cardinals =
        listOf("N", "E", "S", "W")
            .zip(listOf(0.0, QUARTER_TURN_DEGREES, HALF_TURN_DEGREES, THREE_QUARTER_TURN_DEGREES))
    val textRadius = fullRadiusPx - azimuthRingWidthPx / 2f
    val fontMetrics = textPaint.fontMetrics

    cardinals.forEach { (direction, azimuth) ->
        val angleRad = Math.toRadians(azimuth - QUARTER_TURN_DEGREES).toFloat()
        val textX = center.x + textRadius * cos(angleRad)
        val textY = center.y + textRadius * sin(angleRad)
        val baseline = textY - (fontMetrics.ascent + fontMetrics.descent) / 2f
        drawContext.canvas.nativeCanvas.drawText(direction, textX, baseline, textPaint)
    }
}

/** Where the panel should point. Fades once the phone is already there. */
private fun DrawScope.drawTargetAzimuthIndicator(
    targetAzimuth: Double,
    isInTarget: Boolean,
    color: Color,
    fullRadiusPx: Float,
    azimuthRingWidthPx: Float,
    indicatorSizePx: Float,
) {
    val angleRad = Math.toRadians(targetAzimuth - QUARTER_TURN_DEGREES).toFloat()
    val radius = fullRadiusPx - azimuthRingWidthPx / 2f
    drawCircle(
        color = if (isInTarget) color.copy(alpha = IN_TARGET_INDICATOR_ALPHA) else color,
        radius = indicatorSizePx / TARGET_INDICATOR_SIZE_DIVISOR,
        center = Offset(center.x + radius * cos(angleRad), center.y + radius * sin(angleRad)),
    )
}

/** Where the phone currently points, drawn from the housing out to the ring. */
private fun DrawScope.drawCurrentAzimuthNeedle(
    currentAzimuth: Double,
    color: Color,
    fullRadiusPx: Float,
    housingRadiusPx: Float,
    strokeWidthPx: Float,
) {
    val angleRad = Math.toRadians(currentAzimuth - QUARTER_TURN_DEGREES).toFloat()
    val startRadius = housingRadiusPx + strokeWidthPx
    val endRadius = fullRadiusPx - strokeWidthPx / 2
    drawLine(
        color = color,
        start =
            Offset(
                center.x + startRadius * cos(angleRad),
                center.y + startRadius * sin(angleRad),
            ),
        end = Offset(center.x + endRadius * cos(angleRad), center.y + endRadius * sin(angleRad)),
        strokeWidth = strokeWidthPx * NEEDLE_STROKE_MULTIPLIER,
        cap = StrokeCap.Round,
    )
}
