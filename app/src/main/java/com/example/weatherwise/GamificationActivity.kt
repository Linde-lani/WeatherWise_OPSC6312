package com.example.weatherwise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Modern Gamification Activity tracking XP levels, daily streaks, quests, badges, quizzes, and leaderboards.
 */
class GamificationActivity : AppCompatActivity() {

    private lateinit var txtScore: TextView
    private lateinit var txtLevelTitle: TextView
    private lateinit var txtLevelProgressInfo: TextView
    private lateinit var progressLevel: ProgressBar

    private lateinit var txtStreakTitle: TextView
    private lateinit var txtStreakInfo: TextView

    private lateinit var btnQuizOption1: Button
    private lateinit var btnQuizOption2: Button
    private lateinit var btnQuizOption3: Button
    private lateinit var txtQuizResult: TextView

    private lateinit var cardBadgeFiveLocations: MaterialCardView
    private lateinit var cardBadgeFirstStep: MaterialCardView
    private lateinit var cardBadgeSunChaser: MaterialCardView

    private lateinit var txtQuest1Status: TextView
    private lateinit var txtQuest2Status: TextView

    private lateinit var txtLeaderboardUser: TextView
    private lateinit var txtLeaderboardUserXP: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gamification_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.navBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Initialize UI Views
        txtScore = findViewById(R.id.txtGamificationScore)
        txtLevelTitle = findViewById(R.id.txtLevelTitle)
        txtLevelProgressInfo = findViewById(R.id.txtLevelProgressInfo)
        progressLevel = findViewById(R.id.progressLevel)

        txtStreakTitle = findViewById(R.id.txtStreakTitle)
        txtStreakInfo = findViewById(R.id.txtStreakInfo)

        btnQuizOption1 = findViewById(R.id.btnQuizOption1)
        btnQuizOption2 = findViewById(R.id.btnQuizOption2)
        btnQuizOption3 = findViewById(R.id.btnQuizOption3)
        txtQuizResult = findViewById(R.id.txtQuizResult)

        cardBadgeFiveLocations = findViewById(R.id.cardBadgeFiveLocations)
        cardBadgeFirstStep = findViewById(R.id.cardBadgeFirstStep)
        cardBadgeSunChaser = findViewById(R.id.cardBadgeSunChaser)

        txtQuest1Status = findViewById(R.id.txtQuest1Status)
        txtQuest2Status = findViewById(R.id.txtQuest2Status)

        txtLeaderboardUser = findViewById(R.id.txtLeaderboardUser)
        txtLeaderboardUserXP = findViewById(R.id.txtLeaderboardUserXP)

