package com.example.weatherwise

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Screen allowing users to edit their profile details and change profile picture.
 * Changes are saved locally using SharedPreferences and synchronized with the database.
 */
class EditProfileScreen : AppCompatActivity() {



    private lateinit var imgProfile: ImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(filesDir, "profile_picture.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putString("profile_image_path", file.absolutePath).apply()
                loadProfileImage()
                Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        val edtConfirmPassword = findViewById<TextInputEditText>(R.id.edtConfirmPassword)
        imgProfile = findViewById(R.id.imgProfile)

        // Load existing profile image
        loadProfileImage()

        // Load existing data
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val currentUsername = sharedPrefs.getString("username", "WeatherWise User")
        val userEmail = sharedPrefs.getString("email", "") // Identifier for DB update
        
        edtUsername.setText(currentUsername)

        // Back arrow - returns without saving
        btnBack.setOnClickListener { finish() }

        // Cancel button - returns without saving
        btnCancel.setOnClickListener { finish() }

        // Change Photo triggers image picker
        fabChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        imgProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Save button - validates password confirmation, persists changes locally & to DB
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "New password and Confirm password do not match!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (newPassword.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Perform database update in background thread
            thread {
                val rowData = mutableMapOf<String, Any>(
                    "username" to newUsername,
                    "email" to (userEmail ?: "") // Include email to identify the record
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
                        Toast.makeText(this, "Profile and password updated successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Go back to Profile screen
                    } else {
                        // Fallback: update locally even if DB sync fails, but notify user
                        with(sharedPrefs.edit()) {
                            putString("username", newUsername)
                            if (newPassword.isNotEmpty()) putString("password", newPassword)
                            apply()
                        }
                        Toast.makeText(this, "Saved locally. Sync response: $response", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun loadProfileImage() {
        val sharedPrefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val imagePath = sharedPrefs.getString("profile_image_path", null)
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    imgProfile.setImageBitmap(bitmap)
                    imgProfile.imageTintList = null
                    return
                }
            }
        }
        imgProfile.setImageResource(R.drawable.ic_profile)
        imgProfile.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
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

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyBackground(findViewById(R.id.main), ThemeUtils.isDarkModeActive(this))
        loadProfileImage()
    }
}
