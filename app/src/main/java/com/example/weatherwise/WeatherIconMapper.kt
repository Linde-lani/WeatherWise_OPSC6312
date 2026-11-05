package com.example.weatherwise

/**
 * Utility object to map weather condition texts and AccuWeather icon IDs to appropriate drawables,
 * handling daytime (sun) vs. nighttime (moon) appropriately.
 */
object WeatherIconMapper {

    /**
     * Resolves and returns the drawable resource ID for a given weather condition and night/day state.
     *
     * @param weatherIconId AccuWeather icon number (1 to 44) if available.
     * @param weatherText Weather condition phrase string (e.g. "Sunny", "Partly Cloudy", "Clear").
     * @param isNight True if it is nighttime / past sunset or dark mode is active.
     */
    fun getWeatherIcon(weatherIconId: Int?, weatherText: String?, isNight: Boolean): Int {
        if (weatherIconId != null && weatherIconId > 0) {
            when (weatherIconId) {
                1, 2, 30 -> return if (isNight) R.drawable.ic_moon else R.drawable.ic_sunny
                33, 34 -> return R.drawable.ic_moon
                3, 4, 5 -> return if (isNight) R.drawable.ic_partly_cloudy_night else R.drawable.ic_partly_cloudy_day
                35, 36, 37 -> return R.drawable.ic_partly_cloudy_night
                6, 7, 8, 11, 38 -> return R.drawable.ic_cloudy
                12, 13, 14, 15, 16, 17, 18, 39, 40, 41, 42 -> return R.drawable.ic_rain
            }
        }

        val text = weatherText?.lowercase() ?: ""
        return when {
            text.contains("rain") || text.contains("shower") || text.contains("drizzle") || text.contains("thunder") || text.contains("storm") -> {
                R.drawable.ic_rain
            }
            text.contains("partly") || text.contains("intermittent") || text.contains("scattered") -> {
                if (isNight) R.drawable.ic_partly_cloudy_night else R.drawable.ic_partly_cloudy_day
            }
            text.contains("cloud") || text.contains("overcast") || text.contains("fog") || text.contains("haze") -> {
                R.drawable.ic_cloudy
            }
            text.contains("sun") || text.contains("clear") || text.contains("fair") -> {
                if (isNight) R.drawable.ic_moon else R.drawable.ic_sunny
            }
            else -> {
                if (isNight) R.drawable.ic_moon else R.drawable.ic_sunny
            }
        }
    }
}
