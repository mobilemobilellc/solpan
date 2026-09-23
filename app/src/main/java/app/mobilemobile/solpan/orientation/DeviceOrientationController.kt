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
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.util.roundTo
import kotlin.math.PI

@Composable
fun rememberDeviceOrientationController(): DeviceOrientationController {
    val context = LocalContext.current
    val controller = remember { DeviceOrientationController(context) }

    LifecycleStartEffect(controller) {
        controller.startListening()
        onStopOrDispose { controller.stopListening() }
    }
    return controller
}

class DeviceOrientationController(
    context: Context,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)

    private val display = ContextCompat.getDisplayOrDefault(context)

    private val rotationMatrix = FloatArray(9)
    private val screenRotationMatrix = FloatArray(9)
    private val orientationAnglesOutput = FloatArray(3)

    private val _orientation = mutableStateOf(OrientationData())
    val orientation: State<OrientationData> = _orientation

    private var sensorsAvailable = true

    init {
        if (rotationVectorSensor == null) {
            Log.e("DeviceOrientationController", "Rotation vector sensor not available.")
            sensorsAvailable = false
            _orientation.value = OrientationData(sensorAccuracy = null)
        }
    }

    fun startListening() {
        if (!sensorsAvailable) {
            Log.w(
                "DeviceOrientationController",
                "Cannot start listening, rotation vector sensor missing.",
            )
            _orientation.value = OrientationData(sensorAccuracy = _orientation.value.sensorAccuracy)
            return
        }

        _orientation.value =
            _orientation.value.copy(azimuth = 0f, pitch = 0f, roll = 0f, sensorAccuracy = null)

        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !sensorsAvailable) return

        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        // Sensor axes are fixed to the device; the guidance is drawn relative to the screen.
        val (axisX, axisY) =
            when (display.rotation) {
                Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
            }
        SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, screenRotationMatrix)
        SensorManager.getOrientation(screenRotationMatrix, orientationAnglesOutput)

        orientationFromRadians(orientationAnglesOutput, _orientation.value.sensorAccuracy)?.let {
            _orientation.value = it
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
        /** SensorManager reports orientation in radians. */
        private const val DEGREES_PER_HALF_TURN = 180.0
        private const val FULL_CIRCLE_DEGREES = 360f

        private const val ORIENTATION_INDEX_AZIMUTH = 0
        private const val ORIENTATION_INDEX_PITCH = 1
        private const val ORIENTATION_INDEX_ROLL = 2

        internal fun orientationFromRadians(
            angles: FloatArray,
            sensorAccuracy: Int?,
        ): OrientationData? {
            // getOrientation yields NaN when asin gets a matrix element just past ±1.
            if (!angles.all { it.isFinite() }) return null

            var azimuthInDegrees =
                (angles[ORIENTATION_INDEX_AZIMUTH].toDouble() * (DEGREES_PER_HALF_TURN / PI)).toFloat()
            if (azimuthInDegrees < 0) {
                azimuthInDegrees += FULL_CIRCLE_DEGREES
            }

            val pitchInDegrees =
                (angles[ORIENTATION_INDEX_PITCH].toDouble() * (DEGREES_PER_HALF_TURN / PI)).toFloat()
            val rollInDegrees =
                (angles[ORIENTATION_INDEX_ROLL].toDouble() * (DEGREES_PER_HALF_TURN / PI)).toFloat()

            return OrientationData(
                azimuth = azimuthInDegrees.roundTo(2),
                pitch = pitchInDegrees.roundTo(2),
                roll = rollInDegrees.roundTo(2),
                sensorAccuracy = sensorAccuracy,
            )
        }
    }
}
