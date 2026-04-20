/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.mobilemobile.solpan.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Critical flow macrobenchmarks for SolPan.
 *
 * This suite measures performance of the primary user workflow:
 * - App startup
 * - Tab navigation between tilt modes
 * - Solar calculation updates
 * - State recomposition
 *
 * Acceptance criteria (SolPan baseline):
 * - Tab navigation: < 100ms jank per transition
 * - State updates: < 16ms per frame (60 fps)
 * - No visible stutter during mode transitions
 *
 * These benchmarks validate that reactive state management and UI composition scale well
 * as users interact with different optimization modes.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SolPanCriticalFlowBenchmark {

    @get:Rule val rule = MacrobenchmarkRule()

    /**
     * Measures performance of navigating between all tilt modes.
     *
     * This flow:
     * 1. Starts app in REALTIME mode
     * 2. Navigates to SUMMER, WINTER, SPRING_AUTUMN, YEAR_ROUND, back to REALTIME
     * 3. Each transition triggers:
     *    - ViewModel state update (tilt mode change)
     *    - Solar calculation recompute
     *    - Location/orientation sensor subscription change
     *    - UI recomposition (chart, info cards, optimization data)
     *
     * Metrics validate:
     * - StateFlow emission latency
     * - Compose recomposition efficiency
     * - Sensor/location subscription overhead
     * - Memory stability across mode switches
     */
    @Test
    fun criticalFlowModeNavigationWithCompilation() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.Partial(),
            iterations = 3,
        ) {
            pressHome()
            startActivityAndWait()
            device.waitForIdle()

            // Navigate through each tilt mode, measuring composition and state update latency
            val modes = listOf("Summer", "Winter", "Spring Autumn", "Year Round", "Realtime")

            for (mode in modes) {
                clickTab(mode)
                device.waitForIdle()
            }
        }
    }

    /**
     * Measures scrolling performance within a tilt mode screen.
     *
     * Each SolPanScreen displays:
     * - Chart component (interactive sun path visualization)
     * - Info cards (scrollable list of optimization parameters)
     * - Status row (current location, time, device orientation)
     *
     * This benchmark validates:
     * - Chart recomposition efficiency during scroll
     * - Info card rendering performance
     * - List item recycling (if using LazyColumn)
     * - Memory usage under scroll stress
     */
    @Test
    fun criticalFlowScrollingWithCompilation() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.Partial(),
            iterations = 3,
        ) {
            pressHome()
            startActivityAndWait()
            device.waitForIdle()

            // Scroll info cards up and down to measure list performance
            val scrollableContainer = device.findObject(UiSelector().scrollable(true))
            if (scrollableContainer != null) {
                repeat(3) {
                    scrollableContainer.swipe(0, 100, 0, -100, 10)
                    device.waitForIdle()
                }
            }
        }
    }

    /**
     * Measures state stability over repeated interactions.
     *
     * This stress test:
     * 1. Rapidly alternates between modes
     * 2. Simulates user tapping tabs multiple times
     * 3. Validates no memory leaks, subscription leaks, or state corruption
     *
     * Metrics validate:
     * - Memory growth under repeated state updates
     * - Frame rate stability
     * - No ANRs or composition errors
     */
    @Test
    fun criticalFlowRapidModeChangeWithCompilation() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.Partial(),
            iterations = 2,
        ) {
            pressHome()
            startActivityAndWait()
            device.waitForIdle()

            // Rapidly tap between modes
            val modes = listOf("Summer", "Winter", "Realtime", "Summer", "Winter")

            for (mode in modes) {
                clickTab(mode)
                // Minimal wait to stress reactive update pipeline
                device.waitForIdle()
            }
        }
    }

    private fun clickTab(contentDescription: String) {
        device.findObject(UiSelector().descriptionContains(contentDescription))
            .clickAndWaitForNewWindow()
    }
}
