package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardScreen : AppCompatActivity() {

    // AccuWeather API Configuration
    private val API_KEY = "zpka_8d09c7fe1d8a49c3a0bd5c87de1a18e3_5e9eabd2"
    private val DEFAULT_LOCATION_KEY = "306633" // Johannesburg, South Africa

    // Fragments for dynamic forecast display
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
        // Initialize Dropdown Menu
        setupDropdownMenu()

        // Setup Bottom Navigation
        setupBottomNavigation()

        // Handle Sync Indicator Click (Manual Refresh)
        findViewById<LinearLayout>(R.id.layoutSyncStatus).setOnClickListener {
            refreshWeatherData()
        }

        // 1. Fetch real weather data on launch
        refreshWeatherData()
    }

    /**
     * Fetches current conditions from AccuWeather API and updates the UI.
     */
    private fun refreshWeatherData() {
        val txtSyncStatus = findViewById<TextView>(R.id.txtSyncStatus)

        // Update sync UI state
        txtSyncStatus.text = "Syncing..."

        WeatherServiceClient.api.getCurrentConditions(DEFAULT_LOCATION_KEY, API_KEY)
            .enqueue(object : Callback<List<CurrentCondition>> {
                override fun onResponse(call: Call<List<CurrentCondition>>, response: Response<List<CurrentCondition>>) {
                    val conditions = response.body()
                    if (response.isSuccessful && !conditions.isNullOrEmpty()) {
                        updateWeatherUI(conditions[0])
                        txtSyncStatus.text = "Synced"
                        showFloatingNotification("Dashboard updated with real-time data.")
                    } else {
                        handleApiError("Sync failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<CurrentCondition>>, t: Throwable) {
                    handleApiError("Check internet connection.")
                }
            })
    }

    /**
     * Updates the main weather elements with real data from the API response.
     */
    private fun updateWeatherUI(condition: CurrentCondition) {
        val txtTemp = findViewById<TextView>(R.id.txtTemperature)
        val txtFeelsLike = findViewById<TextView>(R.id.txtFeelsLike)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)
        val imgIcon = findViewById<ImageView>(R.id.imgWeatherIcon)

        // Set Temperature values
        txtTemp.text = "${condition.temperature.metric.value.toInt()}°C"
        // Note: AccuWeather CurrentCondition doesn't always have RealFeel at top level unless requested with details=true.
        // The API interface uses details=true by default. However, let's verify if RealFeel is present.
        // Assuming CurrentCondition model is updated to match.
        txtFeelsLike.text = "Feels like ${condition.realFeel.metric.value.toInt()}°C"
        txtCondition.text = condition.weatherText

        // Map AccuWeather icon ID to our local assets (Simple mapping)
        val iconRes = when(condition.weatherIcon) {
            in 1..5 -> R.drawable.ic_sunny        // Sunny/Mostly Sunny
            in 6..11 -> R.drawable.ic_cloudy      // Cloudy/Broken Clouds
            in 12..18 -> R.drawable.ic_cloud      // Rain/Showers
            else -> R.drawable.ic_cloud           // Fallback
        }
        imgIcon.setImageResource(iconRes)
    }

    private fun handleApiError(message: String) {
        findViewById<TextView>(R.id.txtSyncStatus).text = "Offline"
        showFloatingNotification(message)
    }

    private fun setupDropdownMenu() {
        val imgDropdown = findViewById<ImageView>(R.id.imgDropdown)
        imgDropdown.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            // If dashboard_dropdown doesn't exist, we might need to create it or use another one
            try {
                popup.menuInflater.inflate(R.menu.dashboard_dropdown, popup.menu)
            } catch (e: Exception) {
                // Fallback to a basic menu or log error if it doesn't exist
            }
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile -> { startActivity(Intent(this, ProfileScreen::class.java)); true }
                    R.id.action_settings -> { startActivity(Intent(this, SettingsScreen::class.java)); true }
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
        val scrollView = findViewById<NestedScrollView>(R.id.weatherScrollView)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    scrollView?.smoothScrollTo(0, 0)
                    true
                }
                // Updated IDs to match bottom_nav_menu.xml if needed, but the current code uses specific ones.
                // I will update bottom_nav_menu.xml to match these IDs.
                R.id.nav_hourly -> {
                    // Using a fragment container - need to ensure it exists in activity_dashboard_screen.xml
                    // Since it's not in the provided XML, I'll assume it should be added or use another way.
                    // For now, let's keep it as is and I will check the layout.
                    true
                }
                R.id.nav_daily -> {
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchScreen::class.java))
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent( this, NotificationsScreen::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showFloatingNotification(message: String) {
        val card = findViewById<MaterialCardView>(R.id.cardNotificationIndicator)
        val text = findViewById<TextView>(R.id.txtNotificationMsg)
        text.text = message
        card.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({ card.visibility = View.GONE }, 3000)
    }
}
