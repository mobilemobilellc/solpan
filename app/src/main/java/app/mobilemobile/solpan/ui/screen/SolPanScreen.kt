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
import app.mobilemobile.solpan.designsystem.theme.SolPanTheme
import app.mobilemobile.solpan.location.rememberDeviceLocationController
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.OptimalPanelParameters
import app.mobilemobile.solpan.model.OrientationData
import app.mobilemobile.solpan.model.TiltMode
import app.mobilemobile.solpan.optimizer.SolPanUiState
import app.mobilemobile.solpan.optimizer.SolPanViewModel
import app.mobilemobile.solpan.orientation.rememberDeviceOrientationController
import app.mobilemobile.solpan.ui.components.HelpGuidanceDialog
import app.mobilemobile.solpan.ui.screen.components.AzimuthVisualizerCard
import app.mobilemobile.solpan.ui.screen.components.CurrentOrientationCard
import app.mobilemobile.solpan.ui.screen.components.GuidanceCard
import app.mobilemobile.solpan.ui.screen.components.PermissionRequestCard
import app.mobilemobile.solpan.ui.screen.components.TargetParametersCard
import app.mobilemobile.solpan.util.displayName
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SolPanScreen(
    viewModel: SolPanViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAboutLibraries: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val deviceOrientationController = rememberDeviceOrientationController()
    val currentOrientation by deviceOrientationController.orientation

    LaunchedEffect(currentOrientation) { viewModel.updateOrientation(currentOrientation) }

    if (uiState.showTutorial) {
        HelpGuidanceDialog {
            viewModel.dismissTutorial()
            viewModel.onTutorialEnded()
        }
    }

    DisposableEffect(Unit) {
        viewModel.onTutorialStarted()
        onDispose {}
    }

    val locationPermissionsState =
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
        )

    val deviceLocationController =
        rememberDeviceLocationController { newLocation ->
            viewModel.updateLocation(newLocation)
        }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            deviceLocationController.startLocationUpdates()
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SolPanTopBar(
                selectedMode = uiState.selectedMode,
                onShowTutorial = { viewModel.requestTutorial() },
                onToggleDebug = { viewModel.toggleDebugFakeAlignment() },
                onNavigateToAboutLibraries = onNavigateToAboutLibraries,
            )
        },
    ) { contentPadding ->
        SolPanScreenContent(
            contentPadding = contentPadding,
            locationPermissionsState = locationPermissionsState,
            uiState = uiState,
            onPermissionRequest = { locationPermissionsState.launchMultiplePermissionRequest() },
        )
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun SolPanScreenContent(
    contentPadding: PaddingValues,
    locationPermissionsState: MultiplePermissionsState?,
    uiState: SolPanUiState,
    modifier: Modifier = Modifier,
    onPermissionRequest: () -> Unit = {},
) {
    val hasLocationPermission = locationPermissionsState?.allPermissionsGranted != false

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
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
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        if (!hasLocationPermission) {
            item {
                PermissionRequestCard(
                    shouldShowRationale = locationPermissionsState?.shouldShowRationale != false,
                    onRequestPermission = onPermissionRequest,
                )
            }
        }

        item {
            GuidanceCard(
                currentOrientation = uiState.currentOrientation,
                targetParameters = uiState.optimalParams,
                debugFakeAlignmentActive = uiState.isDebugFakeAlignmentActive,
            )
        }

        item {
            AzimuthVisualizerCard(
                currentOrientation = uiState.currentOrientation,
                targetParameters = uiState.optimalParams,
                debugFakeAlignmentActive = uiState.isDebugFakeAlignmentActive,
            )
        }

        item {
            TargetParametersCard(params = uiState.optimalParams, location = uiState.currentLocation)
        }

        item { CurrentOrientationCard(orientation = uiState.currentOrientation) }
    }
}

@Composable
private fun SolPanTopBar(
    selectedMode: TiltMode,
    onShowTutorial: () -> Unit,
    onToggleDebug: () -> Unit,
    onNavigateToAboutLibraries: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name) + " - " + selectedMode.displayName(),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        actions = {
            IconButton(onClick = onShowTutorial) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = stringResource(id = R.string.action_help),
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.action_more),
                    )
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    if (BuildConfig.DEBUG) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_toggle_debug)) },
                            onClick = {
                                onToggleDebug()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null) },
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_about_libraries)) },
                        onClick = {
                            onNavigateToAboutLibraries()
                            showMenu = false
                        },
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SolPanScreenPreview() {
    SolPanTheme {
        SolPanScreenContent(
            contentPadding = PaddingValues(0.dp),
            locationPermissionsState = null,
            uiState =
                SolPanUiState(
                    selectedMode = TiltMode.SUMMER,
                    currentLocation = LocationData(latitude = 36.1627, longitude = -86.7816),
                    currentOrientation = OrientationData(azimuth = 150f, pitch = -25f, roll = 10f),
                    optimalParams =
                        OptimalPanelParameters(
                            targetTrueAzimuth = 180.0,
                            targetMagneticAzimuth = 178.0,
                            targetTilt = 35.0,
                            mode = TiltMode.SUMMER,
                            magneticDeclination = -2.0f,
                        ),
                ),
        )
    }
}
