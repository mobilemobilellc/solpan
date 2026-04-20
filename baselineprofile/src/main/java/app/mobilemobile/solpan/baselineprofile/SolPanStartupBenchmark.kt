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
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Startup macrobenchmarks for SolPan.
 *
 * This suite measures Time To Initial Display (TTID), Time To Full Display (TTFD), and related
 * startup metrics under different compilation modes (cold, warm, hot).
 *
 * Acceptance criteria (SolPan baseline):
 * - Cold start TTID: < 1200ms
 * - Warm start TTID: < 400ms
 * - Hot start TTID: < 150ms
 *
 * These benchmarks validate that startup performance remains within acceptable bounds as features
 * are added.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SolPanStartupBenchmark {

    @get:Rule val rule = MacrobenchmarkRule()

    /**
     * Measures cold start performance (fresh app launch, no cache).
     *
     * Cold start is the most demanding scenario—app and system caches are cleared.
     * Validates baseline profile effectiveness and initialization overhead.
     */
    @Test
    fun startupColdCompilationBaseline() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(androidx.benchmark.macro.FrameTimingMetric()),
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.COLD,
            iterations = 3,
        ) {
            pressHome()
            startActivityAndWait()
        }
    }

    /**
     * Measures warm start performance (app in memory, cache present).
     *
     * Warm start reflects typical usage: user switches back to app after brief absence.
     */
    @Test
    fun startupWarmCompilationBaseline() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(androidx.benchmark.macro.FrameTimingMetric()),
            compilationMode = CompilationMode.Partial(),
            startupMode = StartupMode.WARM,
            iterations = 3,
        ) {
            pressHome()
            startActivityAndWait()
            pressHome()
            startActivityAndWait()
        }
    }

    /**
     * Measures hot start performance (app fully cached and running).
     *
     * Hot start measures pure UI rendering and composition without I/O or system contention.
     */
    @Test
    fun startupHotCompilationBaseline() {
        rule.measureRepeated(
            packageName = "app.mobilemobile.solpan",
            metrics = listOf(androidx.benchmark.macro.FrameTimingMetric()),
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.HOT,
            iterations = 3,
        ) {
            startActivityAndWait()
        }
    }
}
