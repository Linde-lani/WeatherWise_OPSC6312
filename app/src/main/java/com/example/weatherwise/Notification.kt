package com.example.weatherwise

/**
 * Data class representing a weather notification or system message.
 */
data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    var isSelected: Boolean = false
)
