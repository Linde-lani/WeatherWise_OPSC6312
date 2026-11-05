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
import androidx.core.os.LocaleListCompat
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

        // Dark Theme Switch Functions
        val switchDarkTheme = findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        val switchAutoSunset = findViewById<SwitchMaterial>(R.id.switchAutoSunset)
        val sharedPrefs = getSharedPreferences(ThemeUtils.PREFS_NAME, Context.MODE_PRIVATE)

        switchDarkTheme.isChecked = sharedPrefs.getBoolean(ThemeUtils.KEY_DARK_MODE, false)
        switchAutoSunset.isChecked = sharedPrefs.getBoolean(ThemeUtils.KEY_AUTO_SUNSET, true)

        ThemeUtils.applyBackground(findViewById(R.id.main), ThemeUtils.isDarkModeActive(this))

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(ThemeUtils.KEY_DARK_MODE, isChecked).apply()
            ThemeUtils.applyTheme(this)
            val statusText = if (isChecked) getString(R.string.dark_theme_enabled) else getString(R.string.light_theme_enabled)
            Toast.makeText(this, statusText, Toast.LENGTH_SHORT).show()
            recreate()
        }

        switchAutoSunset.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean(ThemeUtils.KEY_AUTO_SUNSET, isChecked).apply()
            ThemeUtils.applyTheme(this)
            val statusText = if (isChecked) getString(R.string.auto_sunset_enabled) else getString(R.string.auto_sunset_disabled)
            Toast.makeText(this, statusText, Toast.LENGTH_SHORT).show()
            recreate()
        }

        // Language Buttons: English, Sesotho, and isiZulu
        findViewById<Button>(R.id.btnLangEnglish).setOnClickListener {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
            sharedPrefs.edit().putString("app_lang", "en").apply()
            Toast.makeText(this, getString(R.string.lang_switched_english), Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnLangSesotho).setOnClickListener {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("st"))
            sharedPrefs.edit().putString("app_lang", "st").apply()
            Toast.makeText(this, getString(R.string.lang_switched_sesotho), Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnLangZulu).setOnClickListener {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("zu"))
            sharedPrefs.edit().putString("app_lang", "zu").apply()
            Toast.makeText(this, getString(R.string.lang_switched_zulu), Toast.LENGTH_LONG).show()
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
                R.id.nav_game -> {
                    startActivity(Intent(this, GamificationScreen::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
