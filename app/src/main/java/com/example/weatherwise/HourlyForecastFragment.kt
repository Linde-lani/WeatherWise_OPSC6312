package com.example.weatherwise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragment to display the Hourly Forecast snippet on the Dashboard.
 * Dynamically fetches data from AccuWeather API based on currently selected location.
 */
class HourlyForecastFragment : Fragment() {

    private val API_KEY = "zpka_7a60f44cc3034119b10f89e87e5a37ea_af868641"
    private lateinit var hourlyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hourly_forecast, container, false)
        hourlyContainer = view.findViewById(R.id.layoutHourlyContainer)
        
        val sharedPrefs = requireContext().getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val locationKey = sharedPrefs.getString("default_location_key", "306633") ?: "306633"
        fetchHourlyForecast(locationKey)
        
        return view
    }

    fun updateLocation(locationKey: String) {
        if (::hourlyContainer.isInitialized) {
            fetchHourlyForecast(locationKey)
        }
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

                override fun onFailure(call: Call<List<HourlyForecast>>, t: Throwable) {}
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

            val time = try {
                if (forecast.dateTime.length >= 16) forecast.dateTime.substring(11, 16) else forecast.dateTime
            } catch (e: Exception) {
                forecast.dateTime
            }
            
            txtTime.text = time
            txtTemp.text = "${forecast.temperature.value.toInt()}°"
            
            val isNight = !forecast.isDayTime || try {
                val hour = if (forecast.dateTime.length >= 13) forecast.dateTime.substring(11, 13).toInt() else 12
                hour >= 18 || hour < 6
            } catch (e: Exception) {
                false
            }
            imgIcon.setImageResource(WeatherIconMapper.getWeatherIcon(forecast.weatherIcon, forecast.iconPhrase, isNight))
            
            hourlyContainer.addView(itemView)
        }
    }
}
