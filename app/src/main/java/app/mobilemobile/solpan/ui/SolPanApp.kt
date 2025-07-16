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

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import app.mobilemobile.solpan.ui.aboutlibraries.AboutLibrariesScreen
import app.mobilemobile.solpan.ui.screen.SolPanScreen

data object SolPan

data object AboutLibraries

@Composable
fun SolPanApp() {
  val backStack = remember { mutableStateListOf<Any>(SolPan) }

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = { key ->
      when (key) {
        is SolPan ->
          NavEntry(key) {
            SolPanScreen(
              viewModel = viewModel(),
              onNavigateToAboutLibraries = { backStack.add(AboutLibraries) },
            )
          }

        is AboutLibraries ->
          NavEntry(key) { AboutLibrariesScreen(onNavigateBack = { backStack.removeLastOrNull() }) }

        else -> NavEntry(Unit) { Text(text = "Invalid Key: $it") }
      }
    },
  )
}
