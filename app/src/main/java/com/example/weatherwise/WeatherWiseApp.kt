package com.example.weatherwise

import android.app.Application
import android.util.Log
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.Executors

/**
 * Custom Application class to initialize global configurations like theme and auto-sunset dark mode.
 */
class WeatherWiseApp : Application() {

    companion object {
        private const val TAG = "WeatherWiseApp"
    }

    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applyTheme(this)

        // Enable Firebase Cloud Messaging auto-initialization in a background thread to avoid blocking main thread
        Executors.newSingleThreadExecutor().execute {
            try {
                FirebaseMessaging.getInstance().isAutoInitEnabled = true

                // Fetch and log FCM Registration Token for Firebase Console test messages
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "==================================================")
                        Log.d(TAG, "FCM REGISTRATION TOKEN (USE THIS IN FIREBASE CONSOLE):")
                        Log.d(TAG, task.result ?: "")
                        Log.d(TAG, "==================================================")
                    } else {
                        Log.e(TAG, "Failed to get FCM Registration Token", task.exception)
                    }
                }

                // Fetch and log Firebase Installation ID (FID)
                FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "FIREBASE INSTALLATION ID (FID): ${task.result}")
                    } else {
                        Log.e(TAG, "Failed to get Firebase Installation ID", task.exception)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "FirebaseMessaging initialization exception", e)
            }
        }
    }
}
