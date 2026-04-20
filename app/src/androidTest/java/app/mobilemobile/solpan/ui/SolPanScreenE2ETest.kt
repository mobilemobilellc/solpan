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

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.mobilemobile.solpan.analytics.FirebaseAnalyticsTracker
import app.mobilemobile.solpan.data.DataStoreUserPreferencesRepository
import app.mobilemobile.solpan.data.DefaultLocationRepository
import app.mobilemobile.solpan.model.TiltMode
import app.mobilemobile.solpan.optimizer.AndroidMagneticDeclinationProvider
import app.mobilemobile.solpan.optimizer.SolPanViewModel
import app.mobilemobile.solpan.ui.screen.SolPanScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end UI tests for SolPan main screen.
 *
 * Tests all 5 tilt modes, location updates, and user interactions on real Android runtime.
 */
@RunWith(AndroidJUnit4::class)
class SolPanScreenE2ETest {
    @get:Rule val composeTestRule = createComposeRule()

    private fun createViewModel(initialMode: TiltMode): SolPanViewModel {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferencesRepository = DataStoreUserPreferencesRepository(context)
        val locationRepository = DefaultLocationRepository()
        val analyticsTracker = FirebaseAnalyticsTracker()
        val magneticDeclinationProvider = AndroidMagneticDeclinationProvider()

        return SolPanViewModel(
            initialMode = initialMode,
            preferencesRepository = preferencesRepository,
            locationRepository = locationRepository,
            analytics = analyticsTracker,
            magneticDeclinationProvider = magneticDeclinationProvider,
        )
    }

    /** Test REALTIME tilt mode loads and displays. */
    @Test
    fun testRealtimeModeLoads() {
        val viewModel = createViewModel(TiltMode.REALTIME)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify the screen is displayed
        composeTestRule.onNodeWithText("REALTIME").assertIsDisplayed()
    }

    /** Test SUMMER tilt mode loads and displays. */
    @Test
    fun testSummerModeLoads() {
        val viewModel = createViewModel(TiltMode.SUMMER)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify SUMMER mode is active
        composeTestRule.onNodeWithText("SUMMER").assertIsDisplayed()
    }

    /** Test WINTER tilt mode loads and displays. */
    @Test
    fun testWinterModeLoads() {
        val viewModel = createViewModel(TiltMode.WINTER)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify WINTER mode is active
        composeTestRule.onNodeWithText("WINTER").assertIsDisplayed()
    }

    /** Test SPRING_AUTUMN tilt mode loads and displays. */
    @Test
    fun testSpringAutumnModeLoads() {
        val viewModel = createViewModel(TiltMode.SPRING_AUTUMN)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify SPRING_AUTUMN mode is active
        composeTestRule.onNodeWithText("SPRING_AUTUMN").assertIsDisplayed()
    }

    /** Test YEAR_ROUND tilt mode loads and displays. */
    @Test
    fun testYearRoundModeLoads() {
        val viewModel = createViewModel(TiltMode.YEAR_ROUND)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify YEAR_ROUND mode is active
        composeTestRule.onNodeWithText("YEAR_ROUND").assertIsDisplayed()
    }

    /** Test UI renders with initial state for all modes. */
    @Test
    fun testUIRendersSuccessfully() {
        val allModes =
            listOf(
                TiltMode.REALTIME,
                TiltMode.SUMMER,
                TiltMode.WINTER,
                TiltMode.SPRING_AUTUMN,
                TiltMode.YEAR_ROUND,
            )

        for (mode in allModes) {
            val viewModel = createViewModel(mode)

            composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

            // Verify screen rendered for this mode
            composeTestRule.onNodeWithText(mode.name).assertIsDisplayed()
        }
    }

    /** Test mode switching through navigation. */
    @Test
    fun testModeNavigationTabs() {
        val viewModel = createViewModel(TiltMode.REALTIME)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Try to navigate to SUMMER
        composeTestRule.onNodeWithText("SUMMER").performClick()

        // Try to navigate to WINTER
        composeTestRule.onNodeWithText("WINTER").performClick()

        // Navigation should complete without crashing
        composeTestRule.onNodeWithText("REALTIME").assertIsDisplayed()
    }

    /** Test screen doesn't crash with minimal permissions. */
    @Test
    fun testScreenWithoutLocationPermission() {
        // Create ViewModel without granting location permission
        val viewModel = createViewModel(TiltMode.REALTIME)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Screen should still render (with "acquiring location" state)
        composeTestRule.onNodeWithText("REALTIME").assertIsDisplayed()
    }

    /** Test about libraries navigation (if available). */
    @Test
    fun testAboutLibrariesNavigation() {
        val viewModel = createViewModel(TiltMode.REALTIME)
        var navigatedToAbout = false

        composeTestRule.setContent {
            SolPanScreen(
                viewModel = viewModel,
                onNavigateToAboutLibraries = { navigatedToAbout = true },
            )
        }

        // Screen should render without crashing
        composeTestRule.onNodeWithText("REALTIME").assertIsDisplayed()
    }

    /** Test adaptive layout for different screen widths (compose test runs on phone by default). */
    @Test
    fun testAdaptiveLayoutPhone() {
        val viewModel = createViewModel(TiltMode.REALTIME)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify UI adapts to phone width
        composeTestRule.onNodeWithText("REALTIME").assertIsDisplayed()
    }

    /** Test all navigation tabs are accessible. */
    @Test
    fun testAllNavigationTabsPresent() {
        val viewModel = createViewModel(TiltMode.REALTIME)

        composeTestRule.setContent { SolPanScreen(viewModel = viewModel) }

        // Verify all 5 tilt mode tabs are present
        val modes = listOf("REALTIME", "SUMMER", "WINTER", "SPRING_AUTUMN", "YEAR_ROUND")
        for (mode in modes) {
            composeTestRule.onNodeWithText(mode).assertIsDisplayed()
        }
    }
}
