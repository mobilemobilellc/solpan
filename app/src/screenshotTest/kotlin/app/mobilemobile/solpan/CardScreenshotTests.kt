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
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.ui.screen.components.AzimuthVisualizerCard
import app.mobilemobile.solpan.ui.screen.components.CurrentOrientationCard
import app.mobilemobile.solpan.ui.screen.components.GuidanceCard
import app.mobilemobile.solpan.ui.screen.components.PermissionRequestCard
import app.mobilemobile.solpan.ui.screen.components.TargetParametersCard
import app.mobilemobile.solpan.ui.theme.SolPanTheme
import com.android.tools.screenshot.PreviewTest

private val orientation = OrientationData(azimuth = 178f, pitch = -35f, roll = 1f)
private val alignedParams =
    OptimalPanelParameters(
        targetTrueAzimuth = 180.0,
        targetMagneticAzimuth = 178.0,
        targetTilt = 35.0,
        mode = TiltMode.YEAR_ROUND,
        magneticDeclination = -2.0f,
    )

@PreviewTest
@Preview(name = "AzimuthCard - Aligned", showBackground = true)
@Composable
private fun AzimuthCardAligned() {
    SolPanTheme {
        AzimuthVisualizerCard(
            currentOrientation = orientation,
            targetParameters = alignedParams,
            debugFakeAlignmentActive = true,
        )
    }
}

@PreviewTest
@Preview(name = "AzimuthCard - Misaligned", showBackground = true)
@Composable
private fun AzimuthCardMisaligned() {
    SolPanTheme {
        AzimuthVisualizerCard(
            currentOrientation = OrientationData(azimuth = 45f, pitch = -10f, roll = 8f),
            targetParameters = alignedParams,
            debugFakeAlignmentActive = false,
        )
    }
}

@PreviewTest
@Preview(name = "AzimuthCard - No Target", showBackground = true)
@Composable
private fun AzimuthCardNoTarget() {
    SolPanTheme {
        AzimuthVisualizerCard(
            currentOrientation = orientation,
            targetParameters = null,
        )
    }
}

@PreviewTest
@Preview(name = "GuidanceCard - Aligned", showBackground = true)
@Composable
private fun GuidanceCardAligned() {
    SolPanTheme {
        GuidanceCard(
            currentOrientation = orientation,
            targetParameters = alignedParams,
            debugFakeAlignmentActive = true,
        )
    }
}

@PreviewTest
@Preview(name = "GuidanceCard - Misaligned", showBackground = true)
@Composable
private fun GuidanceCardMisaligned() {
    SolPanTheme {
        GuidanceCard(
            currentOrientation = OrientationData(azimuth = 90f, pitch = -10f, roll = 5f),
            targetParameters = alignedParams,
            debugFakeAlignmentActive = false,
        )
    }
}

@PreviewTest
@Preview(name = "GuidanceCard - No Target", showBackground = true)
@Composable
private fun GuidanceCardNoTarget() {
    SolPanTheme {
        GuidanceCard(
            currentOrientation = orientation,
            targetParameters = null,
        )
    }
}

@PreviewTest
@Preview(name = "TargetCard - With Params", showBackground = true)
@Composable
private fun TargetCardWithParams() {
    SolPanTheme {
        TargetParametersCard(
            optimalParams = alignedParams,
            currentLocation = LocationData(36.1627, -86.7816),
            hasLocationPermission = true,
        )
    }
}

@PreviewTest
@Preview(name = "TargetCard - No Permission", showBackground = true)
@Composable
private fun TargetCardNoPermission() {
    SolPanTheme {
        TargetParametersCard(
            optimalParams = null,
            currentLocation = null,
            hasLocationPermission = false,
        )
    }
}

@PreviewTest
@Preview(
    name = "TargetCard - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TargetCardDark() {
    SolPanTheme {
        TargetParametersCard(
            optimalParams = alignedParams,
            currentLocation = LocationData(36.1627, -86.7816),
            hasLocationPermission = true,
        )
    }
}

@PreviewTest
@Preview(name = "CurrentOrientationCard", showBackground = true)
@Composable
private fun CurrentOrientationCardPreview() {
    SolPanTheme { CurrentOrientationCard(orientation) }
}

@PreviewTest
@Preview(name = "PermissionCard - Initial", showBackground = true)
@Composable
private fun PermissionCardInitial() {
    SolPanTheme {
        PermissionRequestCard(
            shouldShowRationale = false,
            onRequestPermission = {},
        )
    }
}

@PreviewTest
@Preview(name = "PermissionCard - Rationale", showBackground = true)
@Composable
private fun PermissionCardRationale() {
    SolPanTheme {
        PermissionRequestCard(
            shouldShowRationale = true,
            onRequestPermission = {},
        )
    }
}
