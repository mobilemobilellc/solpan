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

import android.hardware.SensorManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.util.format

@Composable
fun CurrentOrientationCard(orientation: OrientationData) {
    InfoCard(
        title = stringResource(id = R.string.current_orientation_card_title),
        icon = Icons.Filled.Explore,
    ) {
        InfoRow(
            label = stringResource(id = R.string.current_orientation_azimuth_label),
            value = "${orientation.azimuth.format(1)}°",
        )
        InfoRow(
            label = stringResource(id = R.string.current_orientation_pitch_label),
            value = "${orientation.pitch.format(1)}°",
        )
        InfoRow(
            label = stringResource(id = R.string.current_orientation_roll_label),
            value = "${orientation.roll.format(1)}°",
        )
        orientation.sensorAccuracy?.let {
            InfoRow(
                label = stringResource(id = R.string.current_orientation_accuracy_label),
                value = sensorAccuracyToString(accuracy = it),
            )
        }
    }
}

@Composable
private fun sensorAccuracyToString(accuracy: Int): String =
    when (accuracy) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
            stringResource(id = R.string.sensor_accuracy_high)
        }

        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
            stringResource(id = R.string.sensor_accuracy_medium)
        }

        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
            stringResource(id = R.string.sensor_accuracy_low)
        }

        SensorManager.SENSOR_STATUS_UNRELIABLE -> {
            stringResource(id = R.string.sensor_accuracy_unreliable)
        }

        else -> {
            stringResource(id = R.string.sensor_accuracy_unknown)
        }
    }
