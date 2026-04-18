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
package app.mobilemobile.solpan.ui.screen

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import app.mobilemobile.solpan.BuildConfig
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.SolPanViewModel
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.location.rememberDeviceLocationController
import app.mobilemobile.solpan.orientation.rememberDeviceOrientationController
import app.mobilemobile.solpan.ui.components.HelpGuidanceDialog
import app.mobilemobile.solpan.ui.screen.components.AzimuthVisualizerCard
import app.mobilemobile.solpan.ui.screen.components.CurrentOrientationCard
import app.mobilemobile.solpan.ui.screen.components.GuidanceCard
import app.mobilemobile.solpan.ui.screen.components.PermissionRequestCard
import app.mobilemobile.solpan.ui.screen.components.TargetParametersCard
import app.mobilemobile.solpan.ui.theme.SolPanTheme
import app.mobilemobile.solpan.util.displayName
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun SolPanScreen(
    viewModel: SolPanViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAboutLibraries: () -> Unit = {},
) {
    val optimalParams by viewModel.optimalPanelParameters.collectAsStateWithLifecycle()
    val currentSelectedMode by viewModel.selectedTiltModeFlow.collectAsStateWithLifecycle()
    val vmLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val debugFakeAlignmentActive by viewModel.debugFakeAlignmentActive.collectAsStateWithLifecycle()
    val showHelpDialog by viewModel.showTutorial.collectAsStateWithLifecycle()

    val deviceOrientationController = rememberDeviceOrientationController()
    val currentOrientation by deviceOrientationController.orientation

    if (showHelpDialog) {
        HelpGuidanceDialog {
            viewModel.dismissTutorial()
            viewModel.onTutorialEnded()
        }
    }

    DisposableEffect(Unit) {
        deviceOrientationController.startListening()
        onDispose { deviceOrientationController.stopListening() }
    }

    val locationPermissionsState =
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )

    val deviceLocationController =
        rememberDeviceLocationController(onLocationUpdate = viewModel::updateLocation)

    var hasRequestedPermissions by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (hasRequestedPermissions) {
            viewModel.onPermissionResult(locationPermissionsState.allPermissionsGranted)
        }
        if (locationPermissionsState.allPermissionsGranted) {
            deviceLocationController.startLocationUpdates()
        } else {
            deviceLocationController.stopLocationUpdates()
            viewModel.updateLocation(null)
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var showOverflowMenu by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        stringResource(
                            R.string.solar_app_screen_title_with_mode,
                            stringResource(R.string.solar_app_screen_title),
                            currentSelectedMode.displayName(),
                        ),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.requestTutorial()
                            viewModel.onTutorialStarted()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = stringResource(id = R.string.help_dialog_title),
                        )
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(id = R.string.menu_more_options),
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                        ) {
                            if (BuildConfig.DEBUG) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.debug_toggle_fake_alignment)) },
                                    onClick = {
                                        viewModel.toggleDebugFakeAlignment()
                                        showOverflowMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.BugReport,
                                            contentDescription =
                                                stringResource(id = R.string.debug_toggle_fake_alignment),
                                            tint =
                                                if (debugFakeAlignmentActive) {
                                                    MaterialTheme.colorScheme.error
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                        )
                                    },
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.about_libraries)) },
                                onClick = {
                                    onNavigateToAboutLibraries()
                                    showOverflowMenu = false
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { contentPadding ->
        SolPanScreenContent(
            contentPadding = contentPadding,
            locationPermissionsState = locationPermissionsState,
            onPermissionRequest = { hasRequestedPermissions = true },
            currentOrientation = currentOrientation,
            optimalParams = optimalParams,
            debugFakeAlignmentActive = debugFakeAlignmentActive,
            vmLocation = vmLocation,
        )
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun SolPanScreenContent(
    contentPadding: PaddingValues,
    locationPermissionsState: MultiplePermissionsState?,
    currentOrientation: OrientationData,
    optimalParams: OptimalPanelParameters?,
    debugFakeAlignmentActive: Boolean,
    vmLocation: LocationData?,
    modifier: Modifier = Modifier,
    onPermissionRequest: () -> Unit = {},
) {
    val hasLocationPermission = locationPermissionsState?.allPermissionsGranted != false

    val windowSizeClass = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true).windowSizeClass
    val columns =
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                StaggeredGridCells.Fixed(3)
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                StaggeredGridCells.Fixed(2)
            }

            else -> {
                StaggeredGridCells.Fixed(1)
            }
        }

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize().padding(contentPadding),
        columns = columns,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = spacedBy(8.dp),
    ) {
        if (locationPermissionsState != null && !locationPermissionsState.allPermissionsGranted) {
            item(key = "permission") {
                PermissionRequestCard(
                    locationPermissionsState,
                    Modifier.animateItem(),
                    onPermissionRequest = onPermissionRequest,
                )
            }
        }

        item(key = "azimuth") {
            AzimuthVisualizerCard(
                currentOrientation = currentOrientation,
                targetParameters = optimalParams,
                debugFakeAlignmentActive = debugFakeAlignmentActive,
                modifier = Modifier.animateItem(),
            )
        }

        item(key = "guidance") {
            GuidanceCard(
                currentOrientation = currentOrientation,
                targetParameters = optimalParams,
                debugFakeAlignmentActive = debugFakeAlignmentActive,
                modifier = Modifier.animateItem(),
            )
        }

        item(key = "orientation") { CurrentOrientationCard(currentOrientation, Modifier.animateItem()) }

        item(key = "target") {
            TargetParametersCard(
                optimalParams,
                vmLocation,
                hasLocationPermission,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun SolarAppScreenDarkPreview() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = OrientationData(azimuth = 178f, pitch = -13f, roll = 1f),
            optimalParams =
                OptimalPanelParameters(
                    targetTrueAzimuth = 180.0,
                    targetMagneticAzimuth = 178.0,
                    targetTilt = 35.0,
                    mode = TiltMode.YEAR_ROUND,
                    magneticDeclination = -2.0f,
                ),
            debugFakeAlignmentActive = false,
            vmLocation = LocationData(latitude = 36.1627, longitude = -86.7816),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Mode")
@Composable
private fun SolarAppScreenLightPreview() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            currentOrientation = OrientationData(azimuth = 150f, pitch = -25f, roll = 10f),
            optimalParams =
                OptimalPanelParameters(
                    targetTrueAzimuth = 180.0,
                    targetMagneticAzimuth = 178.0,
                    targetTilt = 35.0,
                    mode = TiltMode.SUMMER,
                    magneticDeclination = -2.0f,
                ),
            debugFakeAlignmentActive = false,
            vmLocation = LocationData(latitude = 36.1627, longitude = -86.7816),
        )
    }
}
