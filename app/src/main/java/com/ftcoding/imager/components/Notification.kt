package com.ftcoding.imager.components

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ftcoding.imager.R

class Notification {


    private val channelId = "Download_image"
    private val notificationId = 1
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    fun showDownloadProgressNotification(
        context: Context,
        activity: Activity,
        max: Int,
        progress: Int
    ) {
        builder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle("Download...")
            setContentText("0 %")
            priority = NotificationCompat.PRIORITY_DEFAULT
            setProgress(max, progress, true)
            setAutoCancel(true)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test Channel"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)

            notificationManager =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(
        max: Int,
        progress: Int
    ) {
        builder.setProgress(max, progress, false)
        notificationManager.notify(1, builder.build())
    }

//        NotificationManagerCompat.from(context).apply {
//            builder.setProgress(0, 0, true)
//            notify(NOTIFICATION_ID, builder.build())
//
//
//        }

}