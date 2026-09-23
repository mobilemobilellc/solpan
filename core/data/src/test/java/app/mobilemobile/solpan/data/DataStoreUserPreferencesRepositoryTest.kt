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
package app.mobilemobile.solpan.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DataStoreUserPreferencesRepositoryTest {
    @get:Rule val tmp = TemporaryFolder()

    private fun TestScope.repository(file: File) =
        DataStoreUserPreferencesRepository(
            PreferenceDataStoreFactory.create(
                scope = backgroundScope,
                produceFile = { file },
            ),
        )

    @Test
    fun `tutorial is unseen until it is marked seen`() =
        runTest(UnconfinedTestDispatcher()) {
            val repository = repository(File(tmp.root, "settings.preferences_pb"))

            assertFalse(repository.tutorialSeen.first())
            repository.setTutorialSeen(true)
            assertTrue(repository.tutorialSeen.first())
        }

    @Test
    fun `a corrupt settings file reads as defaults instead of failing`() =
        runTest(UnconfinedTestDispatcher()) {
            val file =
                File(tmp.root, "settings.preferences_pb").apply {
                    writeBytes(byteArrayOf(-1, -1, -1, 0x7f))
                }

            assertFalse(repository(file).tutorialSeen.first())
        }
}
