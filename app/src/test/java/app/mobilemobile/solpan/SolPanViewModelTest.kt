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

import app.mobilemobile.solpan.data.TiltMode
import app.mobilemobile.solpan.ui.SolPan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolPanViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakePrefs: FakeSharedPreferences

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakeSharedPreferences()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(mode: TiltMode = TiltMode.YEAR_ROUND) = SolPanViewModel(SolPan(mode), fakePrefs)

    @Test
    fun `initial state has null location and parameters`() =
        runTest {
            val vm = createViewModel()
            assertNull(vm.currentLocation.value)
            assertNull(vm.optimalPanelParameters.value)
        }

    @Test
    fun `selected tilt mode matches constructor key`() =
        runTest {
            TiltMode.entries.forEach { mode ->
                val vm = createViewModel(mode)
                assertEquals(mode, vm.selectedTiltModeFlow.value)
            }
        }

    @Test
    fun `debug fake alignment toggles correctly`() =
        runTest {
            val vm = createViewModel()
            assertEquals(false, vm.debugFakeAlignmentActive.value)
            vm.toggleDebugFakeAlignment()
            assertEquals(true, vm.debugFakeAlignmentActive.value)
            vm.toggleDebugFakeAlignment()
            assertEquals(false, vm.debugFakeAlignmentActive.value)
        }

    @Test
    fun `updating location with null clears location`() =
        runTest {
            val vm = createViewModel()
            vm.updateLocation(null)
            assertNull(vm.currentLocation.value)
        }

    @Test
    fun `northern hemisphere year-round target azimuth faces south (180)`() =
        runTest {
            val vm = createViewModel(TiltMode.YEAR_ROUND)
            vm.updateLocation(
                app.mobilemobile.solpan.data.LocationData(
                    latitude = 36.16,
                    longitude = -86.78,
                ),
            )
            // Give flow time to compute
            val params = vm.optimalPanelParameters.first { it != null }
            assertNotNull(params)
            assertEquals(180.0, params!!.targetTrueAzimuth, 0.001)
            assertTrue(
                "Tilt should be close to latitude (~36°), was ${params.targetTilt}",
                params.targetTilt in 30.0..42.0,
            )
        }

    @Test
    fun `southern hemisphere year-round target azimuth faces north (0)`() =
        runTest {
            val vm = createViewModel(TiltMode.YEAR_ROUND)
            vm.updateLocation(
                app.mobilemobile.solpan.data.LocationData(
                    latitude = -33.87,
                    longitude = 151.21,
                ),
            )
            val params = vm.optimalPanelParameters.first { it != null }
            assertNotNull(params)
            assertEquals(0.0, params!!.targetTrueAzimuth, 0.001)
            assertTrue(
                "Tilt should be close to latitude (~34°), was ${params.targetTilt}",
                params.targetTilt in 28.0..40.0,
            )
        }

    @Test
    fun `summer mode tilt is lower than year-round`() =
        runTest {
            val vmSummer = createViewModel(TiltMode.SUMMER)
            val vmYearRound = createViewModel(TiltMode.YEAR_ROUND)
            val location =
                app.mobilemobile.solpan.data
                    .LocationData(latitude = 36.16, longitude = -86.78)
            vmSummer.updateLocation(location)
            vmYearRound.updateLocation(location)

            val summer = vmSummer.optimalPanelParameters.first { it != null }!!
            val yearRound = vmYearRound.optimalPanelParameters.first { it != null }!!

            assertTrue(
                "Summer tilt (${summer.targetTilt}) should be < year-round (${yearRound.targetTilt})",
                summer.targetTilt < yearRound.targetTilt,
            )
        }

    @Test
    fun `winter mode tilt is higher than year-round`() =
        runTest {
            val vmWinter = createViewModel(TiltMode.WINTER)
            val vmYearRound = createViewModel(TiltMode.YEAR_ROUND)
            val location =
                app.mobilemobile.solpan.data
                    .LocationData(latitude = 36.16, longitude = -86.78)
            vmWinter.updateLocation(location)
            vmYearRound.updateLocation(location)

            val winter = vmWinter.optimalPanelParameters.first { it != null }!!
            val yearRound = vmYearRound.optimalPanelParameters.first { it != null }!!

            assertTrue(
                "Winter tilt (${winter.targetTilt}) should be > year-round (${yearRound.targetTilt})",
                winter.targetTilt > yearRound.targetTilt,
            )
        }
}
