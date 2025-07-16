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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.util.displayName
import app.mobilemobile.solpan.util.format

@Composable
fun TargetParametersCard(
  optimalParams: OptimalPanelParameters?,
  currentLocation: LocationData?,
  hasLocationPermission: Boolean,
) {
  InfoCard(
    title = stringResource(id = R.string.target_param_card_title),
    icon = Icons.Filled.WbSunny,
  ) {
    if (optimalParams != null) {
      InfoRow(
        label = stringResource(id = R.string.target_param_mode_label),
        value =
          optimalParams.mode.displayName(), // TiltMode.displayName could also use string resources
      )
      InfoRow(
        label = stringResource(id = R.string.target_param_true_azimuth_label),
        value =
          stringResource(
            id = R.string.target_param_value_degree_unit,
            optimalParams.targetTrueAzimuth,
          ),
      )
      optimalParams.targetMagneticAzimuth?.let { magAzimuth ->
        InfoRow(
          label = stringResource(id = R.string.target_param_magnetic_azimuth_label),
          value = stringResource(id = R.string.target_param_value_degree_aim_unit, magAzimuth),
        )
      }
        ?: InfoRow(
          label = stringResource(id = R.string.target_param_magnetic_azimuth_calculating_label),
          value = stringResource(id = R.string.calculating_text),
        )
      InfoRow(
        label = stringResource(id = R.string.target_param_tilt_label),
        value =
          stringResource(
            id = R.string.target_param_value_degree_horizontal_unit,
            optimalParams.targetTilt,
          ),
      )
      optimalParams.magneticDeclination?.let {
        InfoRow(
          label = stringResource(id = R.string.target_param_declination_label),
          value = stringResource(id = R.string.target_param_value_degree_unit, it),
          labelStyle = MaterialTheme.typography.bodySmall,
          valueStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
        )
      }
    } else if (hasLocationPermission) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Text(
          text = stringResource(id = R.string.target_param_waiting_location_text),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    } else {
      Text(
        text = stringResource(id = R.string.target_param_grant_permission_text),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center,
      )
    }

    currentLocation?.let {
      HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 0.dp))
      Text(
        text = stringResource(id = R.string.target_param_current_location_label),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(bottom = 6.dp),
      )
      InfoRow(
        label = stringResource(id = R.string.target_param_latitude_label),
        value = it.latitude.format(4), // formatting for lat/lon is specific
      )
      InfoRow(
        label = stringResource(id = R.string.target_param_longitude_label),
        value = it.longitude.format(4), // formatting for lat/lon is specific
      )
      it.accuracy?.let { acc ->
        InfoRow(
          label = stringResource(id = R.string.target_param_accuracy_label),
          value = stringResource(id = R.string.target_param_value_meter_unit, acc),
        )
      }
    }
  }
}
