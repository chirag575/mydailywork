package com.example.alarmmmm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmId = intent.getIntExtra("ALARM_ID", -1)

        notificationManager.cancel(alarmId)

        when (intent.action) {
            "SNOOZE" -> {
                val snoozeTime = 5 * 60 * 1000 // 5 minutes
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val nextAlarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("ALARM_ID", alarmId)
                    val time = LocalTime.now().plusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm"))
                    putExtra("ALARM_TIME", time)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, alarmId, nextAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + snoozeTime,
                    pendingIntent
                )
            }
            "DISMISS" -> {
                // Just cancel notification, which is done above
            }
        }
    }
}
