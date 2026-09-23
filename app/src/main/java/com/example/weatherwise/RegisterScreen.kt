package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class RegisterScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display for a modern look
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_screen)
        
        // Handle window insets for proper padding with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Register Button and set click listener to redirect to Dashboard
        val registerBtn = findViewById<MaterialButton>(R.id.btnRegister)
        registerBtn.setOnClickListener {
            // In a real app, you would add registration logic here
            val intent = Intent(this, DashboardScreen::class.java)
            startActivity(intent)
            finish() // Close RegisterScreen so user can't go back to it
        }

        // Initialize Login Redirect text and set click listener to go back to LoginScreen
        val loginRedirect = findViewById<TextView>(R.id.txtLoginRedirect)
        loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish() // Optional: Finish current activity to clear it from stack
        }

    }
    fun log_in(view: View)
    {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
    }
    fun send_info(view: View)
    {
        //collecting the user's input
        val name = findViewById<EditText>(R.id.edtUsername).text.toString();
        val email = findViewById<EditText>(R.id.edtEmail).text.toString();
        val password = findViewById<EditText>(R.id.edtPassword).text.toString();

        //send the info to the weather_wise_users table
        // -------------------------------
// HOW TO Insert, put this in a function or in the main under the override fun Oncreate
// -------------------------------
        thread {
            val rowData = mapOf(
                "username" to name,
                "email" to email,
                "password" to password,
            )

            val response = insertRow(
                tableName = "user_b13fde0d_weather_wise_users",
                data = rowData
            )

            // Update UI on main thread
            runOnUiThread {
                println(response)
                // or show Toast / TextView
                Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show()
            }
        }



    }//end of send_info event handler
    fun insertRow(
        tableName: String,
        data: Map< String, Any?>
    ): String? {
        val url = URL("https://studyplugtools.cloud/you_connect.php/$tableName/insert")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true

            // Build JSON payload (include nulls as empty strings if needed)
            val payload = JSONObject()
            for ((key, value) in data) {
                if (key != "id") {
                    payload.put(key, value ?: "")
                }
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
        }}
}