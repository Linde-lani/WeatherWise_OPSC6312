package com.example.weatherwise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragment to display the Hourly Forecast snippet on the Dashboard.
 * Now dynamically fetches data from AccuWeather API.
 */
class HourlyForecastFragment : Fragment() {

    private val API_KEY = "zpka_8d09c7fe1d8a49c3a0bd5c87de1a18e3_5e9eabd2"
    private lateinit var hourlyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hourly_forecast, container, false)
        hourlyContainer = view.findViewById(R.id.layoutHourlyContainer)
        
        // Get location key from parent activity or shared preferences
        val locationKey = "306633" // Default to Johannesburg for now, or get from prefs
        fetchHourlyForecast(locationKey)
        
        return view
    }

    private fun fetchHourlyForecast(locationKey: String) {
        WeatherServiceClient.api.getHourlyForecast(locationKey, API_KEY)
            .enqueue(object : Callback<List<HourlyForecast>> {
                override fun onResponse(
                    call: Call<List<HourlyForecast>>,
                    response: Response<List<HourlyForecast>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        populateHourlyData(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<HourlyForecast>>, t: Throwable) {
                    // Fail silently or show error
                }
            })
    }

    private fun populateHourlyData(forecasts: List<HourlyForecast>) {
        if (!isAdded) return
        hourlyContainer.removeAllViews()
        val inflater = LayoutInflater.from(context)

        for (forecast in forecasts) {
            val itemView = inflater.inflate(R.layout.item_hourly_forecast, hourlyContainer, false)
            
            val txtTime = itemView.findViewById<TextView>(R.id.txtHourlyTime)
            val txtTemp = itemView.findViewById<TextView>(R.id.txtHourlyTemp)
            val imgIcon = itemView.findViewById<ImageView>(R.id.imgHourlyIcon)

            // Format time from DateTime string (e.g., "2023-10-27T14:00:00+02:00")
            val time = try {
                forecast.dateTime.substring(11, 16)
            } catch (e: Exception) {
                forecast.dateTime
            }
            
            txtTime.text = time
            txtTemp.text = "${forecast.temperature.value.toInt()}°"
            
            // In a real app, map icon numbers to drawables
            // For now using the placeholder
            
            hourlyContainer.addView(itemView)
        }
    }
}
