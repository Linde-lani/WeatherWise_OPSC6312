package com.example.weatherwise

import com.google.gson.annotations.SerializedName

/**
 * Data model for AccuWeather Location search result.
 */
data class AccuLocation(
    @SerializedName("Key") val key: String,
    @SerializedName("LocalizedName") val name: String,
    @SerializedName("Country") val country: Country
)

data class Country(
    @SerializedName("ID") val id: String,
    @SerializedName("LocalizedName") val name: String
)

/**
 * Data model for Current Conditions.
 */
data class CurrentCondition(
    @SerializedName("WeatherText") val weatherText: String,
    @SerializedName("WeatherIcon") val weatherIcon: Int,
    @SerializedName("Temperature") val temperature: TemperatureWrapper,
    @SerializedName("RealFeelTemperature") val realFeel: TemperatureWrapper
)

data class TemperatureWrapper(
    @SerializedName("Metric") val metric: TemperatureValue
)

data class TemperatureValue(
    @SerializedName("Value") val value: Double,
    @SerializedName("Unit") val unit: String
)

/**
 * Data model for Hourly Forecast.
 */
data class HourlyForecast(
    @SerializedName("DateTime") val dateTime: String,
    @SerializedName("WeatherIcon") val weatherIcon: Int,
    @SerializedName("IconPhrase") val iconPhrase: String,
    @SerializedName("Temperature") val temperature: TemperatureValue
)

/**
 * Data model for Daily Forecast.
 */
data class DailyForecastResponse(
    @SerializedName("DailyForecasts") val forecasts: List<DailyForecast>
)

data class DailyForecast(
    @SerializedName("Date") val date: String,
    @SerializedName("Temperature") val temperature: DailyTemperature,
    @SerializedName("Day") val day: DayForecast
)

data class DailyTemperature(
    @SerializedName("Minimum") val min: TemperatureValue,
    @SerializedName("Maximum") val max: TemperatureValue
)

data class DayForecast(
    @SerializedName("Icon") val icon: Int,
    @SerializedName("IconPhrase") val phrase: String
)