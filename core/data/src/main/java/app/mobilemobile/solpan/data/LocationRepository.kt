/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.data

import app.mobilemobile.solpan.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing device location state.
 *
 * Abstracts location acquisition (GPS, network) from UI layer.
 * Persists across screen recompositions and activity recreation.
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
 * Stores the latest location in a [MutableStateFlow]. Not persisted to disk
 * (location changes frequently and expires quickly).
 */
public class DefaultLocationRepository : LocationRepository {
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    override val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    override fun updateLocation(location: LocationData?) {
        _currentLocation.value = location
    }
}
