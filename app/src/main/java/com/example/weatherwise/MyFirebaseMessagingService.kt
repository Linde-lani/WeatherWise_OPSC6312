package com.example.weatherwise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFCMService"
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)

        // Fetch Firebase Installation ID on token refresh
        FirebaseInstallations.getInstance().getId().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { installationId ->
                    syncInstallationId(installationId)
                }
            }
        }
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "Sending token to server: $token")
    }

    /**
     * Handles app registration and synchronization with the Firebase Installation ID (FID).
     */
    fun syncInstallationId(installationId: String) {
        Log.d(TAG, "Registered installation ID: $installationId")

        // Send the Firebase Installation ID to your app server.
        sendRegistrationToServer(token = installationId)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Get notification title and body
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "New Alert"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["message"] ?: ""

        // Trigger Android OS System Notification
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "realtime_alerts_channel"
        val notificationId = System.currentTimeMillis().toInt()

        // Intent to open NotificationsScreen when tap on notification banner
        val intent = Intent(this, NotificationsScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android O (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Realtime Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, builder.build())
    }
}
