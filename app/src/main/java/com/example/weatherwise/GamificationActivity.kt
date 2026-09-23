package com.example.weatherwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

/**
 * Activity for Gamification and user badges/achievements tracking.
 */
class GamificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gamification_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Dynamically compute badges based on saved locations list length
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val savedList = sharedPrefs.getString("saved_favorites_list", "") ?: ""
        val count = if (savedList.isEmpty()) 0 else savedList.split(",").size

        val txtScore = findViewById<TextView>(R.id.txtGamificationScore)
        val cardFiveBadge = findViewById<MaterialCardView>(R.id.cardBadgeFiveLocations)

        // Give extra XP depending on location collection
        val totalXP = 100 + (count * 20)
        txtScore.text = "$totalXP XP"

        // visually highlight or show badges if criteria met
        if (count >= 5) {
            cardFiveBadge.alpha = 1.0f
        } else {
            cardFiveBadge.alpha = 0.5f // Dimmed out until unlocked
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, DashboardScreen::class.java)); finish(); true }
                R.id.nav_search -> { startActivity(Intent(this, SearchScreen::class.java)); finish(); true }
                R.id.nav_notification -> { startActivity(Intent(this, NotificationsScreen::class.java)); finish(); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileScreen::class.java)); finish(); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsScreen::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
