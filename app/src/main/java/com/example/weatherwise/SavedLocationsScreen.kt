package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SavedLocationsScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saved_locations_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        populateSavedLocations()
    }

    private fun populateSavedLocations() {
        val container = findViewById<LinearLayout>(R.id.layoutSavedLocationsList)
        container.removeAllViews()

        val savedLocations = LocationPrefs.getSavedLocations(this)
        val defaultKey = LocationPrefs.getDefaultLocationKey(this)

        if (savedLocations.isEmpty()) {
            val emptyTxt = TextView(this).apply {
                text = "No saved locations yet."
                setTextColor(resources.getColor(R.color.white, null))
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            container.addView(emptyTxt)
            return
        }

        for (loc in savedLocations) {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 24, 16, 24)
                val bgDrawable = resources.getDrawable(R.drawable.edittext_bg, null)
                background = bgDrawable
                backgroundTintList = android.content.res.ColorStateList.valueOf(0x26FFFFFF)
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 16)
                layoutParams = params
            }

            val textLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val nameTxt = TextView(this).apply {
                text = if (loc.key == defaultKey) "${loc.name} (Default)" else loc.name
                setTextColor(resources.getColor(R.color.white, null))
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val countryTxt = TextView(this).apply {
                text = loc.country
                setTextColor(0xB3FFFFFF.toInt())
                textSize = 14f
            }

            textLayout.addView(nameTxt)
            textLayout.addView(countryTxt)
            itemLayout.addView(textLayout)

            itemLayout.setOnClickListener {
                LocationPrefs.setDefaultLocation(this, loc.key, loc.name)
                Toast.makeText(this, "${loc.name} set as default location!", Toast.LENGTH_SHORT).show()
                finish()
            }

            val btnDelete = Button(this).apply {
                text = "Delete"
                setTextColor(resources.getColor(R.color.white, null))
                backgroundTintList = android.content.res.ColorStateList.valueOf(0x99FF5252.toInt())
                setOnClickListener {
                    LocationPrefs.deleteLocation(this@SavedLocationsScreen, loc.key)
                    Toast.makeText(this@SavedLocationsScreen, "${loc.name} deleted.", Toast.LENGTH_SHORT).show()
                    populateSavedLocations()
                }
            }

            itemLayout.addView(btnDelete)
            container.addView(itemLayout)
        }
    }
}
