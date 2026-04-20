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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.mobilemobile.solpan.designsystem.theme.SolPanTheme
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.TiltMode
import app.mobilemobile.solpan.ui.screen.components.TargetParametersCard
import com.android.tools.screenshot.PreviewTest

class CardScreenshotTests {
    @PreviewTest
    @Preview(name = "Target Parameters Card", showBackground = true)
    @Composable
    fun TargetParametersCardPreview() {
        SolPanTheme {
            TargetParametersCard(
                params =
                    OptimalPanelParameters(
                        targetTrueAzimuth = 180.0,
                        targetMagneticAzimuth = 178.0,
                        targetTilt = 35.0,
                        mode = TiltMode.YEAR_ROUND,
                        magneticDeclination = -2.0f,
                    ),
                location = LocationData(latitude = 36.1627, longitude = -86.7816),
            )
        }
    }
}
