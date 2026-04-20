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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.designsystem.components.InfoCard
import app.mobilemobile.solpan.designsystem.components.InfoRow
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.util.format

@Composable
fun TargetParametersCard(
    params: OptimalPanelParameters?,
    location: LocationData?,
    modifier: Modifier = Modifier,
) {
    InfoCard(
        title = stringResource(id = R.string.target_param_card_title),
        icon = Icons.Filled.Info,
        modifier = modifier,
    ) {
        if (params == null) {
            InfoRow(
                label = stringResource(id = R.string.calculating_text),
                value = stringResource(id = R.string.target_param_waiting_location_text),
            )
        } else {
            InfoRow(
                label = stringResource(id = R.string.target_param_true_azimuth_label),
                value =
                    stringResource(
                        id = R.string.target_param_value_degree_unit,
                        params.targetTrueAzimuth,
                    ),
            )
            params.targetMagneticAzimuth?.let {
                InfoRow(
                    label = stringResource(id = R.string.target_param_magnetic_azimuth_label),
                    value = stringResource(id = R.string.target_param_value_degree_unit, it),
                )
            }
            InfoRow(
                label = stringResource(id = R.string.target_param_tilt_label),
                value = stringResource(id = R.string.target_param_value_degree_unit, params.targetTilt),
            )
        }

        location?.let { loc ->
            InfoRow(
                label = stringResource(id = R.string.target_param_latitude_label),
                value = loc.latitude.format(4),
            )
            InfoRow(
                label = stringResource(id = R.string.target_param_longitude_label),
                value = loc.longitude.format(4),
            )
            loc.accuracy?.let { acc ->
                InfoRow(
                    label = stringResource(id = R.string.target_param_accuracy_label),
                    value = stringResource(id = R.string.target_param_value_meter_unit, acc),
                )
            }
        }
    }
}
