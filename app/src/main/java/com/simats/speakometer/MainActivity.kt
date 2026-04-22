package com.simats.speakometer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.simats.speakometer.ui.theme.SpeakometerFrontendTheme

class MainActivity : ComponentActivity() {

    private val channelId = "premium_alerts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup the Notification Channel immediately on app launch
        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            SpeakometerFrontendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(name = "User")

                        Spacer(modifier = Modifier.height(20.dp))

                        // 2. A button to test the notification trigger
                        Button(onClick = { showExpiryNotification() }) {
                            Text("Simulate Expiry Notification")
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a notification channel (Required for Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Subscription Alerts"
            val descriptionText = "Notifications regarding your premium status and expiry"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Logic to trigger the notification that leads to PremiumExpiryActivity
     */
    private fun showExpiryNotification() {
        // Intent that tells the system which Activity to open
        val intent = Intent(this, PremiumExpiryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent wraps the intent so the system can fire it later
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification appearance
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Replace with your ic_alert_triangle_pink
            .setContentTitle("Premium Expiring Soon!")
            .setContentText("Your plan expires in 30 days. Click to see what you'll lose.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Attach the click action
            .setAutoCancel(true)            // Remove notification after click

        // Show the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder.build())
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Welcome to Speakometer, $name!")
}