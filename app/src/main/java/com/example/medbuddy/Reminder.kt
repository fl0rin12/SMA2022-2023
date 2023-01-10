package com.example.medbuddy

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Reminder : BroadcastReceiver() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: DoctorTreatmentAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var title: TextView
    private lateinit var mDialog: Dialog
    private lateinit var mnDialog: Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: NotificationCompat.Builder

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    private val description = "Test Notification Channel"
    private val notificationId = 1

    override fun onReceive(context: Context, intent: Intent) {

    }

}