package com.example.weatherwise

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface defining the AccuWeather REST API endpoints.
 */
interface AccuWeatherApi {
    
    // Search for locations by city name
    @GET("locations/v1/cities/search")
    fun searchLocation(
        @Query("apikey") apiKey: String,
        @Query("q") query: String
    ): Call<List<AccuLocation>>

    // Get current weather conditions for a specific location key
    @GET("currentconditions/v1/{locationKey}")
    fun getCurrentConditions(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("details") details: Boolean = true
    ): Call<List<CurrentCondition>>

    // Get 12 hours of hourly forecast data
    @GET("forecasts/v1/hourly/12hour/{locationKey}")
    fun getHourlyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("metric") metric: Boolean = true
    ): Call<List<HourlyForecast>>

    // Get 5 days of daily forecast data
    @GET("forecasts/v1/daily/5day/{locationKey}")
    fun getDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("metric") metric: Boolean = true
    ): Call<DailyForecastResponse>
}

/**
 * Singleton object to provide the Retrofit API service instance.
 */
object WeatherServiceClient {
    private const val BASE_URL = "https://dataservice.accuweather.com/"
    
    val api: AccuWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AccuWeatherApi::class.java)
    }
}
