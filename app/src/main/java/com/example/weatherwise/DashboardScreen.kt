package com.example.weatherwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Main dashboard screen displaying real-time weather, navigation, and saved favorite locations.
 */
class DashboardScreen : AppCompatActivity() {

    private val API_KEY = "zpka_8d09c7fe1d8a49c3a0bd5c87de1a18e3_5e9eabd2"
    
    private val hourlyFragment = HourlyForecastFragment()
    private val dailyFragment = DailyForecastFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupHeaderMenu()
        setupBottomNavigation()
        
        findViewById<MaterialButton>(R.id.btnGamificationIcon).setOnClickListener {
            startActivity(Intent(this, GamificationActivity::class.java))
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.hourlyContainer, hourlyFragment)
            .replace(R.id.dailyContainer, dailyFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        loadDefaultLocationWeather()
        loadSavedLocationsGrid()
    }

    private fun loadDefaultLocationWeather() {
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val defaultKey = sharedPrefs.getString("default_location_key", "306633") ?: "306633"
        val defaultName = sharedPrefs.getString("default_location_name", "Johannesburg") ?: "Johannesburg"

        val txtLocation = findViewById<TextView>(R.id.txtLocation)
        val txtTemperature = findViewById<TextView>(R.id.txtTemperature)
        val txtFeelsLike = findViewById<TextView>(R.id.txtFeelsLike)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)

        txtLocation.text = defaultName

        WeatherServiceClient.api.getCurrentConditions(defaultKey, API_KEY)
            .enqueue(object : Callback<List<CurrentCondition>> {
                override fun onResponse(call: Call<List<CurrentCondition>>, response: Response<List<CurrentCondition>>) {
                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        val current = response.body()!![0]
                        txtTemperature.text = "${current.temperature.metric.value.toInt()}°C"
                        txtCondition.text = current.weatherText
                        txtFeelsLike.text = "Feels like ${current.realFeel.metric.value.toInt()}°C"
                    }
                }
                override fun onFailure(call: Call<List<CurrentCondition>>, t: Throwable) {}
            })
    }

    private fun loadSavedLocationsGrid() {
        val layoutSavedLocations = findViewById<LinearLayout>(R.id.layoutSavedLocations)
        layoutSavedLocations.removeAllViews()

        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val savedCitiesString = sharedPrefs.getString("saved_favorites_list", "306633:Johannesburg,305448:Cape Town,305607:Durban") ?: ""
        
        val savedCities = mutableListOf<Pair<String, String>>()
        if (savedCitiesString.isNotEmpty()) {
            savedCitiesString.split(",").forEach {
                val parts = it.split(":")
                if (parts.size == 2) {
                    savedCities.add(Pair(parts[0], parts[1]))
                }
            }
        }

        val inflater = LayoutInflater.from(this)
        for (city in savedCities) {
            val cardView = inflater.inflate(R.layout.item_hourly_forecast, layoutSavedLocations, false)
            val txtName = cardView.findViewById<TextView>(R.id.txtHourlyTime)
            val txtTemp = cardView.findViewById<TextView>(R.id.txtHourlyTemp)
            
            txtName.text = city.second
            txtTemp.text = "--°"
            
            WeatherServiceClient.api.getCurrentConditions(city.first, API_KEY)
                .enqueue(object : Callback<List<CurrentCondition>> {
                    override fun onResponse(call: Call<List<CurrentCondition>>, response: Response<List<CurrentCondition>>) {
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            txtTemp.text = "${response.body()!![0].temperature.metric.value.toInt()}°C"
                        }
                    }
                    override fun onFailure(call: Call<List<CurrentCondition>>, t: Throwable) {}
                })
            
            cardView.setOnClickListener {
                with(sharedPrefs.edit()) {
                    putString("default_location_key", city.first)
                    putString("default_location_name", city.second)
                    apply()
                }
                loadDefaultLocationWeather()
                
                // Triggers sub fragment weather synchronizations seamlessly
                // Fixed: using supportFragmentManager inside Activity context instead of parentFragmentManager
                if (hourlyFragment.isAdded) {
                    supportFragmentManager.beginTransaction().detach(hourlyFragment).attach(hourlyFragment).commit()
                }
                if (dailyFragment.isAdded) {
                    dailyFragment.updateLocation(city.first)
                }
            }
            
            layoutSavedLocations.addView(cardView)
        }
    }

    private fun setupHeaderMenu() {
        val imgDropdown = findViewById<ImageView>(R.id.imgDropdown)
        imgDropdown.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.dashboard_dropdown, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile -> {
                        startActivity(Intent(this, ProfileScreen::class.java))
                        true
                    }
                    R.id.action_settings -> {
                        startActivity(Intent(this, SettingsScreen::class.java))
                        true
                    }
                    R.id.action_logout -> {
                        val intent = Intent(this, LoginScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchScreen::class.java))
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationsScreen::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileScreen::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsScreen::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
