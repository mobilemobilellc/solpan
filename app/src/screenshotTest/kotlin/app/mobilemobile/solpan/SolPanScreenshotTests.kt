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
package app.mobilemobile.solpan

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.designsystem.theme.SolPanTheme
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.model.TiltMode
import app.mobilemobile.solpan.optimizer.SolPanUiState
import app.mobilemobile.solpan.ui.screen.SolPanScreenContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlin.OptIn

class SolPanScreenshotTests {
    @Composable
    @OptIn(ExperimentalPermissionsApi::class)
    fun SolPanScreenPreview() {
        SolPanTheme {
            SolPanScreenContent(
                contentPadding = PaddingValues(0.dp),
                locationPermissionsState = null,
                uiState =
                    SolPanUiState(
                        selectedMode = TiltMode.YEAR_ROUND,
                        currentLocation = LocationData(latitude = 36.1627, longitude = -86.7816),
                        currentOrientation = OrientationData(azimuth = 150f, pitch = -25f, roll = 10f),
                        optimalParams =
                            OptimalPanelParameters(
                                targetTrueAzimuth = 180.0,
                                targetMagneticAzimuth = 178.0,
                                targetTilt = 35.0,
                                mode = TiltMode.YEAR_ROUND,
                                magneticDeclination = -2.0f,
                            ),
                        isDebugFakeAlignmentActive = false,
                    ),
            )
        }
    }
}
