package com.example.weatherwise

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/**
 * Screen allowing users to edit their profile details including username and password.
 */
class EditProfileScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_screen)

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val edtUsername = findViewById<TextInputEditText>(R.id.edtUsername)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)

        // Setup Back Button functionality
        btnBack.setOnClickListener {
            finish() // Return to the Profile screen
        }

        // Setup Cancel Button functionality
        btnCancel.setOnClickListener {
            finish() // Close screen without saving
        }

        // Setup Save Button functionality
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString()
            // Logic to save changes would be implemented here
            Toast.makeText(this, "Profile updated for $newUsername", Toast.LENGTH_SHORT).show()
            finish() // Return to Profile screen after saving
        }
    }
}