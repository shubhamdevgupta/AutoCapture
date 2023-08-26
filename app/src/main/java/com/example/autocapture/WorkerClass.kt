package com.example.autocapture

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast

class WorkerClass : Service() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Toast.makeText(this, "created", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val handler = Handler()
        handler.postDelayed({
            Toast.makeText(this, "background task is running....", Toast.LENGTH_SHORT).show()
        }, 3000)


        /*     val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
                 .setContentTitle("Background Notification")
                 .setContentText("Your app is running in the background.")
                 .setSmallIcon(R.drawable.ic_launcher_background)
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT)

             val notificationManager = NotificationManagerCompat.from(this)
             if (ActivityCompat.checkSelfPermission(
                     this,
                     Manifest.permission.POST_NOTIFICATIONS
                 ) != PackageManager.PERMISSION_GRANTED
             ) {
                 notificationManager.notify(1, notificationBuilder.build())
             }

             stopSelf() // Stop the service after displaying the notification
     */
        return START_NOT_STICKY
    }
}