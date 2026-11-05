package com.example.weatherwise

import android.content.Context

/**
 * Utility class to persist and retrieve weather data locally for offline usage.
 */
object WeatherCacheManager {

    private const val PREFS_NAME = "WeatherWisePrefs"
    private const val KEY_CACHED_TEMP = "cached_temp"
    private const val KEY_CACHED_CONDITION = "cached_condition"
    private const val KEY_CACHED_LOCATION = "cached_location"
    private const val KEY_CACHED_FEELS_LIKE = "cached_feels_like"
    private const val KEY_CACHED_TIME = "cached_timestamp"
    private const val KEY_CACHED_ICON = "cached_icon_id"

    fun saveCurrentCondition(context: Context, locationName: String, temp: Int, condition: String, feelsLike: Int, iconId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CACHED_LOCATION, locationName)
            .putInt(KEY_CACHED_TEMP, temp)
            .putString(KEY_CACHED_CONDITION, condition)
            .putInt(KEY_CACHED_FEELS_LIKE, feelsLike)
            .putInt(KEY_CACHED_ICON, iconId)
            .putLong(KEY_CACHED_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getCachedWeather(context: Context): CachedWeather {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val location = prefs.getString(KEY_CACHED_LOCATION, "Johannesburg") ?: "Johannesburg"
        val temp = prefs.getInt(KEY_CACHED_TEMP, 22)
        val condition = prefs.getString(KEY_CACHED_CONDITION, "Partly Cloudy") ?: "Partly Cloudy"
        val feelsLike = prefs.getInt(KEY_CACHED_FEELS_LIKE, 24)
        val iconId = prefs.getInt(KEY_CACHED_ICON, 1)
        val timestamp = prefs.getLong(KEY_CACHED_TIME, System.currentTimeMillis())
        return CachedWeather(location, temp, condition, feelsLike, iconId, timestamp)
    }
}

data class CachedWeather(
    val location: String,
    val temp: Int,
    val condition: String,
    val feelsLike: Int,
    val iconId: Int,
    val timestamp: Long
)
