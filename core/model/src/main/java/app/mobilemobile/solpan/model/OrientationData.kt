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
