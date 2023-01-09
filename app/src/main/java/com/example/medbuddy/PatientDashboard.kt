package com.example.medbuddy

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientDashboard : AppCompatActivity() {
    private lateinit var treatmentRecyclerView: RecyclerView
    private lateinit var treatmentList: ArrayList<Treatment>
    private lateinit var adapter: PatientTreatmentAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private lateinit var title: TextView
    private lateinit var needDoctor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.patient_dashboard)

        title = findViewById(R.id.patientDashboardTitle)
        title.text = intent.getStringExtra("fullName")

        needDoctor = findViewById(R.id.layoutNeedDoctor)
        needDoctor.setOnClickListener {
            val intent = Intent(this, CreateVirtualRequest::class.java)
            startActivity(intent)
        }

        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        treatmentList = ArrayList()
        adapter = PatientTreatmentAdapter(this, treatmentList)
        treatmentRecyclerView = findViewById(R.id.treatmentRecyclerView)
        treatmentRecyclerView.layoutManager = LinearLayoutManager(this)
        treatmentRecyclerView.adapter = adapter
        mDbRef.child("Treatment").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                treatmentList.clear()
                for (postSnapshot in snapshot.children) {
                    val treatment = postSnapshot.getValue(Treatment::class.java)
                    if (treatment != null) {
                        if ((mAuth.currentUser?.uid == treatment.patientUID) &&
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
}