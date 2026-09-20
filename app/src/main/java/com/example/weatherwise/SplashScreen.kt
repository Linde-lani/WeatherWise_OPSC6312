package com.example.weatherwise



import android.content.Intent
//
import android.os.Handler
import android.os.Looper
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay for 2 seconds before navigating
//        Handler(Looper.getMainLooper()).postDelayed({
//            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
//            if (user != null) {
//                // User is logged in → go to Dashboard
//                startActivity(Intent(this, DashboardScreen::class.java))
//            } else {
//                // No user → go to Welcome
//                startActivity(Intent(this, WelcomeScreen::class.java))
//            }
//            finish() // Close SplashActivity so user can’t return to it
//        }, 2000) // 2000 ms = 2 seconds

        // Delay for 2 seconds, then go straight to WelcomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, WelcomeScreen::class.java))
            finish() // Close SplashActivity so user can’t return to it
        }, 2000) // 2000 ms = 2 seconds


    }
}