        setupDailyStreak()
        setupDailyQuiz()
        updateGamificationData()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, DashboardScreen::class.java)); finish(); true }
                R.id.nav_search -> { startActivity(Intent(this, SearchScreen::class.java)); finish(); true }
                R.id.nav_notification -> { startActivity(Intent(this, NotificationsScreen::class.java)); finish(); true }
                R.id.nav_game -> {
                    true // Already here
                }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileScreen::class.java)); finish(); true }

                else -> false
            }
        }
    }

    private fun setupDailyStreak() {
        val prefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = prefs.getString("last_streak_date", "") ?: ""
        var streak = prefs.getInt("streak_count", 1)

        if (lastDate.isEmpty()) {
            streak = 1
            prefs.edit().putString("last_streak_date", todayStr).putInt("streak_count", 1).apply()
        } else if (lastDate != todayStr) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val lastDateObj = sdf.parse(lastDate)
                val cal = Calendar.getInstance()
                cal.time = Date()
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterdayStr = sdf.format(cal.time)

                if (lastDate == yesterdayStr) {
                    streak += 1
                } else {
                    streak = 1
                }
                prefs.edit().putString("last_streak_date", todayStr).putInt("streak_count", streak).apply()
            } catch (e: Exception) {
                streak = 1
            }
        }

        txtStreakTitle.text = "$streak-Day Weather Streak 🔥"
        txtStreakInfo.text = if (streak > 1) "Awesome! Keep opening WeatherWise daily to build your streak!" else "Check in daily to build your streak and earn extra XP!"
    }

    private fun setupDailyQuiz() {
        val prefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val quizAnsweredDate = prefs.getString("quiz_answered_date", "") ?: ""

        if (quizAnsweredDate == todayStr) {
            disableQuizButtons("Completed for today! (+25 XP earned)")
            return
        }

        btnQuizOption1.setOnClickListener {
            // Option 1: Cumulonimbus (Correct)
            val currentBonus = prefs.getInt("quiz_bonus_xp", 0) + 25
            prefs.edit().putString("quiz_answered_date", todayStr).putInt("quiz_bonus_xp", currentBonus).apply()
            Toast.makeText(this, "Correct! You earned +25 XP!", Toast.LENGTH_SHORT).show()
            disableQuizButtons("Correct! +25 XP earned!")
            updateGamificationData()
        }

        btnQuizOption2.setOnClickListener {
            Toast.makeText(this, "Incorrect! Cirrus clouds are high wispy clouds.", Toast.LENGTH_SHORT).show()
            prefs.edit().putString("quiz_answered_date", todayStr).apply()
            disableQuizButtons("Incorrect! Try again tomorrow.")
        }

        btnQuizOption3.setOnClickListener {
            Toast.makeText(this, "Incorrect! Stratus clouds produce light drizzle.", Toast.LENGTH_SHORT).show()
            prefs.edit().putString("quiz_answered_date", todayStr).apply()
            disableQuizButtons("Incorrect! Try again tomorrow.")
        }
    }

    private fun disableQuizButtons(message: String) {
        btnQuizOption1.isEnabled = false
        btnQuizOption2.isEnabled = false
        btnQuizOption3.isEnabled = false
        btnQuizOption1.alpha = 0.5f
        btnQuizOption2.alpha = 0.5f
        btnQuizOption3.alpha = 0.5f
        txtQuizResult.visibility = View.VISIBLE
        txtQuizResult.text = message
    }

    private fun updateGamificationData() {
        val prefs = getSharedPreferences("WeatherWisePrefs", Context.MODE_PRIVATE)
        val savedList = prefs.getString("saved_favorites_list", "") ?: ""
        val savedCount = if (savedList.isEmpty()) 0 else savedList.split(",").size
        val streak = prefs.getInt("streak_count", 1)
        val quizBonus = prefs.getInt("quiz_bonus_xp", 0)
        val username = prefs.getString("username", "WeatherWise User") ?: "WeatherWise User"

        // Compute XP
        var totalXP = 50 + (savedCount * 25) + (streak * 10) + quizBonus

        // Quests calculations
        val quest1Completed = savedCount >= 3
        if (quest1Completed) {
            totalXP += 30
            txtQuest1Status.text = "Completed (+30 XP) ✓"
            txtQuest1Status.setTextColor(android.graphics.Color.GREEN)
        } else {
            txtQuest1Status.text = "$savedCount/3 Locations"
            txtQuest1Status.setTextColor(android.graphics.Color.WHITE)
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val quest2Completed = currentHour >= 20 || currentHour < 5
        if (quest2Completed) {
            totalXP += 20
            txtQuest2Status.text = "Completed (+20 XP) ✓"
            txtQuest2Status.setTextColor(android.graphics.Color.GREEN)
        } else {
            txtQuest2Status.text = "Check past 8 PM"
            txtQuest2Status.setTextColor(android.graphics.Color.WHITE)
        }

        txtScore.text = "$totalXP XP"

        // Calculate Level Ranks
        val (levelTitle, maxXP, minXP) = when {
            totalXP < 100 -> Triple("Level 1: Weather Observer", 100, 0)
            totalXP < 250 -> Triple("Level 2: Rain Spotter", 250, 100)
            totalXP < 500 -> Triple("Level 3: Cloud Specialist", 500, 250)
            totalXP < 1000 -> Triple("Level 4: Storm Tracker", 1000, 500)
            else -> Triple("Level 5: WeatherWise Master", totalXP, 1000)
        }

        txtLevelTitle.text = levelTitle
        val currentLevelXP = totalXP - minXP
        val rangeXP = maxOf(1, maxXP - minXP)
        val progress = ((currentLevelXP.toFloat() / rangeXP.toFloat()) * 100).toInt().coerceIn(0, 100)
        progressLevel.progress = progress

        val remainingXP = maxOf(0, maxXP - totalXP)
        txtLevelProgressInfo.text = if (remainingXP > 0) "$remainingXP XP to next Level" else "Max Level Reached! You are a Weather Master!"

        // Badges alpha & highlight
        cardBadgeFirstStep.alpha = 1.0f // Always unlocked when in app
        if (savedCount >= 3) {
            cardBadgeFiveLocations.alpha = 1.0f
        } else {
            cardBadgeFiveLocations.alpha = 0.5f
        }
        cardBadgeSunChaser.alpha = 1.0f

        // Leaderboard
        txtLeaderboardUser.text = "2. You ($username)"
        txtLeaderboardUserXP.text = "$totalXP XP"
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyBackground(findViewById(R.id.main), ThemeUtils.isDarkModeActive(this))
        updateGamificationData()
    }
}
