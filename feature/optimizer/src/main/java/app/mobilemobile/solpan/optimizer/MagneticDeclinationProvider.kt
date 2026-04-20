/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.optimizer

/**
 * Provides magnetic declination values for a given location and time.
 * This abstraction allows for testability and future extensions (e.g., caching, alternative sources).
 */
interface MagneticDeclinationProvider {
    /**
     * Calculate magnetic declination at the given location and time.
     *
     * @param latitude Latitude in degrees
     * @param longitude Longitude in degrees
     * @param altitude Altitude in meters (optional, defaults to 0)
     * @param timeMillis Timestamp in milliseconds
     * @return Magnetic declination in degrees, or null if calculation fails
     */
    fun getMagneticDeclination(
        latitude: Float,
        longitude: Float,
        altitude: Float = 0f,
        timeMillis: Long,
    ): Float?
}

/**
 * Default implementation using Android's GeomagneticField API.
 */
class AndroidMagneticDeclinationProvider : MagneticDeclinationProvider {
    override fun getMagneticDeclination(
        latitude: Float,
        longitude: Float,
        altitude: Float,
        timeMillis: Long,
    ): Float? = try {
        android.hardware.GeomagneticField(latitude, longitude, altitude, timeMillis).declination
    } catch (e: RuntimeException) {
        // GeomagneticField throws RuntimeException on invalid input or missing geomagnetic model
        null
    }
}
