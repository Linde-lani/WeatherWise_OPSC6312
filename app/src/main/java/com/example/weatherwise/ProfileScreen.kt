package com.example.weatherwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

/**
 * Screen displaying the user's profile and providing navigation to editing.
 */
class ProfileScreen : AppCompatActivity() {

    private lateinit var txtProfileName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_screen)
        
        // Handle window insets for proper padding with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        txtProfileName = findViewById(R.id.txtProfileName)

        // Setup Back Button functionality
        btnBack.setOnClickListener {
            finish()
        }

        // Setup the top dropdown menu
        val imgDropdown = findViewById<ImageView>(R.id.imgDropdown)
        imgDropdown.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.dashboard_dropdown, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile -> true // Already here
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

        // Initialize Edit Profile Button and redirect to EditProfileScreen
        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileScreen::class.java))
        }

        // Setup Bottom Navigation Bar
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home // Assuming Profile is accessible via Home/Dashboard path
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

    override fun onResume() {
        super.onResume()
        // Refresh profile data from SharedPreferences when returning to this screen
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val savedName = sharedPrefs.getString("username", "WeatherWise User")
        txtProfileName.text = savedName
    }
}
