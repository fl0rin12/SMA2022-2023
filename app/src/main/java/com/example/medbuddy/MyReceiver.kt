//package com.example.medbuddy
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.core.content.ContextCompat.getSystemService
//
//class MyReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//      //  val notificationManager = NotificationManagerCompat.from(context)
//
//        // Build the notification
//        val channel = NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_HIGH)
//        channel.description = "This is my notification channel"
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_medication)
//            .setContentTitle("My Notification")
//            .setContentText("This is my notification")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        // Display the notification
//        notificationManager.notify(NOTIFICATION_ID, builder.build())
//    }
//}
