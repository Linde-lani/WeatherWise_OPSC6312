package com.example.weatherwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Screen allowing users to configure app preferences like Theme and Language.
 */
class SettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_screen)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Dark Theme Switch Function
        val switchDarkTheme = findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        switchDarkTheme.isChecked = isDarkMode

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark theme enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light theme enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Language Buttons Functions
        findViewById<Button>(R.id.btnLangSesotho).setOnClickListener {
            sharedPrefs.edit().putString("app_lang", "st").apply()
            Toast.makeText(this, "Language switched to Sesotho (Kopo e amohetswe)", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnLangZulu).setOnClickListener {
            sharedPrefs.edit().putString("app_lang", "zu").apply()
            Toast.makeText(this, "Language switched to isiZulu (Isicelo samukelwe)", Toast.LENGTH_LONG).show()
        }

        // Initialize Bottom Navigation and handle redirection
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_settings
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardScreen::class.java))
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchScreen::class.java))
                    finish()
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationsScreen::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileScreen::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
