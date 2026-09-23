package com.example.weatherwise

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
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment to display the Daily Forecast preview snippet on the Dashboard with named week days.
 */
class DailyForecastFragment : Fragment() {

    private val API_KEY = "zpka_8d09c7fe1d8a49c3a0bd5c87de1a18e3_5e9eabd2"
    private lateinit var dailyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daily_forecast, container, false)
        dailyContainer = view.findViewById(R.id.layoutDailyContainer)
        
        val sharedPrefs = requireContext().getSharedPreferences("WeatherWisePrefs", android.content.Context.MODE_PRIVATE)
        val locationKey = sharedPrefs.getString("default_location_key", "306633") ?: "306633"
        fetchDailyForecast(locationKey)
        
        return view
    }

    fun updateLocation(locationKey: String) {
        if (::dailyContainer.isInitialized) {
            fetchDailyForecast(locationKey)
        }
    }

    private fun fetchDailyForecast(locationKey: String) {
        WeatherServiceClient.api.getDailyForecast(locationKey, API_KEY)
            .enqueue(object : Callback<DailyForecastResponse> {
                override fun onResponse(
                    call: Call<DailyForecastResponse>,
                    response: Response<DailyForecastResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        populateDailyData(response.body()!!.forecasts)
                    }
                }

                override fun onFailure(call: Call<DailyForecastResponse>, t: Throwable) {}
            })
    }

    private fun populateDailyData(forecasts: List<DailyForecast>) {
        if (!isAdded) return
        dailyContainer.removeAllViews()
        val inflater = LayoutInflater.from(context)

        for ((index, forecast) in forecasts.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_daily_forecast, dailyContainer, false)
            
            val txtDayName = itemView.findViewById<TextView>(R.id.txtDayName)
            val txtDayHigh = itemView.findViewById<TextView>(R.id.txtDayHigh)
            val txtDayLow = itemView.findViewById<TextView>(R.id.txtDayLow)

            // Convert ISO dates into beautifully styled weekday titles like Mon, Tue, etc.
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate = inputFormat.parse(forecast.date.substring(0, 10))
                val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Full title name like Monday
                txtDayName.text = outputFormat.format(parsedDate!!)
            } catch (e: Exception) {
                txtDayName.text = "Day ${index + 1}"
            }
            
            txtDayHigh.text = "${forecast.temperature.max.value.toInt()}°"
            txtDayLow.text = "${forecast.temperature.min.value.toInt()}°"
            
            dailyContainer.addView(itemView)
        }
    }
}
