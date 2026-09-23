package com.example.weatherwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Screen displaying weather alerts and system notifications.
 * Features a 3-second long-press to enter selection mode for multi-delete.
 */
class NotificationsScreen : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>()
    private lateinit var btnDelete: ImageButton
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications_screen)

        // Handle window insets for proper system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnDelete = findViewById(R.id.btnDelete)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Setup Back Button functionality
        btnBack.setOnClickListener {
            if (adapter.isSelectionMode) {
                exitSelectionMode()
            } else {
                finish()
            }
        }

        // Setup Delete Action (Trash icon in header)
        btnDelete.setOnClickListener {
            adapter.deleteSelected()
            updateEmptyState()
            btnDelete.visibility = View.GONE
            Toast.makeText(this, "Selected messages deleted", Toast.LENGTH_SHORT).show()
        }

        // Pre-fill some simulated weather notifications
        loadDummyData()

        // Initialize Adapter with 3-second long press callbacks
        adapter = NotificationAdapter(notificationList, 
            onSelectionStarted = {
                // When 3s hold triggers selection mode
                btnDelete.visibility = View.VISIBLE
            },
            onSelectionChanged = {
                // Handle delete button visibility based on if anything is still selected
                val hasSelection = notificationList.any { it.isSelected }
                if (!hasSelection) exitSelectionMode()
            }
        )

        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        // Setup Bottom Navigation redirection including Profile and Settings
        bottomNav.selectedItemId = R.id.nav_notification
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, DashboardScreen::class.java)); finish(); true }
                R.id.nav_search -> { startActivity(Intent(this, SearchScreen::class.java)); finish(); true }
                R.id.nav_notification -> true // Already here
                R.id.nav_profile -> { startActivity(Intent(this, ProfileScreen::class.java)); finish(); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsScreen::class.java)); finish(); true }
                else -> false
            }
        }

        updateEmptyState()
    }

    private fun loadDummyData() {
        notificationList.add(Notification(1, "Storm Warning", "Severe thunderstorm detected near your area.", "Just now"))
        notificationList.add(Notification(2, "Temperature Alert", "Extreme heat expected between 1 PM and 4 PM.", "2h ago"))
        notificationList.add(Notification(3, "Daily Summary", "Tomorrow will be clear and sunny. Perfect for a hike!", "5h ago"))
        notificationList.add(Notification(4, "System", "Welcome to WeatherWise! Your data is synced.", "Yesterday"))
    }

    private fun exitSelectionMode() {
        adapter.isSelectionMode = false
        notificationList.forEach { it.isSelected = false }
        adapter.notifyDataSetChanged()
        btnDelete.visibility = View.GONE
    }

    private fun updateEmptyState() {
        layoutEmpty.visibility = if (notificationList.isEmpty()) View.VISIBLE else View.GONE
    }
}
