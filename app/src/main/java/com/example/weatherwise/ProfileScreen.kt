package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
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
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Initialize Edit Profile Button
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            // Redirect to the Edit Profile screen
            val intent = Intent(this, EditProfileScreen::class.java)
            startActivity(intent)
        }
    }
}