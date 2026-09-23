package com.example.weatherwise

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Screen allowing users to edit their profile details.
 * Changes are saved locally using SharedPreferences and synchronized with the database.
 */
class EditProfileScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_screen)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val fabChangePhoto = findViewById<FloatingActionButton>(R.id.fabChangePhoto)
        val edtUsername = findViewById<TextInputEditText>(R.id.edtUsername)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)

        // Load existing data
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val currentUsername = sharedPrefs.getString("username", "WeatherWise User")
        val userEmail = sharedPrefs.getString("email", "") // Identifier for DB update
        
        edtUsername.setText(currentUsername)

        // Back arrow - returns without saving
        btnBack.setOnClickListener { finish() }

        // Cancel button - returns without saving
        btnCancel.setOnClickListener { finish() }

        // Change Photo placeholder
        fabChangePhoto.setOnClickListener {
            Toast.makeText(this, "Profile picture selection opened", Toast.LENGTH_SHORT).show()
        }

        // Save button - persists changes and returns
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString()
            val newPassword = edtPassword.text.toString()

            if (newUsername.isNotEmpty()) {
                // Perform database update in background thread
                thread {
                    val rowData = mutableMapOf(
                        "username" to newUsername,
                        "email" to userEmail // Include email to identify the record
                    )
                    if (newPassword.isNotEmpty()) {
                        rowData["password"] = newPassword
                    }

                    val response = updateRow(
                        tableName = "user_b13fde0d_weather_wise_users",
                        data = rowData
                    )

                    runOnUiThread {
                        if (response != null && response.contains("success", ignoreCase = true)) {
                            // Update local SharedPreferences on success
                            with(sharedPrefs.edit()) {
                                putString("username", newUsername)
                                if (newPassword.isNotEmpty()) putString("password", newPassword)
                                apply()
                            }
                            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Go back to Profile screen
                        } else {
                            // Fallback: update locally even if DB sync fails, but notify user
                            with(sharedPrefs.edit()) {
                                putString("username", newUsername)
                                if (newPassword.isNotEmpty()) putString("password", newPassword)
                                apply()
                            }
                            Toast.makeText(this, "Saved locally. Sync failed: $response", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Database update function mirroring RegisterScreen's insertRow logic.
     * Connects to the you_connect API to update user information.
     */
    private fun updateRow(
        tableName: String,
        data: Map<String, Any?>
    ): String? {
        // Using the 'update' action for the you_connect.php script
        val url = URL("https://studyplugtools.cloud/you_connect.php/$tableName/update")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true

            // Build JSON payload
            val payload = JSONObject()
            for ((key, value) in data) {
                payload.put(key, value ?: "")
            }

            // Send JSON payload
            connection.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            // Read response
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
            response.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }
}
