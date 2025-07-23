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
package app.mobilemobile.solpan.orientation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.util.roundTo

@Composable
fun rememberDeviceOrientationController(): DeviceOrientationController {
    val context = LocalContext.current
    val controller = remember { DeviceOrientationController(context) }

    DisposableEffect(controller) { onDispose { controller.stopListening() } }
    return controller
}

class DeviceOrientationController(
    context: Context,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val rawAccelerometerReading = FloatArray(3)
    private val rawMagnetometerReading = FloatArray(3)

    private var filteredAccelerometerReading: FloatArray? = null
    private var filteredMagnetometerReading: FloatArray? = null

    private val rotationMatrix = FloatArray(9)
    private val orientationAnglesOutput = FloatArray(3)

    private val _orientation = mutableStateOf(OrientationData())
    val orientation: State<OrientationData> = _orientation

    private var sensorsAvailable = true

    init {
        if (accelerometer == null) {
            Log.e("DeviceOrientationController", "Accelerometer not available.")
            sensorsAvailable = false
        }
        if (magnetometer == null) {
            Log.e("DeviceOrientationController", "Magnetometer not available.")
            sensorsAvailable = false
        }
        if (!sensorsAvailable) {
            _orientation.value = OrientationData(sensorAccuracy = null)
        }
    }

    fun startListening() {
        if (!sensorsAvailable) {
            Log.w(
                "DeviceOrientationController",
                "Cannot start listening, essential sensors missing.",
            )
            _orientation.value = OrientationData(sensorAccuracy = _orientation.value.sensorAccuracy)
            return
        }

        filteredAccelerometerReading = null
        filteredMagnetometerReading = null
        _orientation.value =
            _orientation.value.copy(azimuth = 0f, pitch = 0f, roll = 0f, sensorAccuracy = null)

        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !sensorsAvailable) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values,
                    0,
                    rawAccelerometerReading,
                    0,
                    rawAccelerometerReading.size,
                )
                filteredAccelerometerReading =
                    applyLowPassFilter(rawAccelerometerReading, filteredAccelerometerReading)
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(
                    event.values,
                    0,
                    rawMagnetometerReading,
                    0,
                    rawMagnetometerReading.size,
                )
                filteredMagnetometerReading =
                    applyLowPassFilter(rawMagnetometerReading, filteredMagnetometerReading)
            }
        }
        updateOrientationAngles()
    }

    private fun applyLowPassFilter(
        inputValues: FloatArray,
        previousFilteredValues: FloatArray?,
    ): FloatArray {
        if (previousFilteredValues == null) {
            return inputValues.clone()
        }
        val newFilteredValues = FloatArray(inputValues.size)
        for (i in inputValues.indices) {
            newFilteredValues[i] =
                FILTER_ALPHA * inputValues[i] + (1 - FILTER_ALPHA) * previousFilteredValues[i]
        }
        return newFilteredValues
    }

    private fun updateOrientationAngles() {
        if (!sensorsAvailable) {
            _orientation.value = OrientationData(sensorAccuracy = _orientation.value.sensorAccuracy)
            return
        }

        val accelReading = filteredAccelerometerReading
        val magReading = filteredMagnetometerReading

        if (accelReading != null && magReading != null) {
            val success =
                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelReading,
                    magReading,
                )
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAnglesOutput)

                var azimuthInDegrees =
                    Math
                        .toDegrees(
                            orientationAnglesOutput[0].toDouble(),
                        ).toFloat()
                if (azimuthInDegrees < 0) {
                    azimuthInDegrees += 360f
                }

                val pitchInDegrees = Math.toDegrees(orientationAnglesOutput[1].toDouble()).toFloat()
                val rollInDegrees = Math.toDegrees(orientationAnglesOutput[2].toDouble()).toFloat()

                _orientation.value =
                    OrientationData(
                        azimuth = azimuthInDegrees.roundTo(2),
                        pitch = pitchInDegrees.roundTo(2),
                        roll = rollInDegrees.roundTo(2),
                        sensorAccuracy = _orientation.value.sensorAccuracy,
                    )
            } else {
                Log.w(
                    "DeviceOrientationController",
                    "Failed to get rotation matrix. Device may be in freefall or near magnetic pole.",
                )
                _orientation.value = OrientationData(sensorAccuracy = _orientation.value.sensorAccuracy)
            }
        } else {
            _orientation.value = OrientationData(sensorAccuracy = _orientation.value.sensorAccuracy)
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
        _orientation.value = _orientation.value.copy(sensorAccuracy = accuracy)

        val accuracyDescription =
            when (accuracy) {
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "LOW"
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "MEDIUM"
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "HIGH"
                SensorManager.SENSOR_STATUS_UNRELIABLE -> "UNRELIABLE"
                else -> "UNKNOWN ($accuracy)"
            }
        Log.i(
            "DeviceOrientationController",
            "Accuracy for ${sensor?.name ?: "Unknown Sensor"} changed to: $accuracyDescription",
        )
    }

    companion object {
        private const val FILTER_ALPHA = 0.08f
    }
}
