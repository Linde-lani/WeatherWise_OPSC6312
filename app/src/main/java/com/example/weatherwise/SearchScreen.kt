package com.example.weatherwise

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchScreen : AppCompatActivity() {

    private val API_KEY = "zpka_7a60f44cc3034119b10f89e87e5a37ea_af868641"
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
        val textInputLayout = edtSearchQuery.parent.parent as? TextInputLayout
        val txtSelectedLocation = findViewById<TextView>(R.id.txtSelectedLocation)
        val btnAddToFavorites = findViewById<MaterialButton>(R.id.btnAddToFavorites)

        btnBack.setOnClickListener { finish() }

        fun triggerSearch() {
            val query = edtSearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                hideKeyboard()
                performLocationSearch(query)
            } else {
                Toast.makeText(this, "Please enter a city name to search", Toast.LENGTH_SHORT).show()
            }
        }

        edtSearchQuery.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                triggerSearch()
                true
            } else {
                false
            }
        }

        textInputLayout?.setStartIconOnClickListener {
            triggerSearch()
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

    private fun hideKeyboard() {
        val view = currentFocus ?: findViewById(R.id.main)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun performLocationSearch(query: String) {
        val layoutEmptyState = findViewById<LinearLayout>(R.id.layoutEmptyState)
        val layoutSearchResults = findViewById<LinearLayout>(R.id.layoutSearchResults)
        val layoutWeatherDetails = findViewById<LinearLayout>(R.id.layoutWeatherDetails)

        WeatherServiceClient.api.searchLocation(API_KEY, query).enqueue(object : Callback<List<AccuLocation>> {
            override fun onResponse(call: Call<List<AccuLocation>>, response: Response<List<AccuLocation>>) {
                val locations = response.body()
                if (response.isSuccessful && !locations.isNullOrEmpty()) {
                    layoutEmptyState.visibility = View.GONE
                    layoutWeatherDetails.visibility = View.GONE
                    layoutSearchResults.visibility = View.VISIBLE
                    layoutSearchResults.removeAllViews()

                    val inflater = LayoutInflater.from(this@SearchScreen)

                    for (loc in locations) {
                        val itemView = inflater.inflate(R.layout.item_notification, layoutSearchResults, false)
                        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
                        val txtMessage = itemView.findViewById<TextView>(R.id.txtMessage)
                        val txtTime = itemView.findViewById<TextView>(R.id.txtTime)
                        val imgIcon = itemView.findViewById<ImageView>(R.id.imgType)

                        txtTitle.text = loc.name
                        txtMessage.text = loc.country.name
                        txtTime.text = loc.key
                        imgIcon.setImageResource(R.drawable.ic_cloud)

                        itemView.setOnClickListener {
                            currentSearchedKey = loc.key
                            currentSearchedName = loc.name
                            findViewById<TextView>(R.id.txtSelectedLocation).text = loc.name
                            fetchWeatherDetails(loc.key, loc.name)
                        }

                        layoutSearchResults.addView(itemView)
                    }
                } else {
                    val msg = if (response.code() == 401 || response.code() == 403) {
                        "API limit or key invalid. Try again later."
                    } else {
                        "No locations found for '$query'."
                    }
                    Toast.makeText(this@SearchScreen, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AccuLocation>>, t: Throwable) {
                Toast.makeText(this@SearchScreen, "Search failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyBackground(findViewById(R.id.main), ThemeUtils.isDarkModeActive(this))
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
                        val isNight = !forecast.isDayTime || try {
                            val hour = forecast.dateTime.substring(11, 13).toInt()
                            hour >= 18 || hour < 6
                        } catch (e: Exception) {
                            false
                        }
                        itemView.findViewById<ImageView>(R.id.imgHourlyIcon).setImageResource(WeatherIconMapper.getWeatherIcon(forecast.weatherIcon, forecast.iconPhrase, isNight))
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
                        itemView.findViewById<ImageView>(R.id.imgDayIcon).setImageResource(WeatherIconMapper.getWeatherIcon(forecast.day.icon, forecast.day.phrase, false))
                        dailyContainer.addView(itemView)
                    }
                }
            }
            override fun onFailure(call: Call<DailyForecastResponse>, t: Throwable) {}
        })
    }
}