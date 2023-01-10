package com.example.medbuddy

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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

class DoctorInteraction : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDialog: Dialog
    private lateinit var mnDialog: Dialog
    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    private lateinit var patientFullName: TextView
    private lateinit var symptom: TextView

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.doctor_interaction)

        patientFullName = findViewById(R.id.patientTreatmentTitle)
        patientFullName.text = intent.getStringExtra("patientFullName")

        symptom = findViewById(R.id.symptom)
        symptom.text = intent.getStringExtra("symptom")

        findViewById<TextView>(R.id.medication).text = intent.getStringExtra("medication")
        findViewById<TextView>(R.id.diagnostic).text = intent.getStringExtra("diagnostic")

        findViewById<ImageView>(R.id.treatmentHistoryBackButton).setOnClickListener {
            val intent = Intent(this, DoctorDashboard::class.java)
            startActivity(intent)
        }

        val reminderButton = findViewById<LinearLayout>(R.id.layoutReminder)
        reminderButton.setOnClickListener {
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.pop_up_reminder_set)
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
                mnDialog.setContentView(R.layout.pop_up_reminder_hour)
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
                            showNotification(hours)
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
                mnDialog.show()
            }
            mDialog.show()
        }


        mDbRef = FirebaseDatabase.getInstance().reference
        val receiverUid = intent.getStringExtra("patientUID")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Do nothing
                }
            })

        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            if( message != "") {
                val messageObject = Message(message, senderUid)
                mDbRef.child("chats").child(senderRoom!!).child("messages").push(
                ).setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push(
                    ).setValue(messageObject)
                }
                messageBox.setText("")
            }
            else {
                // Do nothing
            }
        }

        findViewById<LinearLayout>(R.id.layoutEditMedication).setOnClickListener{
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.pop_up_edit_medication)
            mDialog.setTitle("Pop-up Window")
            mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            mDialog.show()

            mDialog.findViewById<LinearLayout>(R.id.layoutEditMedicationSave).setOnClickListener{
                val treatmentUID = intent.getStringExtra("treatmentUID")
                val userReference =
                    treatmentUID?.let { it1 -> mDbRef.child("Treatment").child(it1) }
                val updates = HashMap<String, Any>()
                updates["medication"] = mDialog.findViewById<EditText>(R.id.editMedication).text.toString()
                userReference?.updateChildren(updates)
                mDialog.dismiss()
                startActivity(Intent(this, DoctorDashboard::class.java))
            }

            mDialog.findViewById<ImageView>(R.id.backButtonMedication).setOnClickListener{
                mDialog.dismiss()
            }
        }

        findViewById<LinearLayout>(R.id.layoutEndMedication).setOnClickListener {
            val treatmentUID = intent.getStringExtra("treatmentUID")
            val userReference =
                treatmentUID?.let { it1 -> mDbRef.child("Treatment").child(it1) }
            val updates = HashMap<String, Any>()
            updates["active"] = false
            userReference?.updateChildren(updates)
        }
    }

    private fun showNotification(hours: Int) {
        createNotificationChannel(hours)
        val date = Date()
        val notificationId = SimpleDateFormat("ddHHmmss", Locale.US).format(date).toInt()
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_medication)
        notificationBuilder.setContentTitle("Medication")
        notificationBuilder.setContentText("Short description")
        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel(hours: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My medication"
            val description = "Medication channel"
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