# 🌤️ WeatherWise

**WeatherWise** is an Android application designed to provide weather forecasts, real-time weather alert notifications, location search, and weather trivia gamification.

---

## 🚀 Key Features

* ☀️ **Current Conditions & Live Forecasts**: View temperature, weather conditions, feels-like temperature, humidity, and wind details powered by the **AccuWeather REST API**.
* ⏰ **Hourly & 5-Day Forecasts**: Access detailed 12-hour hourly forecasts and 5-day weather predictions with interactive list views.
* 🔍 **City Search & Favorite Locations**: Search for cities worldwide and save your favorite locations for quick access.
* 🔔 **Firebase Real-Time Push Notifications**: Integrated with **Firebase Cloud Messaging (FCM)** to deliver urgent weather alerts directly to your device.
* 🎮 **Gamification & Daily Quizzes**: Earn XP, build daily login streaks, unlock achievement badges, and test your weather knowledge with daily trivia.
* 🌙 **Auto-Sunset Dark Mode & Custom Theming**: Adaptive UI background colors (Sky Blue during the day, Navy Blue at night) with automatic time-based dark mode and manual theme toggles.
* 📶 **Offline Cache Support**: Local weather caching via `WeatherCacheManager` ensures critical weather data remains accessible even when offline.
* 👤 **Profile & Customization**: Manage user profiles, update profile pictures, and adjust application settings.

---
One of our flagship ideas in **WeatherWise** is the **Auto-Sunset Dark Mode & Dynamic Background System** implemented in [`ThemeUtils.kt`](file:///C:/Users/asus/Downloads/WeatherWise/app/src/main/java/com/example/weatherwise/ThemeUtils.kt).

 * 🌆 **Automatic Sunset Detection**: The app intelligently checks your local system time (between 18:00 / 6 PM and 06:00 / 6 AM) or live daytime condition APIs to automatically switch the application theme into Dark Mode at sunset without user intervention.
 * 🎨 **Adaptive Background Color Switching**: Dynamically transitions root layout backgrounds between vibrant **Sky Blue (`bg_skyblue.xml`)** during the day and deep **Navy Blue (`bg_navyblue.xml`)** during the night and in Dark Mode.
 * ⚙️ **User Preference Controls**: Located in [`SettingsScreen.kt`](file:///C:/Users/asus/Downloads/WeatherWise/app/src/main/java/com/example/weatherwise/SettingsScreen.kt), users can toggle between manual Dark Theme, Light Theme, or enable/disable Auto-Sunset mode at any time.


## 🛠️ Architecture & Tech Stack

* **Language**: Kotlin
* **UI**: Android XML Layouts, Material Design 3, ViewBinding, Jetpack Compose
* **Network & API**: Retrofit 2, Gson Converter, AccuWeather REST API
* **Cloud & Messaging**: Firebase Cloud Messaging (FCM), Firebase Installations
* **Async & Threading**: Kotlin Coroutines, Background Threading (`Executors`)
* **Persistence & Caching**: SharedPreferences, `WeatherCacheManager`
* **Minimum SDK**: API 24 (Android 7.0 Nougat)
* **Target SDK**: API 34 / 35 (Android 14 / 15)

---

## 📱 Project Structure

```text
com.example.weatherwise
├── AccuWeatherService.kt        # Retrofit interface and API client for AccuWeather
├── WeatherModels.kt             # Data models for weather, forecasts, and locations
├── WeatherWiseApp.kt            # Custom Application class (Theme & FCM initialization)
├── MyFirebaseMessagingService.kt# FCM service for receiving and displaying push notifications
├── DashboardScreen.kt           # Main weather overview dashboard
├── SearchScreen.kt              # City search and favorite location manager
├── DailyForecastFragment.kt     # Fragment for 5-day weather forecast
├── HourlyForecastFragment.kt    # Fragment for 12-hour weather forecast
├── GamificationActivity.kt      # Quiz, login streaks, badges, and XP tracker
├── NotificationsScreen.kt       # Notification center and alert history
├── ProfileScreen.kt             # User profile overview
├── EditProfileScreen.kt         # Profile editing and image update screen
├── SettingsScreen.kt            # App settings (Dark mode, auto-sunset, language)
├── ThemeUtils.kt                # Helper utility for dark mode & auto-sunset theme logic
└── WeatherCacheManager.kt       # Local caching manager for offline data fallback
```

---

## 🔧 Getting Started & Setup

### Prerequisites
* **Android Studio**: Jellyfish (2023.3) or higher.
* **JDK**: Java 11 or higher.
* **AccuWeather API Key**: Obtain a free API key from [AccuWeather Developer Portal](https://developer.accuweather.com/).
* **Firebase Project**: A configured Firebase project with `google-services.json`.

### Building and Running
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/WeatherWise.git
   cd WeatherWise
   ```
2. **Add `google-services.json`**:
   Place your project's `google-services.json` file inside the `app/` directory (`WeatherWise/app/google-services.json`).
3. **Configure AccuWeather API Key**:
   Open `AccuWeatherService.kt` (or your config file) and set your AccuWeather API key.
4. **Build & Run**:
   Open the project in Android Studio and run it on an emulator or physical device.

---

## 🔔 Testing Firebase Cloud Messaging (FCM)

1. Run the app on your device or emulator.
2. Open **Logcat** in Android Studio and filter by `WeatherWiseApp`.
3. Copy the **FCM Registration Token** printed in the log:
   ```text
   D/WeatherWiseApp: FCM REGISTRATION TOKEN (USE THIS IN FIREBASE CONSOLE): ...
   ```
4. In the **Firebase Console**, go to **Messaging > Send your first message / New campaign**.
5. Click **Send test message**, paste your token, and trigger a push notification.

---

## 📄 License

This project is licensed under the MIT License.
