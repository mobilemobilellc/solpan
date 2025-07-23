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
package app.mobilemobile.solpan.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import app.mobilemobile.solpan.SolPanViewModel
import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.ui.aboutlibraries.AboutLibrariesScreen
import app.mobilemobile.solpan.ui.screen.SolPanScreen
import kotlinx.serialization.Serializable

@Serializable
data class SolPan(
    val mode: TiltMode,
) : NavKey

@Serializable data object AboutLibraries : NavKey

@Composable
fun SolPanApp() {
    val backStack = rememberNavBackStack(SolPan(TiltMode.YEAR_ROUND))

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            TiltMode.entries.forEach { mode ->
                val currentScreen = backStack.lastOrNull()
                item(
                    selected = currentScreen is SolPan && currentScreen.mode == mode,
                    onClick = {
                        if (backStack.lastOrNull() != SolPan(mode)) {
                            backStack.clear()
                            backStack.add(SolPan(mode))
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = stringResource(id = mode.titleRes),
                        )
                    },
                    label = { Text(stringResource(id = mode.titleRes)) },
                )
            }
        },
    ) {
        NavDisplay(
            entryDecorators =
                listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is SolPan -> {
                        NavEntry(key) {
                            SolPanScreen(
                                viewModel = viewModel(factory = SolPanViewModel.Factory(key)),
                                onNavigateToAboutLibraries = { backStack.add(AboutLibraries) },
                            )
                        }
                    }

                    is AboutLibraries -> {
                        NavEntry(key) {
                            AboutLibrariesScreen(onNavigateBack = { backStack.removeLastOrNull() })
                        }
                    }

                    else -> {
                        NavEntry(key) { error("Unknown key: $key") }
                    }
                }
            },
        )
    }
}
