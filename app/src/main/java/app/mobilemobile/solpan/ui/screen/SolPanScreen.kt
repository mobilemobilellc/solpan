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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import app.mobilemobile.solpan.BuildConfig
import app.mobilemobile.solpan.R
import app.mobilemobile.solpan.SolPanViewModel
import app.mobilemobile.solpan.data.LocationData
import app.mobilemobile.solpan.data.OptimalPanelParameters
import app.mobilemobile.solpan.data.OrientationData
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
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun SolPanScreen(
    viewModel: SolPanViewModel,
    onNavigateToAboutLibraries: () -> Unit = {},
) {
    val context = LocalContext.current
    val preferences =
        context.getSharedPreferences(
            "app.mobilemobile.solpan_preferences",
            android.content.Context.MODE_PRIVATE,
        )

    val optimalParams by viewModel.optimalPanelParameters.collectAsState()
    val currentSelectedMode by viewModel.selectedTiltModeFlow.collectAsState()
    val vmLocation by viewModel.currentLocation.collectAsState()
    val debugFakeAlignmentActive by viewModel.debugFakeAlignmentActive.collectAsState()

    val deviceOrientationController = rememberDeviceOrientationController()
    val currentOrientation by deviceOrientationController.orientation

    val tutorialSeen = preferences.getBoolean("tutorialSeen", false)
    var showHelpDialog by remember { mutableStateOf(!tutorialSeen) }
    val analytics = remember { Firebase.analytics }

    if (showHelpDialog) {
        HelpGuidanceDialog {
            showHelpDialog = false
            preferences.edit { putBoolean("tutorialSeen", true) }
            analytics.logEvent("end_tutorial", null)
        }
    }

    DisposableEffect(Unit) {
        deviceOrientationController.startListening()
        onDispose { deviceOrientationController.stopListening() }
    }

    val locationPermissionsState =
        rememberMultiplePermissionsState(
            listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        )

    val deviceLocationController =
        rememberDeviceLocationController(onLocationUpdate = viewModel::updateLocation)

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        val permissionGranted = locationPermissionsState.allPermissionsGranted
        analytics.logEvent("permission_request_response") {
            param("permission_type", "location")
            param("permission_granted", permissionGranted.toString())
        }
        if (permissionGranted) {
            deviceLocationController.startLocationUpdates()
        } else {
            deviceLocationController.stopLocationUpdates()
            viewModel.updateLocation(null)
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var showOverflowMenu by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(id = R.string.solar_app_screen_title)) },
                subtitle = {
                    Text(
                        stringResource(
                            id = R.string.solar_app_screen_subtitle_tilt_mode,
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
                            showHelpDialog = true
                            analytics.logEvent("start_tutorial", null)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = stringResource(id = R.string.help_dialog_title),
                        )
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                        ) {
                            if (BuildConfig.DEBUG) {
                                DropdownMenuItem(
                                    text = { Text("Toggle Fake Alignment") },
                                    onClick = {
                                        viewModel.toggleDebugFakeAlignment()
                                        showOverflowMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.BugReport,
                                            contentDescription = "Toggle Fake Alignment",
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
            contentPadding,
            locationPermissionsState,
            currentOrientation,
            optimalParams,
            debugFakeAlignmentActive,
            vmLocation,
        )
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun SolPanScreenContent(
    contentPadding: PaddingValues,
    locationPermissionsState: MultiplePermissionsState,
    currentOrientation: OrientationData,
    optimalParams: OptimalPanelParameters?,
    debugFakeAlignmentActive: Boolean,
    vmLocation: LocationData?,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        columns = StaggeredGridCells.Adaptive(240.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = spacedBy(8.dp),
    ) {
        if (!locationPermissionsState.allPermissionsGranted) {
            item { PermissionRequestCard(locationPermissionsState) }
        }

        item {
            AzimuthVisualizerCard(
                currentOrientation = currentOrientation,
                targetParameters = optimalParams,
                debugFakeAlignmentActive = debugFakeAlignmentActive,
            )
        }

        item {
            GuidanceCard(
                currentOrientation = currentOrientation,
                targetParameters = optimalParams,
                debugFakeAlignmentActive = debugFakeAlignmentActive,
            )
        }

        item { CurrentOrientationCard(currentOrientation) }

        item {
            TargetParametersCard(
                optimalParams,
                vmLocation,
                locationPermissionsState.allPermissionsGranted,
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun SolarAppScreenDarkPreview() {
    SolPanTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SolPanScreen(viewModel = viewModel(), onNavigateToAboutLibraries = {})
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Mode")
@Composable
fun SolarAppScreenLightPreview() {
    SolPanTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SolPanScreen(viewModel = viewModel(), onNavigateToAboutLibraries = {})
        }
    }
}
