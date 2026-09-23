package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_screen)
        
        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Login Button and set click listener to redirect to DashboardScreen
        val loginBtn = findViewById<MaterialButton>(R.id.btnLogin)
        loginBtn.setOnClickListener {
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
            finish() // Optional: Finish LoginScreen so user can't go back to it
        }

        // Initialize Register Redirect text and set click listener to redirect to RegisterScreen
        val registerRedirect = findViewById<TextView>(R.id.txtRegisterRedirect)
        registerRedirect.setOnClickListener {
            val intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)
        }

        // Initialize Forgot Password Redirect text (Logic can be added later)
        val forgotPasswordRedirect = findViewById<TextView>(R.id.txtForgotPasswordRedirect)
        forgotPasswordRedirect.setOnClickListener {
            // Add forgot password logic here
        }
    }
}