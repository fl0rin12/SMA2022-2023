package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RespondVirtualRequest : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var accept: LinearLayout
    private lateinit var decline: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setContentView(R.layout.respond_virtual_request)

        findViewById<ImageView>(R.id.BackButton).setOnClickListener {
            val intent = Intent(this, DoctorDashboard::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.requestPatientName).text = intent.getStringExtra("patientFullName")
        findViewById<TextView>(R.id.patientAgeData).text = intent.getStringExtra("age")
        findViewById<TextView>(R.id.patientGenderData).text = intent.getStringExtra("gender")
        findViewById<TextView>(R.id.patientWeightData).text = intent.getStringExtra("weight")
        findViewById<TextView>(R.id.patientWords).text = intent.getStringExtra("symptom")

        val treatmentUid = intent.getStringExtra("uid").toString()

        accept = findViewById(R.id.layoutAcceptRequest)
        accept.setOnClickListener {
            val diagnostic = findViewById<EditText>(R.id.givenDiagnostic).text.toString()
            val medication = findViewById<EditText>(R.id.givenMedication).text.toString()
            if (diagnostic.isEmpty() || medication.isEmpty()) {
                Toast.makeText(this, "Please don't leave the fields empty!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val userReference =
                    treatmentUid?.let { it1 -> database.child("Treatment").child(it1) }
                val updates = HashMap<String, Any>()
                updates["diagnostic"] = diagnostic
                updates["medication"] = medication
                updates["doctorUID"] = auth.currentUser?.uid.toString()
                updates["accepted"] = true
                userReference?.updateChildren(updates)
            }
        }

        decline = findViewById(R.id.layoutDeclineRequest)
        decline.setOnClickListener {
            val userReference =
                treatmentUid?.let { it1 -> database.child("Treatment").child(it1) }
            val updates = HashMap<String, Any>()
            updates["active"] = false
            userReference?.updateChildren(updates)
        }
    }
}