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

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.ui.theme.SolPanTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestCard(
    locationPermissionsState: MultiplePermissionsState,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Filled.EditLocation,
                contentDescription = stringResource(R.string.permission_card_icon_desc),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text =
                    if (locationPermissionsState.shouldShowRationale) {
                        stringResource(R.string.permission_card_rationale_text)
                    } else {
                        stringResource(R.string.permission_card_essential_text)
                    },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onError),
            ) {
                Text(
                    stringResource(R.string.permission_card_button_text),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
private fun PermissionRequestCardPreview() {
    SolPanTheme {
        val dummyPermissionsState =
            rememberMultiplePermissionsState(
                permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
            )
        PermissionRequestCard(locationPermissionsState = dummyPermissionsState)
    }
}
