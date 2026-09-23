# WeatherWise

WeatherWise is a modern Android weather tracking application designed to provide accurate real-time forecasts, customizable settings, multiple localization profiles, and reliable offline tracking.

## External Libraries and APIs Referenced

- **AccuWeather API**: Used as the primary data provider for location tracking, current meteorological data, 12-hour hourly forecasts, and 5-day comprehensive summaries.
- **Retrofit (v2.11.0)**: Used as the type-safe HTTP client to query the AccuWeather REST endpoints efficiently with automatic asynchronous request queuing.
- **Gson Converter (v2.11.0)**: Integrated seamlessly with Retrofit to handle deserialization of complex JSON response trees directly into typed Kotlin data classes.
- **Firebase**: Positioned as the identity management provider to authenticate users securely and handle cloud configurations if synchronization is triggered.
- **Room Persistence**: Used to store persistent locally cached weather entities and offline structures for fluid background access.

## Architecture and Localization

- **Multilingual Local Resources**: Comprehensive localization frameworks mapping English (`values/strings.xml`), isiZulu (`values-zu/strings.xml`), and Sesotho (`values-st/strings.xml`) keys dynamically across widgets.
- **State Management & Shared Preferences**: Safe configuration stores managing localized defaults, primary locations, and quick delete mutations.
-  **YouTube link: https://youtu.be/Nubh8U7oDGE?si=mGugTuphXdnR7M8Y 
