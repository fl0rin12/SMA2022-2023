package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientHistory : AppCompatActivity() {

    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: PatientHistoryAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var back: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.patient_history)

        back = findViewById(R.id.treatmentHistoryBackButton)
        back.setOnClickListener {
            val intent = Intent(this, PatientDashboard::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        treatmentList = ArrayList()
        adapter = PatientHistoryAdapter(this, treatmentList)
        treatmentRecyclerView = findViewById(R.id.treatmentHistoryRecyclerView)
        treatmentRecyclerView.layoutManager = LinearLayoutManager(this)
        treatmentRecyclerView.adapter = adapter
        database.child("Treatment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if ((auth.currentUser?.uid == treatment.patientUID) &&
                            (treatment.accepted == true) && (treatment.active == false)
                        ) {
                            treatmentList.add(treatment)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
}