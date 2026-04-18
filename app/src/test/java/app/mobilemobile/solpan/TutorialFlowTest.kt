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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TutorialFlowTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakePrefs: FakeUserPreferencesRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakeUserPreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SolPanViewModel(SolPan(TiltMode.YEAR_ROUND), fakePrefs)

    @Test
    fun `showTutorial is true initially when tutorial not seen`() =
        runTest {
            // fakePrefs defaults tutorialSeen=false → !seen = true
            val vm = createViewModel()
            assertTrue(vm.showTutorial.first())
        }

    @Test
    fun `showTutorial is false when tutorial already seen`() =
        runTest {
            fakePrefs.setTutorialSeen(true)
            val vm = createViewModel()
            assertFalse(vm.showTutorial.first())
        }

    @Test
    fun `dismissTutorial hides tutorial and persists seen state`() =
        runTest {
            val vm = createViewModel()
            assertTrue(vm.showTutorial.first())
            vm.dismissTutorial()
            assertFalse(vm.showTutorial.first())
            // Also persisted
            assertTrue(fakePrefs.tutorialSeen.first())
        }

    @Test
    fun `requestTutorial shows tutorial even when already seen`() =
        runTest {
            fakePrefs.setTutorialSeen(true)
            val vm = createViewModel()
            assertFalse(vm.showTutorial.first()) // starts hidden
            vm.requestTutorial()
            assertTrue(vm.showTutorial.first()) // override shows it
        }

    @Test
    fun `tutorialOverride takes priority over persisted preference`() =
        runTest {
            fakePrefs.setTutorialSeen(true)
            val vm = createViewModel()
            vm.requestTutorial()
            assertTrue(vm.showTutorial.first()) // override=true beats seen=true
            vm.dismissTutorial()
            assertFalse(vm.showTutorial.first()) // override=false beats seen=true
        }
}
