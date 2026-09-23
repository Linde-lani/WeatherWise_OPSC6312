package com.example.weatherwise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchScreen : AppCompatActivity() {

    private val API_KEY = "zpka_8d09c7fe1d8a49c3a0bd5c87de1a18e3_5e9eabd2"
    private var currentSearchedKey: String? = null
    private var currentSearchedName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_screen)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val edtSearchQuery = findViewById<TextInputEditText>(R.id.edtSearchQuery)
        val itemSearchResult = findViewById<LinearLayout>(R.id.itemSearchResult)
        val txtSearchResultName = findViewById<TextView>(R.id.txtSearchResultName)
        val txtSelectedLocation = findViewById<TextView>(R.id.txtSelectedLocation)
        val btnAddToFavorites = findViewById<MaterialButton>(R.id.btnAddToFavorites)

        btnBack.setOnClickListener { finish() }

        edtSearchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = edtSearchQuery.text.toString().trim()
                if (query.isNotEmpty()) {
                    performLocationSearch(query)
                }
                true
            } else {
                false
            }
        }

        itemSearchResult.setOnClickListener {
            val locationKey = itemSearchResult.tag as? String ?: return@setOnClickListener
            val cityName = txtSearchResultName.text.toString().split(",")[0]
            currentSearchedKey = locationKey
            currentSearchedName = cityName
            
            txtSelectedLocation.text = cityName
            fetchWeatherDetails(locationKey, cityName)
        }

        btnAddToFavorites.setOnClickListener {
            val key = currentSearchedKey
            val name = currentSearchedName
            if (key != null && name != null) {
                val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
                val savedList = sharedPrefs.getString("saved_favorites_list", "306633:Johannesburg,305448:Cape Town,305607:Durban") ?: ""
                
                if (!savedList.contains(key)) {
                    val updatedList = if (savedList.isEmpty()) "$key:$name" else "$savedList,$key:$name"
                    sharedPrefs.edit().putString("saved_favorites_list", updatedList).apply()
                    Toast.makeText(this, "$name added to Saved Locations list!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "$name is already in your Saved Locations list!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please search and select a location first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLocationSearch(query: String) {
        val layoutEmptyState = findViewById<LinearLayout>(R.id.layoutEmptyState)
        val layoutSearchResults = findViewById<LinearLayout>(R.id.layoutSearchResults)
        val txtSearchResultName = findViewById<TextView>(R.id.txtSearchResultName)
        val itemSearchResult = findViewById<LinearLayout>(R.id.itemSearchResult)

        WeatherServiceClient.api.searchLocation(API_KEY, query).enqueue(object : Callback<List<AccuLocation>> {
            override fun onResponse(call: Call<List<AccuLocation>>, response: Response<List<AccuLocation>>) {
                val locations = response.body()
                if (response.isSuccessful && !locations.isNullOrEmpty()) {
                    val firstLocation = locations[0]
                    txtSearchResultName.text = "${firstLocation.name}, ${firstLocation.country.name}"
                    itemSearchResult.tag = firstLocation.key
                    
                    layoutEmptyState.visibility = View.GONE
                    layoutSearchResults.visibility = View.VISIBLE
                    findViewById<LinearLayout>(R.id.layoutWeatherDetails).visibility = View.GONE
                } else {
                    Toast.makeText(this@SearchScreen, "No locations found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AccuLocation>>, t: Throwable) {
                Toast.makeText(this@SearchScreen, "Search failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchWeatherDetails(locationKey: String, cityName: String) {
        val layoutSearchResults = findViewById<LinearLayout>(R.id.layoutSearchResults)
        val layoutWeatherDetails = findViewById<LinearLayout>(R.id.layoutWeatherDetails)
        val txtCurrentTemp = findViewById<TextView>(R.id.txtCurrentTemp)
        val txtConditionText = findViewById<TextView>(R.id.txtCondition)
        val layoutHourlyList = findViewById<LinearLayout>(R.id.layoutHourlyList)
        val layoutDailyList = findViewById<LinearLayout>(R.id.layoutDailyList)

        WeatherServiceClient.api.getCurrentConditions(locationKey, API_KEY).enqueue(object : Callback<List<CurrentCondition>> {
            override fun onResponse(call: Call<List<CurrentCondition>>, response: Response<List<CurrentCondition>>) {
                val conditions = response.body()
                if (response.isSuccessful && !conditions.isNullOrEmpty()) {
                    val current = conditions[0]
                    txtCurrentTemp.text = "${current.temperature.metric.value.toInt()}°C"
                    txtConditionText.text = current.weatherText
                    
                    layoutSearchResults.visibility = View.GONE
                    layoutWeatherDetails.visibility = View.VISIBLE
                    
                    fetchSearchedForecasts(locationKey, layoutHourlyList, layoutDailyList)
                }
            }
            override fun onFailure(call: Call<List<CurrentCondition>>, t: Throwable) {}
        })
    }

    private fun fetchSearchedForecasts(locationKey: String, hourlyContainer: LinearLayout, dailyContainer: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        
        WeatherServiceClient.api.getHourlyForecast(locationKey, API_KEY).enqueue(object : Callback<List<HourlyForecast>> {
            override fun onResponse(call: Call<List<HourlyForecast>>, response: Response<List<HourlyForecast>>) {
                if (response.isSuccessful && response.body() != null) {
                    hourlyContainer.removeAllViews()
                    for (forecast in response.body()!!) {
                        val itemView = inflater.inflate(R.layout.item_hourly_forecast, hourlyContainer, false)
                        itemView.findViewById<TextView>(R.id.txtHourlyTime).text = forecast.dateTime.substring(11, 16)
                        itemView.findViewById<TextView>(R.id.txtHourlyTemp).text = "${forecast.temperature.value.toInt()}°"
                        hourlyContainer.addView(itemView)
                    }
                }
            }
            override fun onFailure(call: Call<List<HourlyForecast>>, t: Throwable) {}
        })

        WeatherServiceClient.api.getDailyForecast(locationKey, API_KEY).enqueue(object : Callback<DailyForecastResponse> {
            override fun onResponse(call: Call<DailyForecastResponse>, response: Response<DailyForecastResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    dailyContainer.removeAllViews()
                    val daysMapping = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    for ((index, forecast) in response.body()!!.forecasts.withIndex()) {
                        val itemView = inflater.inflate(R.layout.item_daily_forecast, dailyContainer, false)
                        val dayLabel = try {
                            val javaDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(forecast.date.substring(0, 10))
                            java.text.SimpleDateFormat("EEEE").format(javaDate)
                        } catch (e: Exception) {
                            "Day ${index + 1}"
                        }
                        itemView.findViewById<TextView>(R.id.txtDayName).text = dayLabel
                        itemView.findViewById<TextView>(R.id.txtDayHigh).text = "${forecast.temperature.max.value.toInt()}°"
                        itemView.findViewById<TextView>(R.id.txtDayLow).text = "${forecast.temperature.min.value.toInt()}°"
                        dailyContainer.addView(itemView)
                    }
                }
            }
            override fun onFailure(call: Call<DailyForecastResponse>, t: Throwable) {}
        })
    }
}