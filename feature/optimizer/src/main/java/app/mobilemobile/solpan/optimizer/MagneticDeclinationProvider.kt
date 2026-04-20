/*
 * Copyright 2025 MobileMobile LLC
 */
package app.mobilemobile.solpan.optimizer

/**
 * Provides magnetic declination values for a given location and time.
 *
 * Magnetic declination is the angle between true north and magnetic north at a specific
 * geographic location. This abstraction allows for testability and future extensions
 * (e.g., caching, model updates, alternative data sources).
 *
 * **Typical values:** -20° to +20° depending on location (varies ~6° per 1000 km).
 */
public interface MagneticDeclinationProvider {
    /**
     * Calculate magnetic declination at the given location and time.
     *
     * @param latitude Latitude in degrees (-90 to +90)
     * @param longitude Longitude in degrees (-180 to +180)
     * @param altitude Altitude in meters above sea level (optional, defaults to 0)
     * @param timeMillis Timestamp in milliseconds since Unix epoch (UTC)
     * @return Magnetic declination in degrees (-180 to +180), or null if calculation fails
     *   (e.g., location outside modeled region, time too far in past/future)
     */
    public fun getMagneticDeclination(
        latitude: Float,
        longitude: Float,
        altitude: Float = 0f,
        timeMillis: Long,
    ): Float?
}

/**
 * Default implementation using Android's GeomagneticField API.
 *
 * Wraps the IGRF-13 (International Geomagnetic Reference Field) model provided
 * by Android hardware APIs. Handles exceptions gracefully for invalid inputs.
 */
public class AndroidMagneticDeclinationProvider : MagneticDeclinationProvider {
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
