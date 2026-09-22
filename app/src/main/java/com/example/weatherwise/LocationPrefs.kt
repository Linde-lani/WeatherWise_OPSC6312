package com.example.weatherwise

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SavedLocation(
    val key: String,
    val name: String,
    val country: String
)

object LocationPrefs {
    private const val PREFS_NAME = "weatherwise_prefs"
    private const val KEY_DEFAULT_LOCATION_KEY = "default_location_key"
    private const val KEY_DEFAULT_LOCATION_NAME = "default_location_name"
    private const val KEY_SAVED_LIST = "saved_locations_list"

    fun getDefaultLocationKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEFAULT_LOCATION_KEY, "306633") ?: "306633" // Default to Johannesburg
    }

    fun getDefaultLocationName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEFAULT_LOCATION_NAME, "Johannesburg") ?: "Johannesburg"
    }

    fun setDefaultLocation(context: Context, key: String, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_DEFAULT_LOCATION_KEY, key)
            .putString(KEY_DEFAULT_LOCATION_NAME, name)
            .apply()
    }

    fun getSavedLocations(context: Context): List<SavedLocation> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SAVED_LIST, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<SavedLocation>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveLocation(context: Context, location: SavedLocation) {
        val list = getSavedLocations(context).toMutableList()
        if (!list.any { it.key == location.key }) {
            list.add(location)
            val json = Gson().toJson(list)
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SAVED_LIST, json)
                .apply()
        }
    }

    fun deleteLocation(context: Context, key: String) {
        val list = getSavedLocations(context).filter { it.key != key }
        val json = Gson().toJson(list)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SAVED_LIST, json)
            .apply()
    }
}
