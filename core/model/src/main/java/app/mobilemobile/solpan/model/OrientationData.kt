/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.model

import androidx.compose.runtime.Immutable

/**
 * Device orientation data from accelerometer and magnetometer sensors.
 *
 * @property azimuth Compass heading in degrees (0-360), where 0° = North
 * @property pitch Tilt forward/backward in degrees (-90 to +90), positive = forward
 * @property roll Tilt left/right in degrees (-180 to +180), positive = clockwise
 * @property sensorAccuracy Sensor accuracy level (optional, Android SensorManager constant)
 */
@Immutable
public data class OrientationData(
    val azimuth: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val sensorAccuracy: Int? = null,
)
