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
package app.mobilemobile.solpan.optimizer

import app.mobilemobile.solpan.FakeAnalyticsTracker
import app.mobilemobile.solpan.FakeUserPreferencesRepository
import app.mobilemobile.solpan.data.DefaultLocationRepository
import app.mobilemobile.solpan.model.LocationData
import app.mobilemobile.solpan.model.TiltMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolPanViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakePrefs: FakeUserPreferencesRepository
    private lateinit var locationRepository: DefaultLocationRepository
    private lateinit var fakeAnalytics: FakeAnalyticsTracker
    private lateinit var fakeMagneticProvider: FakeMagneticDeclinationProvider

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakeUserPreferencesRepository()
        locationRepository = DefaultLocationRepository()
        fakeAnalytics = FakeAnalyticsTracker()
        fakeMagneticProvider = FakeMagneticDeclinationProvider()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(mode: TiltMode = TiltMode.YEAR_ROUND) =
        SolPanViewModel(mode, fakePrefs, locationRepository, fakeAnalytics, fakeMagneticProvider)

    @Test
    fun `initial state has null location and parameters`() =
        runTest {
            val vm = createViewModel()
            assertNull(vm.currentLocation.value)
            assertNull(vm.optimalPanelParameters.value)
        }

    @Test
    fun `updating location updates currentLocation flow`() =
        runTest {
            val vm = createViewModel()
            val location = LocationData(latitude = 37.7749, longitude = -122.4194)
            vm.updateLocation(location)
            assertEquals(location, vm.currentLocation.value)
        }

    @Test
    fun `northern hemisphere year-round target azimuth faces south (180)`() =
        runTest {
            val vm = createViewModel(TiltMode.YEAR_ROUND)
            vm.updateLocation(LocationData(latitude = 36.1627, longitude = -86.7816))

            val params = vm.optimalPanelParameters.first { it != null }!!
            assertEquals(180.0, params.targetTrueAzimuth, 0.1)
        }

    @Test
    fun `southern hemisphere year-round target azimuth faces north (0)`() =
        runTest {
            val vm = createViewModel(TiltMode.YEAR_ROUND)
            vm.updateLocation(LocationData(latitude = -33.8688, longitude = 151.2093))

            val params = vm.optimalPanelParameters.first { it != null }!!
            assertEquals(0.0, params.targetTrueAzimuth, 0.1)
        }

    @Test
    fun `winter mode tilt is higher than year-round`() =
        runTest {
            val location = LocationData(latitude = 40.0, longitude = -100.0)

            val vmYearRound = createViewModel(TiltMode.YEAR_ROUND)
            vmYearRound.updateLocation(location)
            val yearRound = vmYearRound.optimalPanelParameters.first { it != null }!!

            val vmWinter = createViewModel(TiltMode.WINTER)
            vmWinter.updateLocation(location)
            val winter = vmWinter.optimalPanelParameters.first { it != null }!!

            assertTrue(
                "Winter tilt (${winter.targetTilt}) should be > year-round (${yearRound.targetTilt})",
                winter.targetTilt > yearRound.targetTilt,
            )
        }
}
