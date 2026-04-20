/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.data

import app.mobilemobile.solpan.model.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LocationRepository {
    val currentLocation: StateFlow<LocationData?>
    fun updateLocation(location: LocationData?)
}

class DefaultLocationRepository : LocationRepository {
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    override val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    override fun updateLocation(location: LocationData?) {
        _currentLocation.value = location
    }
}
