package com.example.medbuddy

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DoctorDashboard : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.doctor_dashboard)

        title = findViewById(R.id.doctorDashboardTitle)
        title.text = intent.getStringExtra("fullName")
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        treatmentList = ArrayList()
        adapter = DoctorTreatmentAdapter(this, treatmentList)
        userRecyclerView = findViewById(R.id.treatmentRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        val settingsButton = findViewById<ImageView>(R.id.settingsDoctor)

        settingsButton.setOnClickListener {
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.popup_settings)
            mDialog.setTitle("Pop-up Window")
            mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val logoutButton = mDialog.findViewById<Button>(R.id.btnLogout)
            logoutButton.setOnClickListener {
                startActivity(Intent(this, Login::class.java))

                finish()

                mDialog.dismiss()
            }
            val editProfileButton = mDialog.findViewById<Button>(R.id.btnEditProfile)
            editProfileButton.setOnClickListener {
                mDialog.dismiss()

                mnDialog = Dialog(this)
                mnDialog.setContentView(R.layout.edit_profile_doctor)
                mnDialog.setTitle("Pop-up Window")
                mnDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mnDialog.window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val fullName = mnDialog.findViewById<TextInputLayout>(R.id.editfullName)
                val phoneNumber = mnDialog.findViewById<TextInputLayout>(R.id.editphoneNumber)
                val editData = mnDialog.findViewById<Button>(R.id.editData)
                val spinner = mnDialog.findViewById<Spinner>(R.id.editspinner)
                val adapter = ArrayAdapter.createFromResource(
                    this, R.array.medical_specialties, android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
                editData.setOnClickListener {
                    val sdFullName = fullName.editText?.text.toString()
                    val sdSpeciality = spinner.selectedItem.toString()
                    val sdPhoneNumber = phoneNumber.editText?.text.toString()
                    if (sdFullName.isEmpty() || sdPhoneNumber.isEmpty()) {
                        if (sdFullName.isEmpty()) {
                            fullName.error = "Please enter your Full Name"
                        }
                        if (sdPhoneNumber.isEmpty()) {
                            phoneNumber.error = "Please enter your phone number"
                        }
                        Toast.makeText(this, "Please check your fields", Toast.LENGTH_SHORT).show()
                    } else if (sdPhoneNumber.length != 10) {
                        phoneNumber.error = "Please enter 10 digits"
                        Toast.makeText(this, "Please enter 10 digits ", Toast.LENGTH_SHORT).show()
                    } else {
                        val userReference =
                            database.reference.child("Users").child(auth.currentUser!!.uid)
                        val updates = HashMap<String, Any>()
                        updates["fullName"] = sdFullName
                        updates["phoneNumber"] = sdPhoneNumber
                        updates["speciality"] = sdSpeciality
                        userReference.updateChildren(updates)
                    }
                }

                mnDialog.show()
            }
            mDialog.show()
        }
        val reminderButton = findViewById<ImageView>(R.id.patientsHistory)
        reminderButton.setOnClickListener {
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.edit_reminder)
            mDialog.setTitle("Pop-up Window")
            mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val setFixedReminder = mDialog.findViewById<Button>(R.id.setFixedReminder)
            setFixedReminder.setOnClickListener {
                showNotification(0)

                mDialog.dismiss()
            }
            val setRepetitiveReminder = mDialog.findViewById<Button>(R.id.setRepetitiveReminder)
            setRepetitiveReminder.setOnClickListener {
                mDialog.dismiss()

                mnDialog = Dialog(this)
                mnDialog.setContentView(R.layout.set_hours)
                mnDialog.setTitle("Pop-up Window")
                mnDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mnDialog.window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )


                val saveReminder = mnDialog.findViewById<Button>(R.id.saveReminder)
                val hoursTextInput = mnDialog.findViewById<TextInputLayout>(R.id.bookingHours)
                saveReminder.setOnClickListener {

                    val hoursString = hoursTextInput.editText!!.text.toString()
                    if (hoursString.isNotEmpty()) {


                        val hours = hoursString.toInt()
                        try {
                            //setAlarm(hours)
                            showNotification(hours)

//                            val notificationIntent = Intent(this, Reminder::class.java)
//                            val pendingIntent = PendingIntent.getActivity(
//                                this,
//                                0,
//                                notificationIntent,
//                                PendingIntent.FLAG_IMMUTABLE
//                            )
////                            builder = NotificationCompat.Builder(this, channelId)
////                                .setContentTitle("Notification")
////                                .setContentText("This is a test notification")
////                                .setSmallIcon(R.drawable.ic_launcher_foreground)
////                                .setContentIntent(pendingIntent)
////                                .setAutoCancel(true)
//                            val notificationTime = TimeUnit.HOURS.toMillis(hours)
//
//                            // Get the AlarmManager and schedule the notification
//                            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
//
                            mnDialog.dismiss()
                        } catch (e: NumberFormatException) {
                            // Display an error message to the user if the string cannot be converted to an integer
                            Toast.makeText(
                                this,
                                "Please enter a valid number of hours",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Display an error message to the user if the string is empty
                        Toast.makeText(this, "Please enter a number of hours", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
//
                mnDialog.show()

            }
            mDialog.show()


        }
        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if ((mAuth.currentUser?.uid == treatment.doctorUID) &&
                            (treatment.accepted == true) && (treatment.active == true)
                        ) {
                            treatmentList.add(treatment)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


//    fun scheduleNotification(context: Context, hours: Int, patientId: String) {
//        // Create an intent to open the app when the notification is clicked
//        val intent = Intent(context, MyReceiver::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        // Use the NotificationCompat library to build the notification
//        val builder = NotificationCompat.Builder(context, patientId)
//            .setSmallIcon(R.drawable.ic_medication)
//            .setContentTitle("Medication Reminder")
//            .setContentText("It's time to take your medication!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        // Use the AlarmManager to schedule the notification to be shown after the specified number of hours
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val notificationTime = SystemClock.elapsedRealtime() + (hours * 3600 * 1000)
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, notificationTime, pendingIntent)
//    }


    fun showNotification(hours: Int) {
        createNotificationChannel(hours)

            val date = Date()
            val notificationId = SimpleDateFormat("ddHHmmss", Locale.US).format(date).toInt()
            val notificationBuilder = NotificationCompat.Builder(this, "$CHANNEL_ID")
            notificationBuilder.setSmallIcon(R.drawable.ic_medication)
            notificationBuilder.setContentTitle("Medicamentatie")
            notificationBuilder.setContentText("Mica descriere")
            notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            notificationManagerCompat.notify(notificationId, notificationBuilder.build())

    }

    private fun createNotificationChannel(hours: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val name: CharSequence = "Medicamentatia mea"
                val description = "Channel ul pentru medicamentatie"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
                notificationChannel.description = description
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val currentTime = Calendar.getInstance().timeInMillis
                val triggerTime =
                    currentTime + (hours * 60 * 60 * 1000) // hours * minutes * seconds * milliseconds

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_HOUR,
                    pendingIntent
                )


        }
    }

}


