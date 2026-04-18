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

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.ui.screen.SolPanScreenContent
import app.mobilemobile.solpan.ui.theme.SolPanTheme
import com.android.tools.screenshot.PreviewTest
import com.google.accompanist.permissions.ExperimentalPermissionsApi

private val sampleOrientation = OrientationData(azimuth = 178f, pitch = -35f, roll = 1f)
private val sampleParams =
    OptimalPanelParameters(
        targetTrueAzimuth = 180.0,
        targetMagneticAzimuth = 178.0,
        targetTilt = 35.0,
        mode = TiltMode.YEAR_ROUND,
        magneticDeclination = -2.0f,
    )
private val sampleLocation = LocationData(latitude = 36.1627, longitude = -86.7816)

@OptIn(ExperimentalPermissionsApi::class)
@PreviewTest
@Preview(name = "Aligned - Light", showBackground = true)
@Composable
private fun SolPanScreenAlignedLight() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = sampleOrientation,
            optimalParams = sampleParams,
            debugFakeAlignmentActive = true,
            vmLocation = sampleLocation,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewTest
@Preview(name = "Aligned - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SolPanScreenAlignedDark() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = sampleOrientation,
            optimalParams = sampleParams,
            debugFakeAlignmentActive = true,
            vmLocation = sampleLocation,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewTest
@Preview(name = "Misaligned - Light", showBackground = true)
@Composable
private fun SolPanScreenMisalignedLight() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = OrientationData(azimuth = 90f, pitch = -15f, roll = 5f),
            optimalParams = sampleParams,
            debugFakeAlignmentActive = false,
            vmLocation = sampleLocation,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@PreviewTest
@Preview(name = "No Location - Light", showBackground = true)
@Composable
private fun SolPanScreenNoLocationLight() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = OrientationData(),
            optimalParams = null,
            debugFakeAlignmentActive = false,
            vmLocation = null,
        )
    }
}
