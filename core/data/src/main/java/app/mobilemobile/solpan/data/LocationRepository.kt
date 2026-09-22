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

import app.mobilemobile.solpan.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing device location state.
 *
 * Abstracts location acquisition (GPS, network) from UI layer. Persists across screen
 * recompositions and activity recreation.
 */
public interface LocationRepository {
    /** Current location as a reactive [StateFlow]. Emits null while acquiring initial location. */
    val currentLocation: StateFlow<LocationData?>

    /** Update or clear the current location (called by location controller). */
    fun updateLocation(location: LocationData?)
}

/**
 * Default in-memory implementation of [LocationRepository].
 *
 * Stores the latest location in a [MutableStateFlow]. Not persisted to disk (location changes
 * frequently and expires quickly).
 */
public class DefaultLocationRepository : LocationRepository {
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    override val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    override fun updateLocation(location: LocationData?) {
        _currentLocation.value = location
    }
}
