package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateVirtualRequest : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var spinner: Spinner
    private lateinit var patientWords: EditText
    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_virtual_request)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        back = findViewById(R.id.BackButton)
        back.setOnClickListener {
            val intent = Intent(this, PatientDashboard::class.java)
            startActivity(intent)
        }

        patientWords = findViewById(R.id.patientWords)

        spinner = findViewById(R.id.spinner_medical)
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

        val sendRequest = findViewById<LinearLayout>(R.id.sendRequestLayout)
        sendRequest.setOnClickListener {
            val symptoms = patientWords.text.toString()
            val specialty = spinner.selectedItem.toString()

            val request = Treatment(
                patientUID = auth.currentUser?.uid,
                symptom = symptoms,
                specialty = specialty,
                accepted = false,
                active = true
            )
            val uid = database.reference.push().key
            if (uid != null) {
                database.reference.child("Treatment").child(uid).setValue(request)
                    .addOnSuccessListener {
                        database.reference.child("Treatment").child(uid).setValue(request)
                        val intent = Intent(this, PatientDashboard::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                    Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}