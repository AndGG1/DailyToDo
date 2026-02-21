package com.example.myapplication.NotificationSystem

import android.R
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("textExtra").toString()
        val title = intent.getStringExtra("titleExtra").toString()
        val notif_id = intent.getStringExtra("notificationIdExtra").toString()

        val notification = NotificationCompat.Builder(context, "ch_1")
            .setSmallIcon(R.drawable.alert_light_frame)
            .setContentText(message)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notif_id.toInt(), notification)
    }
}
