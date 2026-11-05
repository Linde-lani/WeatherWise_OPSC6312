package com.example.weatherwise

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import java.util.Calendar

/**
 * Utility helper to manage Dark Mode state, Auto-Sunset detection, and App Theme application.
 */
object ThemeUtils {

    const val PREFS_NAME = "WeatherWisePrefs"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_AUTO_SUNSET = "auto_sunset_dark_mode"

    /**
     * Determines whether Dark Mode should be active.
     * Active if manually enabled, OR if auto-sunset is enabled and current time is past sunset (18:00 to 06:00).
     */
    fun isDarkModeActive(context: Context, isDayTimeCondition: Boolean? = null): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val manualDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        val autoSunset = prefs.getBoolean(KEY_AUTO_SUNSET, true) // Enabled by default

        if (manualDarkMode) {
            return true
        }

        if (autoSunset) {
            // Check condition from weather API if explicitly passed
            if (isDayTimeCondition != null && !isDayTimeCondition) {
                return true
            }
            // Check local system time (sunset is between 18:00 / 6 PM and 06:00 / 6 AM)
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour >= 18 || currentHour < 6) {
                return true
            }
        }

        return false
    }

    /**
     * Applies the current theme mode (AppCompatDelegate) based on preferences and sunset status.
     */
    fun applyTheme(context: Context, isDayTimeCondition: Boolean? = null) {
        val isDark = isDarkModeActive(context, isDayTimeCondition)
        val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }
    }

    /**
     * Updates a screen root layout background to Navy Blue if Dark Mode, or Sky Blue if Light Mode.
     */
    fun applyBackground(view: View?, isDark: Boolean) {
        view?.setBackgroundResource(if (isDark) R.drawable.bg_navyblue else R.drawable.bg_skyblue)
    }
}
