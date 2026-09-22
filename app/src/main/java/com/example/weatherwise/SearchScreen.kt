package com.example.weatherwise

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 1. Initialize UI components
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val edtSearchQuery = findViewById<TextInputEditText>(R.id.edtSearchQuery)
        val layoutSearchResults = findViewById<LinearLayout>(R.id.layoutSearchResults)
        val itemSearchResult = findViewById<LinearLayout>(R.id.itemSearchResult)
        val txtSearchResultName = findViewById<TextView>(R.id.txtSearchResultName)
        val layoutWeatherDetails = findViewById<LinearLayout>(R.id.layoutWeatherDetails)
        val layoutEmptyState = findViewById<LinearLayout>(R.id.layoutEmptyState)
        val txtSelectedLocation = findViewById<TextView>(R.id.txtSelectedLocation)
        val btnAddToFavorites = findViewById<MaterialButton>(R.id.btnAddToFavorites)

        // UI Detail Fields
        val txtCurrentTemp = findViewById<TextView>(R.id.txtCurrentTemp)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)

        // 2. Setup Back Button
        btnBack.setOnClickListener { finish() }

        // 3. Setup Search Action (when user presses Enter/Search on keyboard)
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

        // 4. Setup Result Selection logic
        itemSearchResult.setOnClickListener {
            val locationKey = itemSearchResult.tag as? String ?: return@setOnClickListener
            val cityName = txtSearchResultName.text.toString()

            // Update Headers
            txtSelectedLocation.text = cityName

            // Fetch detailed weather for the selected location key
            fetchWeatherDetails(locationKey, cityName)
        }

        // 5. Setup Favorites button
        btnAddToFavorites.setOnClickListener {
            val city = txtSelectedLocation.text.toString()
            Toast.makeText(this, "$city added to Favorites!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Calls AccuWeather API to find locations matching the user's query.
     */
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
                    itemSearchResult.tag = firstLocation.key // Store the key for weather lookup

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

    /**
     * Fetches current conditions and recommendations for the selected city.
     */
    private fun fetchWeatherDetails(locationKey: String, cityName: String) {
        val layoutSearchResults = findViewById<LinearLayout>(R.id.layoutSearchResults)
        val layoutWeatherDetails = findViewById<LinearLayout>(R.id.layoutWeatherDetails)
        val txtCurrentTemp = findViewById<TextView>(R.id.txtCurrentTemp)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)

        WeatherServiceClient.api.getCurrentConditions(locationKey, API_KEY).enqueue(object : Callback<List<CurrentCondition>> {
            override fun onResponse(call: Call<List<CurrentCondition>>, response: Response<List<CurrentCondition>>) {
                val conditions = response.body()
                if (response.isSuccessful && !conditions.isNullOrEmpty()) {
                    val current = conditions[0]

                    // Update UI with real data
                    txtCurrentTemp.text = "${current.temperature.metric.value.toInt()}°C"
                    txtCondition.text = current.weatherText

                    // Toggle Views
                    layoutSearchResults.visibility = View.GONE
                    layoutWeatherDetails.visibility = View.VISIBLE

                    // Fetch Forecasts as well (Optional: can be expanded)
                    Toast.makeText(this@SearchScreen, "Updated weather for $cityName", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CurrentCondition>>, t: Throwable) {
                Toast.makeText(this@SearchScreen, "Failed to load weather: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}