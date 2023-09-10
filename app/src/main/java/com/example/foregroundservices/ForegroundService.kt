package com.example.foregroundservices

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.lang.UnsupportedOperationException

class ForegroundService: Service() {

    private lateinit var notificationManager: NotificationManager

    private var isStarted = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            makeForeground()
            isStarted = true
        }
        return START_NOT_STICKY
    }

    private fun makeForeground() {

        createServiceNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Title")
            .setContentText("Foreground Service content text")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"

        fun startService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(intent)
            } else {
                context.startForegroundService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.stopService(intent)
        }
    }
}