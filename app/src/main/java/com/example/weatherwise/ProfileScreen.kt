package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class ProfileScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize back button
        val btnBack = findViewById<ImageView>(R.id.imgDropdown) // Currently using the dropdown as a placeholder icon in your layout
        // Note: You added a TextView "My Profile" but didn't explicitly add a back button in the XML snippet provided.
        // I will update the XML to include a proper back button.

        // Initialize Edit Profile Button
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            // Redirect to the Edit Profile screen
            val intent = Intent(this, EditProfileScreen::class.java)
            startActivity(intent)
        }
    }
}