package com.st10397576.sanewshub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * MyFirebaseMessagingService handles incoming push notifications from Firebase Cloud Messaging.
 * It processes messages received when the app is in the foreground or background.
 *
 * Key Features:
 * - Receives push notifications from FCM
 * - Creates notification channels for Android 8.0+
 * - Displays notifications with custom content
 * - Handles notification clicks to open the app
 *
 * Reference: Firebase Cloud Messaging
 * https://firebase.google.com/docs/cloud-messaging/android/client
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    /**
     * Called when a new FCM token is generated.
     * This happens on app install, device restore, or token refresh.
     *
     * You should send this token to your backend server to enable
     * sending targeted notifications to this specific device.
     *
     * @param token The new FCM registration token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token generated: $token")

        // TODO: Send token to your backend server
        // This allows your server to send push notifications to this device
        sendTokenToServer(token)

        // Save token locally for debugging
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }

    /**
     * Called when a message is received from FCM.
     * Handles both notification and data messages.
     *
     * @param remoteMessage The message received from FCM
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Check if message contains a notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification title: ${notification.title}")
            Log.d(TAG, "Notification body: ${notification.body}")

            // Display the notification
            sendNotification(
                title = notification.title ?: "SA NewsHub",
                body = notification.body ?: "You have a new update"
            )
        }

        // Check if message contains a data payload
        // Data messages are used for custom processing
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Handle custom data here
            handleDataMessage(remoteMessage.data)
        }
    }

    /**
     * Handles custom data messages from FCM.
     * You can use this to trigger specific actions based on the data.
     *
     * @param data Map containing custom key-value pairs
     */
    private fun handleDataMessage(data: Map<String, String>) {
        // Example: Trigger a sync if data contains "sync" key
        if (data.containsKey("sync") && data["sync"] == "true") {
            Log.d(TAG, "Sync requested via push notification")
            // Trigger news refresh
            // You can use WorkManager or directly call repository here
        }

        // Example: Show custom notification based on data
        val title = data["title"] ?: "SA NewsHub"
        val message = data["message"] ?: "New update available"
        sendNotification(title, message)
    }

    /**
     * Displays a notification to the user.
     * Creates a notification channel for Android 8.0+ devices.
     *
     * @param title Notification title
     * @param body Notification message body
     */
    private fun sendNotification(title: String, body: String) {
        // Create an intent to open HomeActivity when notification is clicked
        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "news_notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to create this icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true) // Dismiss notification when clicked
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Show full text

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "News Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new news articles and updates"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())
        Log.d(TAG, "Notification displayed: $title")
    }

    /**
     * Sends the FCM token to your backend server.
     * This allows your server to send push notifications to this device.
     *
     * @param token The FCM registration token
     */
    private fun sendTokenToServer(token: String) {
        Log.d(TAG, "Sending token to server: $token")

        // TODO: Implement API call to send token to your backend
        // Example:
        // CoroutineScope(Dispatchers.IO).launch {
        //     try {
        //         ApiHelper.apiService.registerFcmToken(token)
        //         Log.d(TAG, "Token registered successfully")
        //     } catch (e: Exception) {
        //         Log.e(TAG, "Failed to register token: ${e.message}")
        //     }
        // }

        // For now, just log it
        Log.d(TAG, "Token saved locally. Implement server registration in production.")
    }
}